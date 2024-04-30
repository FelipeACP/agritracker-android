package com.agritracker.plus

import android.content.Context
import android.content.SharedPreferences
import com.agritracker.plus.User

class UserLocalStorage(context: Context) {

    companion object {
        const val SP_NAME = "userDetails"
    }

    private val userLocalDatabase: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun storeUserData(user: User) {
        userLocalDatabase.edit().apply {
            putString("userUID", user.userUID)
            putString("firstName", user.firstName)
            putString("lastName", user.lastName)
            putString("email", user.email)
            putString("companyName", user.companyName)
            putString("companyUID", user.companyUID)
            putString("currentTaskUID", user.currentTaskUID)
            apply()
        }
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        userLocalDatabase.edit().apply {
            putBoolean("loggedIn", loggedIn)
            apply()
        }
    }

    fun setCurrentTaskUID(currentTaskUID: String) {
        userLocalDatabase.edit().apply {
            putString("currentTaskUID", currentTaskUID)
            apply()
        }
    }

    fun getCurrentTaskUID(): String {
        return userLocalDatabase.getString("currentTaskUID", "") ?: ""
    }

    fun setCurrentClientUID(currentClientUID: String) {
        userLocalDatabase.edit().apply {
            putString("currentClientUID", currentClientUID)
            apply()
        }
    }

    fun getCurrentClientUID(): String {
        return userLocalDatabase.getString("currentClientUID", "") ?: ""
    }

    fun clearUserData() {
        userLocalDatabase.edit().apply {
            clear()
            apply()
        }
    }

    fun getLoggedInUser(): User? {
        val userUID = userLocalDatabase.getString("userUID", "") ?: ""
        val firstName = userLocalDatabase.getString("firstName", "") ?: ""
        val lastName = userLocalDatabase.getString("lastName", "") ?: ""
        val companyName = userLocalDatabase.getString("companyName", "") ?: ""
        val companyUID = userLocalDatabase.getString("companyUID", "") ?: ""
        val currentTaskUID = userLocalDatabase.getString("currentTaskUID", "") ?: ""
        val email = userLocalDatabase.getString("email", "") ?: ""
        val status = userLocalDatabase.getString("status", "") ?: ""
        val avatarImage = userLocalDatabase.getString("avatarImage", "") ?: ""

        return User(userUID, firstName, lastName, companyName, companyUID, currentTaskUID, email, status)
    }
}


