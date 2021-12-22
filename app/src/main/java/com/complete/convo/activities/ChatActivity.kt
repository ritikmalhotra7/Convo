package com.complete.convo.activities

import android.annotation.SuppressLint

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.actvities.MainActivity
import com.complete.convo.adapters.MessagesAdapter
import com.complete.convo.databinding.ActivityChatBinding
import com.complete.convo.model.Messages
import com.complete.convo.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.*
import kotlin.collections.ArrayList
import android.media.RingtoneManager

import android.app.NotificationManager
import android.content.Context
import android.view.View
import androidx.core.app.NotificationCompat


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




        binding.msgbox.setStartIconOnClickListener {
            binding.fragContainer.visibility = View.VISIBLE
            val msg = binding.messageBox.text.toString()
            var frag  = BlankFragment(msg)
            val manager = supportFragmentManager.beginTransaction()
            manager.replace(R.id.fragContainer,frag)
            manager.commit()
        }

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid
        val emailorphone = intent.getStringExtra("emailorphone")

        var actionBar = supportActionBar
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        actionBar?.title = recieversName?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        actionBar?.subtitle = emailorphone.toString()
        actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)

        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        messageList = ArrayList()
        mAdapter = MessagesAdapter(this, messageList)
        binding.recyclerView1.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        dbReference.child("chats").child(senderRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Messages::class.java)
                    if(!message!!.senderId!!.equals(senderUid)){
                        var mNotificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val senderName = intent.getStringExtra("name")

                        val mBuilder: NotificationCompat.Builder =
                            NotificationCompat.Builder(this@ChatActivity)
                                .setSmallIcon(R.drawable.undraw_ideas_s70l)
                                .setContentTitle("New Message from " + senderName)
                                .setContentText(message.message)
                                .setOnlyAlertOnce(true)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        mBuilder.setAutoCancel(true)
                        mBuilder.setLocalOnly(false)
                        var NOTIFICATION_ID = 0
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

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
                    val intent = Intent(this@ChatActivity, MainActivity::class.java)
                    intent.putExtra("message_list_size",messageList.size)
                    intent.putExtra("username",recieverUid)



                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(binding.root, "not done", Snackbar.LENGTH_SHORT).show()
                }

            })
        /*binding.search.setOnClickListener{
            val open = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
            startActivity(open)
        }*/

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
                    hour -= 12
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
    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/
}