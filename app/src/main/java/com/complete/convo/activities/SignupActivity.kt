package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.complete.convo.databinding.ActivitySignupBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private  var _binding : ActivitySignupBinding? = null
    private  val binding get() = _binding!!
    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        binding.signupbutton.setOnClickListener {
            viaEmail()

            binding.name.isEnabled = false
            binding.password.isEnabled = false
            binding.emailid.isEnabled = false
        }
    }
    private fun viaEmail() {
        val name = binding.name.text.toString()
        val email = binding.emailid.text.toString()
        val password = binding.password.text.toString()
        if(!TextUtils.isEmpty(binding.emailid.text.toString()) && !TextUtils.isEmpty(binding.password.text.toString())){
            binding.progress.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        checkEmail()
                    } else {
                        Log.d("taget",task.exception.toString())
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.INVISIBLE
                    }
                }.addOnFailureListener {
                    binding.name.isEnabled = true
                    binding.password.isEnabled = true
                    binding.emailid.isEnabled = true
                    binding.progress.visibility = View.INVISIBLE
                }
        }else{
            Toast.makeText(this , "Please Enter Something", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addUserToDbViaEmail(name : String, email :String, uid : String) {
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        dbReference.child("user").child(uid).setValue(User(name,email,uid))
    }
    private fun checkEmail(){
        val firebaseUser = mAuth?.currentUser
        val name = binding.name.text.toString().trim()
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Verification mail sent",Toast.LENGTH_SHORT).show()
                mAuth?.signOut()
                finish()
                val intent = Intent(this,LoginActivity::class.java)
                intent.putExtra("name",name)
                startActivity(intent)
                binding.progress.visibility = View.INVISIBLE

            }else{
                Toast.makeText(this,"error occured",Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.INVISIBLE
            }

        }
    }
}
