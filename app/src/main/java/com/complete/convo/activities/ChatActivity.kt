package com.complete.convo.activities

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

class ChatActivity : Activity() {
    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter : MessagesAdapter
    private lateinit var messageList : ArrayList<Messages>

    var senderRoom : String? = null
    var recieverRoom : String?= null

    private lateinit var dbRefrence : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefrence =  FirebaseDatabase
            .getInstance("https://basic-chat-application-4d671-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference()

        val name = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid!!

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        messageList = ArrayList()
        mAdapter = MessagesAdapter(this,messageList)
        binding.recyclerView.adapter = mAdapter


        dbRefrence.child("chats").child(senderUid +"->"+name.toString()).child("messages").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(snap in snapshot.children){
                    val message = snap.getValue(Messages::class.java)
                    /*messageList[position]*/
                    messageList.add(message!!)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        binding.send.setOnClickListener {
            val message = binding.messageBox.text.toString()
            Log.d("taget",message)
            val messageObject = Messages(message,senderUid)

            if(message.isNotEmpty()) {
                dbRefrence.child("chats").child(senderRoom.toString()).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        dbRefrence.child("chats").child(recieverRoom.toString()).child("messages").push()
                            .setValue(messageObject)
                    }
                binding.messageBox.setText("")
            }else{
                Toast.makeText(applicationContext, "Please enter some message! ", Toast.LENGTH_SHORT).show()
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
    /*override fun onDestroy() {
        super.onDestroy()
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(lastp, lastPosition!!)
            apply()
        }*//*SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPos", lastPosition);
        e.apply();*//*
    }*/
}