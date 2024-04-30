package com.agritracker.plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/// Root of all AppCompatActivities where all general stuff goes
class RootActivity : AppCompatActivity() {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get firebase auth instance
        auth = FirebaseAuth.getInstance()

        //get current user
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        user?.let {
            currentUserId = it.uid
        }

        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(Intent(this@RootActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        authListener?.let { auth.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        authListener?.let { auth.removeAuthStateListener(it) }
    }
}


