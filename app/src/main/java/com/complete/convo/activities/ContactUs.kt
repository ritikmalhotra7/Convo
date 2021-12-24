package com.complete.convo.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        binding.ritik.setOnClickListener{
            copyToClipboard("ritik","ritikrkmalhotra@gmail.com")
            toast()
        }
        binding.abhishek.setOnClickListener{
            copyToClipboard("abhishek","gairolabhi80@gmail.com")
            toast()
        }
        binding.nupur.setOnClickListener{
            copyToClipboard("nupur","nupuraggarwal1502@gmail.com")
            toast()
        }
        binding.prateek.setOnClickListener{
            copyToClipboard("prateek","aroraprateek700@gmail.com")
            toast()
        }
    }
    fun Context.copyToClipboard(clipLabel: String, text: CharSequence){
        val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText(clipLabel, text))
    }
    fun toast(){
        Toast.makeText(this,"Credentials Copied",Toast.LENGTH_SHORT).show()
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}