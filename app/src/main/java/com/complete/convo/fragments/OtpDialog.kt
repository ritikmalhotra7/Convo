package com.complete.convo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.complete.convo.activities.SignupActivity
import com.complete.convo.databinding.OtpDialogBinding
import com.complete.convo.databinding.SignupViaDialogBinding

class OtpDialog : DialogFragment() {
    private var _binding : OtpDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OtpDialogBinding.inflate(inflater,container,false)
        val otp = binding.otpinput.text.toString()
        binding.verifybutton.setOnClickListener {
            var intent = Intent(activity, SignupActivity::class.java)
            intent.putExtra("otp",otp)//for email
            startActivity(intent)
            dismiss()
        }
        return binding.root
    }
}