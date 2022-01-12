package com.complete.convo.activities

import android.annotation.SuppressLint
import android.app.ActionBar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import android.graphics.BitmapFactory
import android.util.Log
import android.view.*
import androidx.core.app.NotificationCompat
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.disklrucache.DiskLruCache
import com.complete.convo.databinding.ActionbarBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec


class ChatActivity : AppCompatActivity() {
    private var encryptionKey = byteArrayOf(9,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53) ;
    private lateinit var storage: FirebaseStorage
    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var senderRoom : String? = null
    private var recieverRoom : String?= null
    private lateinit var dbReference : DatabaseReference
    private lateinit var mAdapter : MessagesAdapter
    private lateinit var messageList : ArrayList<Messages>

    private lateinit var cipher : Cipher
    private lateinit var decipher : Cipher
    private lateinit var secretKey : SecretKeySpec


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

        //encryption

        try{
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        }catch(e:NoSuchAlgorithmException){
            e.printStackTrace()
        }catch(e:NoSuchPaddingException){
            e.printStackTrace()
        }
        secretKey = SecretKeySpec(encryptionKey,"AES")




        binding.msgbox.setStartIconOnClickListener {
            binding.fragContainer.visibility = View.VISIBLE
            val msg = binding.messageBox.text.toString()
            var frag  = BlankFragment(msg)
            val manager = supportFragmentManager.beginTransaction()
            manager.replace(R.id.fragContainer,frag).addToBackStack("fragmnet Google")
            manager.commit()
        }

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid
        val emailorphone = intent.getStringExtra("emailorphone")

        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        /*val inf = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inf.inflate(R.layout.actionbar,null)*/
        val b = ActionbarBinding.inflate(layoutInflater)
        b.name.text = recieversName.toString()
        storage = FirebaseStorage.getInstance()
        val storagRef = storage.reference.child(recieverUid!!).child(recieverUid+"profile")
        val localFile = File.createTempFile("temp","jpg")
        storagRef.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            b.profilePic.setImageBitmap(bitMap)
        }
        b.root.setOnClickListener {
            val inte = Intent(this@ChatActivity,ProfileActivity::class.java)
            inte.putExtra("name",recieversName)
            inte.putExtra("emailorphone",emailorphone)
            inte.putExtra("uid",recieverUid)
            startActivity(inte)
            finish()
        }
        actionBar?.setCustomView(b.root)
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
        /*actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)*/
        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        messageList = ArrayList()
        mAdapter = MessagesAdapter(this, messageList)
        binding.recyclerView1.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        /*var items = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val msg = messageList.elementAt(viewHolder.adapterPosition)
                dbReference.child("chats").child(senderRoom.toString()).child("messages").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for(snap in snapshot.children){
                            val currentMessage = snap.getValue(Messages::class.java)
                            if(currentMessage!!.message == msg.message
                                && currentMessage!!.senderId == msg.senderId
                                && currentMessage!!.date == msg.date
                                && currentMessage!!.time == msg.time){
                                    Log.d("taget_sender_room",senderRoom.toString())

                                dbReference.child("chats").child(senderRoom.toString()).child("messages").child(snap.key.toString()).removeValue().addOnCompleteListener {
                                    dbReference.child("chats").child(recieverRoom.toString()).child("messages").addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot2: DataSnapshot) {
                                            for (snap2 in snapshot2.children) {
                                                val currentMessage2 =
                                                    snap.getValue(Messages::class.java)
                                                if (currentMessage2!!.message == msg.message
                                                    && currentMessage2!!.senderId == msg.senderId
                                                    && currentMessage2!!.date == msg.date
                                                    && currentMessage2!!.time == msg.time
                                                ) {
                                                    dbReference.child("chats")
                                                        .child(recieverRoom.toString())
                                                        .child("messages")
                                                        .child(snap2.key.toString()).removeValue()
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })

                                }
                                    Log.d("taget_key",snap.key.toString())
                                Log.d("taget_value",snap.value.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

        }
        val item = ItemTouchHelper(items)
        item.attachToRecyclerView(binding.recyclerView1)
*/

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
            val date = c.get(Calendar.DATE)
            val month = c.get(Calendar.MONTH)+1
            Log.d("m",month.toString())
            val year = c.get(Calendar.YEAR)
            var hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val seconds = c.get(Calendar.SECOND)
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
            }else {
                if (hour == 0) {
                    hour = 12
                }
                if (hour > 12) {
                    hour -= 12
                    am = "pm"
                }
                timeStamp = "$hour:$minute $am"
            }
            val messageObject = Messages(message,timeStamp , senderUid,"$date/$month/$year")

            if (message.isNotEmpty()) {
                Log.d("tagetmessage",messageObject.message.toString() + " " + messageObject.senderId.toString() + " " + messageObject.time.toString())
                dbReference.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnCompleteListener {
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
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                Toast.makeText(this@ChatActivity,"yes",Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/

    private fun AESEncryptionMethod( message : Messages): Messages {
        val stringByte = byteArrayOf(message.message!!.toByte())
        var encryptedByte = byteArrayOf()
        try{
          cipher.init(Cipher.ENCRYPT_MODE,secretKey)
            encryptedByte = cipher.doFinal(stringByte)
        }catch(e:InvalidKeyException){
            e.printStackTrace()
        }catch(e:BadPaddingException){
            e.printStackTrace()
        }catch(e:IllegalBlockSizeException){
            e.printStackTrace()
        }
        var returnString:String? = null
        var charset : Charset = charset("ISO-8859-1")
        try{
            returnString =  String(encryptedByte,charset)
        }catch(e:UnsupportedEncodingException){
            e.printStackTrace()
        }
        return Messages(
            returnString.toString(), message.time.toString(), message.senderId.toString(),
            message.date.toString()
        )
    }
    private fun AESDecryptionMethod(message : Messages): Messages {
        val stringByte = byteArrayOf(message.message!!.toByte())
        var encryptedByte = byteArrayOf()
        try{
            decipher.init(Cipher.DECRYPT_MODE,secretKey)
            encryptedByte = cipher.doFinal(stringByte)
        }catch(e:InvalidKeyException){
            e.printStackTrace()
        }catch(e:BadPaddingException){
            e.printStackTrace()
        }catch(e:IllegalBlockSizeException){
            e.printStackTrace()
        }
        var decryptedString:String? = null
        var charset : Charset = charset("ISO-8859-1")
        try{
            decryptedString =  String(encryptedByte,charset)
        }catch(e:UnsupportedEncodingException){
            e.printStackTrace()
        }
        return Messages(
            decryptedString.toString(), message.time.toString(), message.senderId.toString(),
            message.date.toString()
        )
    }
}