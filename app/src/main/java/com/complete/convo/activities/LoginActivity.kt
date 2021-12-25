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
import com.complete.convo.actvities.MainActivity
import com.complete.convo.databinding.ActivityLoginBinding
import com.complete.convo.databinding.DialogProgressBinding
import com.complete.convo.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.royrodriguez.transitionbutton.TransitionButton
import com.royrodriguez.transitionbutton.TransitionButton.OnAnimationStopEndListener

class LoginActivity : AppCompatActivity(){

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private var vemail = false

    private lateinit var mProgressDialog: Dialog

    private lateinit var tb: TransitionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        tb = binding.loginButton
        tb.setOnClickListener {
            tb.startAnimation()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            verifyEmail(email, password)
        }
        binding.loginPhone.setOnClickListener {
            startActivity(Intent(this, PhoneAuthenticationActivity::class.java))
            finish()
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                tb.stopAnimation(
                    TransitionButton.StopAnimationStyle.EXPAND,
                    OnAnimationStopEndListener {
                        finish()
                        val intent = Intent(baseContext, MainActivity::class.java)
                        startActivity(intent)
                    })
            } else {
                Toast.makeText(this, " Authentication Failed", Toast.LENGTH_SHORT).show()
                tb.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance().currentUser
        if (mAuth != null) {
            startActivity(Intent(this, MainActivity::class.java))
            Log.d("taget", mAuth.uid)
        } else {
            super.onStart()
        }


    }

    private fun verifyEmail(email: String, password: String) {
        if (!TextUtils.isEmpty(binding.email.text.toString()) && !TextUtils.isEmpty(binding.password.text.toString())) {
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                vemail = mAuth.currentUser!!.isEmailVerified
                mAuth.signOut()
                Log.d("taget2", vemail.toString())
                if (vemail) {
                    login(email, password)
                } else {
                    tb.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                    Toast.makeText(this, "Please Verify", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                tb.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                Toast.makeText(this, " Authentication Failed", Toast.LENGTH_SHORT).show()
            }


        } else {
            tb.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
            Toast.makeText(this, "Please Enter Something", Toast.LENGTH_SHORT).show()
        }
    }
}
