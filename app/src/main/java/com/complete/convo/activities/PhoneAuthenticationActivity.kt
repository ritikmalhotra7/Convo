package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Toast

import com.complete.convo.databinding.ActivityPhoneAuthenticationBinding
import com.complete.convo.model.User

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*

import java.util.concurrent.TimeUnit
import com.google.firebase.auth.AuthResult

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import java.lang.Exception


class PhoneAuthenticationActivity : AppCompatActivity() {

    private var _binding : ActivityPhoneAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId: String
    private lateinit var db : DatabaseReference
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

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
                    storedVerificationId, otp)
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
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val phNo = binding.phoneNumber.text.toString().trim()
                    try {
                        val completeListener: OnCompleteListener<AuthResult> =
                            OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val isNew = task.result?.getAdditionalUserInfo()?.isNewUser()
                                    Log.d(
                                        "MyTAG",
                                        "onComplete: " + if (isNew!!) "new user" else "old user"
                                    )
                                }
                            }
                    }catch(e:Exception){
                        Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this, "Welcome $phNo", Toast.LENGTH_SHORT).show()
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
        var number = binding.phoneNumber.text.toString().trim()

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
    private fun addUserToDB(name: String, phone: String, uid: String?) {

        db = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        db.child("user").child(name).setValue(User(name,phone,uid,phone))

    }
    /*private fun addUserToDB(name: String, email: String, uid: String?) {

        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        dbReference.child("user").child(name).setValue(User(name,email,uid))

    }*/

}