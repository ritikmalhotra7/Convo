package com.complete.convo.activities

import android.annotation.SuppressLint

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.adapters.MessagesAdapter
import com.complete.convo.databinding.ActivityChatBinding
import com.complete.convo.model.Messages
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var senderRoom : String? = null
    private var recieverRoom : String?= null
    private lateinit var dbReference : DatabaseReference
    private lateinit var mAdapter : MessagesAdapter
    private lateinit var messageList : ArrayList<Messages>

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
        binding.toolbar.title = recieversName
        setSupportActionBar(binding.toolbar)


        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        messageList = ArrayList()
        mAdapter = MessagesAdapter(this, messageList)
        binding.recyclerView1.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        dbReference.child("chats").child(senderRoom!!).child("messages")
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
                    val intent = Intent(this@ChatActivity,MainActivity::class.java)
                    intent.putExtra("message_list_size",messageList.size)
                    intent.putExtra("username",recieverUid)

                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(binding.root, "not done", Snackbar.LENGTH_SHORT).show()
                }

            })
        binding.searchInChat.setOnClickListener{
            val open = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
            startActivity(open)
        }
        binding.send.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val c = Calendar.getInstance()
            var hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            var timeStamp = ""
            var am = "am"
            if(minute<10 ){
                if(hour == 0){
                    hour = 12
                }
                if(hour>12){
                    hour -=12
                    am = "pm"
                }
                 timeStamp = "$hour:0$minute $am"
            }else{
                if(hour == 0){
                    hour = 12
                }
                if(hour>12){
                    hour -=12
                    am = "pm"
                }
                 timeStamp = "$hour:$minute $am"
            }

            val messageObject = Messages(message,timeStamp , senderUid)

            if (message.isNotEmpty()) {
                dbReference.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        dbReference.child("chats").child(recieverRoom!!).child("messages").push()
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