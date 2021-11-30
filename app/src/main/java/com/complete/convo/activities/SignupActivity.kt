package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.complete.convo.databinding.ActivitySignupBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private var _binding : ActivitySignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.signupbutton.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.emailid.text.toString()
            val password = binding.password.text.toString()

            signUp(name,email,password)
        }
    }
    private fun signUp(name:String, email: String, password: String){
        if(!TextUtils.isEmpty(binding.emailid.text.toString()) && !TextUtils.isEmpty(binding.password.text.toString())){
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        addUserToDB(name,email, mAuth.currentUser?.uid)
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Welcome $name", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("taget",task.exception.toString())
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }else{
            Toast.makeText(this , "Please Enter Something", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addUserToDB(name: String, email: String, uid: String?) {

        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference()
        dbReference.child("user").child(uid!!).setValue(User(name,email,uid))

    }
}