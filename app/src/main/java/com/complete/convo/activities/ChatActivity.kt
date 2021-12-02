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
import com.complete.convo.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class ChatActivity : Activity() {
    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter : MessagesAdapter
    private lateinit var messageList : ArrayList<Messages>

    private var senderRoom : String? = null
    private var recieverRoom : String?= null

    private lateinit var dbRefrence : DatabaseReference


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefrence = FirebaseDatabase
            .getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference


        val recieversName = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid!!
        /*apply {
            dbRefrence.child("user").child(senderUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        senderUser = snapshot.getValue(User ::class.java)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
        val senderName = senderUser?.name
        *//**/

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        messageList = ArrayList()
        mAdapter = MessagesAdapter(this, messageList)
        binding.recyclerView1.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        dbRefrence.child("chats").child("$recieversName <- $senderUid").child("messages")
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
            Log.d("taget", message)
            val messageObject = Messages(message, senderUid)

            if (message.isNotEmpty()) {
                dbRefrence.child("chats").child("$recieversName <- $senderUid").child("messages")
                    .push()
                    .setValue(messageObject).addOnSuccessListener {
                        dbRefrence.child("chats")
                            .child(recieverRoom.toString() + (" ($recieversName)"))
                            .child("messages").push()
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
    /* override fun onSupportNavigateUp(): Boolean {
         onBackPressed()
         return super.onSupportNavigateUp()
     }*/

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}