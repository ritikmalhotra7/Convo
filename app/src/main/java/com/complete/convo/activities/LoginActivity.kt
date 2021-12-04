package com.complete.convo.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.complete.convo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
           val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            val email = binding.emailid.text.toString()
            val password = binding.password.text.toString()

            login(email,password)
        }
        binding.loginPhone.setOnClickListener {
            startActivity(Intent(this,PhoneAuthenticationActivity::class.java))
            finish()
        }
    }

    private fun login(email: String, password: String) {
        if(!TextUtils.isEmpty(binding.emailid.text.toString()) && !TextUtils.isEmpty(binding.password.text.toString())){
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this,MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this , "Please Enter Something", Toast.LENGTH_SHORT).show()
        }
    }


   /* override fun onStart() {
        *//*super.onStart()*//*
        *//*val mAuth = FirebaseAuth.getInstance().currentUser
        if(mAuth != null){
            startActivity(Intent(this,MainActivity::class.java))
        }else{
            super.onStart()
        }*//*
        //create sharedPrefrences store uid when user login /sign up ,remove uid on logout

    }*/
}