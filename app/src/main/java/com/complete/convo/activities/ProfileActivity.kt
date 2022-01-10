package com.complete.convo.activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.complete.convo.R
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var storage : FirebaseStorage
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("name")
        val emailorphone = intent.getStringExtra("emailorphone")
        val uid = intent.getStringExtra("uid")
        binding.nameprofile.text = name.toString()
        binding.detailprofile.text = emailorphone.toString()

        binding.fabprofile.setOnClickListener {
            val intent = Intent(this,ChatActivity::class.java)

            intent.putExtra("name",name)
            intent.putExtra("emailorphone",emailorphone)
            intent.putExtra("uid",uid)
            finish()
            startActivity(intent)
        }
        storage = FirebaseStorage.getInstance()
        val storagRef = storage.reference.child(uid!!).child(uid+"profile")
        val localFile = File.createTempFile("temp","jpg")
        storagRef.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageprofile.setImageBitmap(bitMap)
        }
        val actionBar = supportActionBar
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)
    }
}