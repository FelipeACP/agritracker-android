package com.agritracker.plus

import com.agritracker.plus.LoginActivity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.agritracker.plus.R
import com.agritracker.plus.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btn_register)
        btnBack = findViewById(R.id.btn_back)
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.password)
        inputConfirmPassword = findViewById(R.id.confirm_password)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val emailInput = inputEmail.text.toString().trim()
            val codedEmail: String
            val password = inputPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            if (TextUtils.isEmpty(emailInput)) {
                Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(emailInput) || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                Toast.makeText(applicationContext, "Enter a valid email address!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(applicationContext, "Password and confirm password don't match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(this@RegisterActivity, OnCompleteListener<AuthResult> { task ->
                    Toast.makeText(this@RegisterActivity, "createUserWithEmail:onComplete:" + task.isSuccessful, Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE

                    if (!task.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Authentication failed." + task.exception,
                            Toast.LENGTH_SHORT).show()
                    } else {
                        val user = auth.currentUser
                        val uid = user!!.uid
                        val email = user.email
                        //val userRef = FirebaseDatabase.getInstance().getReference("users")
                        //val newUser = User(uid, email)
                        //userRef.child(uid).setValue(newUser)
                        Toast.makeText(this@RegisterActivity, "User created with email: $email", Toast.LENGTH_SHORT)
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                })


        }
    }
    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }
}


