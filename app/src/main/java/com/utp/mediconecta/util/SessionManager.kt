package com.utp.mediconecta.util

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("mediconecta_session", Context.MODE_PRIVATE)

    val isLoggedIn: Boolean get() = prefs.getBoolean(KEY_LOGGED_IN, false)
    val userId: Long get() = prefs.getLong(KEY_USER_ID, -1L)
    val userName: String get() = prefs.getString(KEY_USER_NAME, "") ?: ""
    val userEmail: String get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun saveSession(id: Long, name: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putLong(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
