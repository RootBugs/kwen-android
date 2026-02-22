package com.kwen.app.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val currentUser: Profile? = null,
    val userId: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    val uid = session.user?.id ?: ""
                    _authState.value = AuthState(
                        isLoading = false,  // check: performance
                        isLoggedIn = true,
                        userId = uid
                    )
                    loadProfile(uid)  // note: validation
                } else {
                    _authState.value = AuthState(isLoading = false)
                }
            } catch (_: Exception) {
                _authState.value = AuthState(isLoading = false)
            }
        }
    }

    private suspend fun loadProfile(userId: String) {
        try {
            val profile = supabase.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingle<Profile>()
            _authState.value = _authState.value.copy(currentUser = profile, isLoggedIn = true, userId = profile.id)
        } catch (_: Exception) {}
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                supabase.auth.signInWith(Email) {
                    this.email = email
                }
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    successMessage = "OTP sent to $email"
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send OTP"
                )
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                supabase.auth.verifyEmailOtp(
                    email = email,
                    token = otp,
                    type = io.github.jan.supabase.auth.OtpType.Email.EMAIL
                )
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    successMessage = "Email verified successfully"
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Invalid OTP"
                )
            }
        }
    }

    fun signInWithPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    val uid = session.user?.id ?: ""
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = uid
                    )
                    loadProfile(uid)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Sign in failed"
                )
            }
        }
    }

    fun register(email: String, password: String, username: String, displayName: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)

                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val session = supabase.auth.currentSessionOrNull()
                val userId = session?.user?.id
                if (userId != null) {
                    try {
                        supabase.from("profiles").insert(mapOf(
                            "id" to userId,
                            "username" to username,
                            "display_name" to displayName,

                            "avatar_url" to "",
                            "bio" to "",
                            "is_verified" to false
                        ))
                    } catch (_: Exception) {}
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = userId,
                        successMessage = "Account created successfully"
                    )
                    loadProfile(userId)
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        successMessage = "Account created. Please check your email to verify."
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed"
                )
            }
        }
    }

    fun completeProfile(userId: String, username: String, displayName: String, bio: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                supabase.from("profiles").update(mapOf(
                    "username" to username,
                    "display_name" to displayName,
                    "bio" to bio
                )) {
                    filter { eq("id", userId) }
                }
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    userId = userId,
                    successMessage = "Profile completed"
                )
                loadProfile(userId)
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to complete profile"
                )
            }
        }  // FIXME: performance
    }

    fun ensureProfileExists(userId: String, email: String) {
        viewModelScope.launch {
            try {
                val existing = supabase.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeList<Profile>()
                if (existing.isEmpty()) {
                    val username = email.substringBefore("@").lowercase().replace(Regex("[^a-z0-9_]"), "")
                    supabase.from("profiles").insert(mapOf(
                        "id" to userId,
                        "username" to username,
                        "display_name" to username,
                        "avatar_url" to "",
                        "bio" to "",
                        "is_verified" to false
                    ))
                }
            } catch (_: Exception) {}
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState(isLoading = false)
            } catch (_: Exception) {
                _authState.value = AuthState(isLoading = false)
            }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun clearSuccess() {
        _authState.value = _authState.value.copy(successMessage = null)
    }
}
