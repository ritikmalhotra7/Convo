package com.complete.convo.activities

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.complete.convo.R
import com.complete.convo.databinding.ActivityLoginBinding
import com.complete.convo.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference
    private var isVerified = false
    private var vemail = false

    private lateinit var mProgressDialog: Dialog

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
        setTheme(R.style.OverlayPrimaryColorRed)
    }

    private fun login(email: String, password: String) {
        if(!TextUtils.isEmpty(binding.emailid.text.toString()) && !TextUtils.isEmpty(binding.password.text.toString())){
            showProgressDialog()
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        verifyEmail(email)

                        hideProgressDialog()
                        finish()
                        startActivity(Intent(this,MainActivity::class.java))
                    } else {
                        Snackbar.make(binding.root,"seems like you have entered wrong inputs!",Snackbar.LENGTH_SHORT).show()
                        hideProgressDialog()
                    }
                }

        }else{
            Toast.makeText(this , "Please Enter Something", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance().currentUser
        if(mAuth != null){
            startActivity(Intent(this,MainActivity::class.java))
        }else{
            super.onStart()
        }


    }
    private fun verifyEmail(email: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val vemail = firebaseUser?.isEmailVerified
        if(vemail!!){
        }else{
            Toast.makeText(this,"please Verify",Toast.LENGTH_SHORT).show()
            mAuth.signOut()
        }
    }
    private fun addUserToDbViaEmail(name : String, email :String, uid : String) {
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        dbReference.child("user").child(email).setValue(User(name,email,uid))
    }

    fun showProgressDialog() {
        mProgressDialog = Dialog(this)


        mProgressDialog.setContentView(R.layout.dialog_progress)


        mProgressDialog.show()
    }


    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}