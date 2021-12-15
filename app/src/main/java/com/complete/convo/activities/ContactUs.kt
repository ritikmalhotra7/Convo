package com.complete.convo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.complete.convo.R
import com.complete.convo.databinding.ActivityContactUsBinding
import com.complete.convo.databinding.ActivityMainBinding

class ContactUs : AppCompatActivity() {
    private var _binding : ActivityContactUsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var actionBar = supportActionBar
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)
        binding.emailabhi.setOnClickListener {
            Toast.makeText(this,"abhishek",Toast.LENGTH_SHORT).show()
        }

    }
}