package com.dicoding.aplikasistoryapppt2.data

import android.content.Context

internal class UserPreference (context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(USER_ID, value.userId)
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)

        editor.apply()
    }

    fun getUser(): UserModel {
        val model = UserModel()
        model.userId = preferences.getString(USER_ID, "")
        model.name = preferences.getString(NAME, "")
        model.token = preferences.getString(TOKEN, "")

        return model
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USER_ID = "user_id"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }
}