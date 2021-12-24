package com.complete.convo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.complete.convo.R
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}