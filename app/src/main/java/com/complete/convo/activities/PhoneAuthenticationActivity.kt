package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.complete.convo.R
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.databinding.ActivityPhoneAuthenticationBinding
import com.complete.convo.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class PhoneAuthenticationActivity : AppCompatActivity() {

    private var _binding : ActivityPhoneAuthenticationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var db : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhoneAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()


        val login = binding.loginBtn


        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        login.setOnClickListener {
            binding.verify.isEnabled = true
            binding.otp.isEnabled = true
            binding.loginBtn.isEnabled = false
            binding.phoneNumber.isEnabled = false
            login()
        }
        binding.verify.setOnClickListener{
            val otp=binding.otp.text.toString().trim()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()
            }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.verify.isEnabled = false
                binding.otp.isEnabled = false
                binding.loginBtn.isEnabled = true
                binding.phoneNumber.isEnabled = true
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                Log.d("taget",e.toString())
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                val mobileNumber = binding.phoneNumber.text.toString()
            }
        }

    }
    private fun addUserToDB(name: String, phno :Int, uid: String?) {

        db = FirebaseDatabase.getInstance("https://basic-chat-application-4d671-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference()
        db.child("user").child(uid!!).setValue(User(name,phno,uid))

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = findViewById<EditText>(R.id.name).toString()
                    val phno = intent.getStringArrayExtra("phone_number") as Int
                    addUserToDB(name,phno, mAuth.currentUser?.uid)
                    val intent = Intent(this,LoginActivity::class.java)
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
    }

    private fun login() {
        val mobileNumber = findViewById<EditText>(R.id.phoneNumber)
        var number = mobileNumber.text.toString().trim()

        if (number.isNotEmpty()) {
            number = "+91$number"
            sendVerificationCode(number)

        } else {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
            binding.verify.isEnabled = false
            binding.otp.isEnabled = false
            binding.loginBtn.isEnabled = true
            binding.phoneNumber.isEnabled = true
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}