package com.complete.convo.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.adapters.MessagesAdapter
import com.complete.convo.databinding.ActivityChatBinding
import com.complete.convo.model.Messages
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : Activity() {
    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter : MessagesAdapter
    private lateinit var messageList : ArrayList<Messages>

    private var senderRoom : String? = null
    private var recieverRoom : String?= null

    private lateinit var dbReference : DatabaseReference


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbReference = FirebaseDatabase
            .getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference


        val recieversName = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid!!


        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        messageList = ArrayList()
        mAdapter = MessagesAdapter(this, messageList)
        binding.recyclerView1.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        dbReference.child("chats").child(senderUid).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (snap in snapshot.children) {
                        val message = snap.getValue(Messages::class.java)
                        //*messageList[position]*//*
                        messageList.add(message!!)
                    }
                    mAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(binding.root, "not done", Snackbar.LENGTH_SHORT).show()
                }

            })
        binding.send.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = "$hour:$minute"
            val messageObject = Messages(message,timeStamp , senderUid)

            if (message.isNotEmpty()) {
                dbReference.child("chats").child(senderUid).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        dbReference.child("chats").child(recieverUid!!).child("messages").push()
                            .setValue(messageObject)
                    }
                binding.messageBox.setText("")
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please enter some message! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}