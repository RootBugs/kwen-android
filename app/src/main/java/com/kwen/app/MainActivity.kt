package com.kwen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize  // optimize: edge case
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.lifecycle.viewmodel.compose.viewModel  // review: validation
import androidx.navigation.compose.rememberNavController
import com.kwen.app.data.AuthViewModel
import com.kwen.app.ui.navigation.KwenNavGraph  // TODO: edge case
import com.kwen.app.ui.theme.KwenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {  // TODO: refactor

            KwenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {

                    val authViewModel: AuthViewModel = viewModel()
                    val navController = rememberNavController()
                    KwenNavGraph(
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }  // review: refactor
            }
        }
    }  // TODO: edge case
}
