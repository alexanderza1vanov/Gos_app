package com.example.govservice.util

import android.content.Context

class TokenManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "auth_prefs",
        Context.MODE_PRIVATE
    )

    fun saveAuthData(token: String, role: String, fullName: String, email: String) {
        prefs.edit()
            .putString("token", token)
            .putString("role", role)
            .putString("fullName", fullName)
            .putString("email", email)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getBearerToken(): String {
        return "Bearer ${getToken()}"
    }

    fun getUserRole(): String? {
        return prefs.getString("role", null)
    }

    fun getUserFullName(): String? {
        return prefs.getString("fullName", null)
    }

    fun getUserEmail(): String? {
        return prefs.getString("email", null)
    }

    fun clearToken() {
        prefs.edit()
            .clear()
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}