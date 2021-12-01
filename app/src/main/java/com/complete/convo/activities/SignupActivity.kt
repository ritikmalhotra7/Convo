package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.complete.convo.databinding.ActivitySignupBinding
import com.complete.convo.fragments.OtpDialog
import com.complete.convo.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private var _binding : ActivitySignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var credential:PhoneAuthCredential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        val vale = intent.getIntExtra("code_",0)
        if(vale == 1){
            binding.emailid.isEnabled = false
        }else{
            binding.phoneNumber.isEnabled = false
        }



        binding.signupbutton.setOnClickListener {
            if(vale == 2){
                val name = binding.name.text.toString()
                val email = binding.emailid.text.toString()
                val password = binding.password.text.toString()

                signUp(name,email,password)
            }else{
                val name = binding.name.text.toString()
                val phoneNumber = binding.phoneNumber.text.toString()
                val password = binding.password.text.toString()

                var dialog = OtpDialog()
                dialog.show(supportFragmentManager,"fragment for otp!")
                var otp = intent.getStringExtra("otp")

                callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }

                    override fun onVerificationFailed(e: FirebaseException) {

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

                if(!otp.isNullOrEmpty()){
                    credential = PhoneAuthProvider.getCredential(
                        storedVerificationId, otp)
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this,"not valid",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = binding.name.text.toString()
                    val phno = binding.phoneNumber.text.toString().trim()
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
        dbReference.child("user").child(name).setValue(User(name,email,uid))

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