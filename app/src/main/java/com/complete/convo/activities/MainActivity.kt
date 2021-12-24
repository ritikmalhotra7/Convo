package com.complete.convo.actvities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache
import com.complete.convo.R
import com.complete.convo.adapters.UserAdapter
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.databinding.NavHeaderBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.complete.convo.activities.AllUsers
import com.complete.convo.activities.ContactUs
import com.complete.convo.activities.LoginActivity
import com.complete.convo.model.Messages
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var uri : StorageReference
    private var imageUri: Uri? = null
    private val pickImage: Int = 100
    private var clicked: Boolean = false
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var userList:ArrayList<User>
    private lateinit var adapter : UserAdapter

    private lateinit var mAuth :FirebaseAuth
    private lateinit var dbReference : DatabaseReference
    lateinit var toggle : ActionBarDrawerToggle

    var name :String? = null
    var emailorphone :String? = null
    private lateinit var storage : FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progress.visibility = View.VISIBLE
        binding.swipe.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            Handler().postDelayed({ binding.swipe.isRefreshing = false }, 2000)
        }

        mAuth = FirebaseAuth.getInstance()
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)


        toggle = ActionBarDrawerToggle(this,binding.drawerlayout,R.string.open,R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        storage = FirebaseStorage.getInstance()
        val storagRef = storage.reference.child(mAuth.currentUser!!.uid).child(mAuth.currentUser!!.uid+"bg")
        val localFile = File.createTempFile("temp","jpg")
        storagRef.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageview.setImageBitmap(bitMap)
            binding.progress.visibility = View.INVISIBLE
        }

        val navi = binding.navView
        val header = navi.getHeaderView(0)








        dbReference.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(snap in snapshot.children){
                    val currentUser = snap.getValue(User::class.java)
                    val receiversuid = currentUser!!.uid
                    val senderUid = mAuth.currentUser!!.uid
                    val senderRoom = senderUid+receiversuid
                    if(currentUser.uid == mAuth.currentUser?.uid){
                        name = currentUser!!.name.toString()
                        if(currentUser.email != null){
                            emailorphone = currentUser.email.toString()
                        }else{
                            emailorphone = currentUser.phoneNumber.toString()
                        }
                        header.findViewById<TextView>(R.id.tv_username).setText(name)
                        header.findViewById<TextView>(R.id.emailorphone).setText(emailorphone)

                    }
                    dbReference.child("chats").child(senderRoom).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.childrenCount>0){
                                if(userList.contains(currentUser)){

                                }else{
                                    userList.add(currentUser)
                                    adapter.notifyDataSetChanged()
                                    Log.d("tagetsnapshots",currentUser.name.toString())
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, AllUsers::class.java)
            startActivity(intent)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.logout ->{
                    mAuth.signOut()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    finish()
                    mAuth.signOut()
                    startActivity(intent)
                }
                R.id.contactus->{
                    val intent=Intent(this, ContactUs::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_menu2,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            if(data != null){
                imageUri = data.data
                storage = FirebaseStorage.getInstance()
                val storageRefer = storage.reference
                storageRefer.child(mAuth.currentUser!!.uid).child(mAuth.currentUser!!.uid+"bg")
                    .putFile(data.data!!).addOnCompleteListener {
                        Toast.makeText(this,"saved",Toast.LENGTH_LONG).show()
                    }
                binding.imageview.setImageURI(imageUri)
            }else{
                Toast.makeText(this,"Empty Data",Toast.LENGTH_SHORT).show()
            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        when(item.itemId) {
            R.id.wallpaper ->{
                val galleryIntent = Intent(Intent.ACTION_PICK)
                galleryIntent.type = "image/*, video/*"
                if (galleryIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(
                        Intent.createChooser(galleryIntent, "Select File"),pickImage
                    )
                }

            }


        }
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}