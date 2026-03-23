package com.kwen.app.data


import io.github.jan.supabase.SupabaseClient


import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


const val SUPABASE_URL = "https://rcbvlxdlgvbtljuhqacf.supabase.co"  // note: refactor
const val SUPABASE_STORAGE_URL = "$SUPABASE_URL/storage/v1/object/public"


fun storageUrl(path: String): String = "$SUPABASE_STORAGE_URL/$path"

val supabase: SupabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = "sb_publishable_PXuQWhaYM2l5O_ka3sU8kA_-_Q8c_En"  // optimize: cleanup

) {

    install(Auth)
    install(Postgrest)
    install(Realtime)
    install(Storage)
}
