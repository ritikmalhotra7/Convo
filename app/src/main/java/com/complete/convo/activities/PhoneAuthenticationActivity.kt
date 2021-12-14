package com.complete.convo.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

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
import androidx.appcompat.app.AlertDialog
import com.complete.convo.R
import com.complete.convo.databinding.DialogViewBinding

import com.google.android.gms.tasks.OnCompleteListener
import java.lang.Exception


class PhoneAuthenticationActivity : AppCompatActivity() {

    private var _binding : ActivityPhoneAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var name : String
    private lateinit var phone : String
    private lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId: String
    private lateinit var db : DatabaseReference
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mProgressDialog: Dialog
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.otp.isEnabled = false
        binding.verify.isEnabled = false
        login.setOnClickListener {
            binding.verify.isEnabled = true
            binding.otp.isEnabled = true
            binding.loginBtn.isEnabled = false
            binding.phoneNumber.isEnabled = false

            login()
            showProgressDialog()
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
                addUserToDB(name,phone,mAuth
                    .currentUser?.uid)
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
                hideProgressDialog()
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showProgressDialog()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val isNew = task.result?.getAdditionalUserInfo()?.isNewUser()
                    Log.d("taget", isNew.toString())
                    phone = binding.phoneNumber.text.toString().trim()
                    if(isNew == true) {
                        try{
                            val b = DialogViewBinding.inflate(layoutInflater)
                            /*val mDialogView = LayoutInflater.from(this).inflate(b.root, null)*/

                            val mBuilder = AlertDialog.Builder(this)
                                .setView(b.root)
                                .setTitle("Login Form")
                                .setNegativeButton("Cancel"
                                ) { dialog, which -> dialog?.cancel() }
                                .setPositiveButton("Continue"
                                ) { dialog, which ->
                                    name = b.yourname.text.toString().trim()
                                    addUserToDB(name, phone, mAuth.currentUser?.uid)
                                    val intent = Intent(
                                        this@PhoneAuthenticationActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@PhoneAuthenticationActivity,
                                        "Welcome $name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog?.dismiss()
                                }
                            hideProgressDialog()
                            mBuilder.show()
                        }catch (e:Exception){
                            Log.d("taget",e.toString())
                            hideProgressDialog()

                        }
                    }else{
                        binding.verify.isEnabled = false
                        binding.otp.isEnabled = false
                        binding.loginBtn.isEnabled = true
                        binding.phoneNumber.isEnabled = true
                        val intent = Intent(this@PhoneAuthenticationActivity,MainActivity::class.java)
                        startActivity(intent)
                        hideProgressDialog()
                    }



                } else {
                    Log.d("taget",task.exception.toString())
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    hideProgressDialog()
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
        db.child("user").child(uid!!).setValue(User(name,phone,uid,phone))

    }

    private fun showProgressDialog() {
        mProgressDialog = Dialog(this)


        mProgressDialog.setContentView(R.layout.dialog_progress)


        mProgressDialog.show()
    }


    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}