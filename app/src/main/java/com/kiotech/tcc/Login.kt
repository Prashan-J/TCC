package com.kiotech.tcc

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kiotech.tcc.databinding.ActivityLoginBinding
import com.kiotech.tcc.databinding.ActivityMainBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginbtn.setOnClickListener(){
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.forgotpass.text = "Please enter email and password";
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        binding.forgotpass.text = "signInWithEmail:success";
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        binding.forgotpass.text = "signInWithEmail:failure";
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                    }
                }
        }
    }
}