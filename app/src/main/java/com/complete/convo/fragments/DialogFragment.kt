package com.complete.convo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.complete.convo.activities.SignupActivity
import com.complete.convo.databinding.SignupViaDialogBinding

open class DialogFragment : DialogFragment() {
   private var _binding :SignupViaDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignupViaDialogBinding.inflate(inflater,container,false)
        binding.viaph.setOnClickListener {
            val intent = Intent(activity,SignupActivity::class.java)
            intent.putExtra("code_",1)//for phone
            startActivity(intent)
            dismiss()
         }
        binding.viaEmail.setOnClickListener {
            val intent = Intent(activity,SignupActivity::class.java)
            intent.putExtra("code_",2)//for email
            startActivity(intent)
            dismiss()
        }
        return binding.root
    }
}