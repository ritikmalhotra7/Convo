package com.complete.convo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.R
import com.complete.convo.adapters.UserAdapter
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.databinding.NavHeaderBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var uri: Uri ? = null
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        val sharedPrefrences = getSharedPreferences("shared",Context.MODE_PRIVATE)
        val savedString = sharedPrefrences.getString("uri","nothing")
        Log.d("tagetMain",savedString.toString())
        binding.imageview.setImageURI(uri)



        /*val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        uri = Uri.parse(sharedPref.getString("imageUri","/"))
        binding.imageview.setImageURI(uri)
            binding.imageview.invalidate()*/

        toggle = ActionBarDrawerToggle(this,binding.drawerlayout,R.string.open,R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()


        binding.search.setOnClickListener{
            val open = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/"))
            startActivity(open)
        }

        dbReference.child("user").child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val cu = snapshot.getValue(User::class.java)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val b = NavHeaderBinding.inflate(layoutInflater)
        b.emailorphone.text = emailorphone.toString()
        b.tvUsername.text = name.toString()
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







        /*dbReference.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                val senderUid = mAuth.currentUser?.uid
                for(snap in snapshot.children){
                    Log.d("tagetsnaps",snap.exists().toString())
                    val currentUser = snap.getValue(User::class.java)
                    val recieverUid = currentUser?.uid
                    val senderRoom = recieverUid + senderUid
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        Log.d("tagetsnap",snap.exists().toString())
                        dbReference.child("chats").child(senderRoom).addValueEventListener(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    Log.d("taget",snapshot.exists().toString())
                                    userList.add(currentUser!!)
                                    Log.d("tagetuser",userList.toString())
                                    Log.d("tagetuser",currentUser.name.toString())
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })*/
        binding.fab.setOnClickListener {
            val intent = Intent(this,AllUsers::class.java)
            startActivity(intent)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView
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
                    val intent=Intent(this,ContactUs::class.java)
                    startActivity(intent)
                }
                /*R.id.wallpaper ->{
                   if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        showImageChooser()
                        val b = NavHeaderBinding.inflate(layoutInflater)
                        b.yourpicture.setImageURI(ur)
                    } else {

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            READ_STORAGE_PERMISSION_CODE
                        )
                    }

                }*/

            }
            true
        }
       /* val preferences: SharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
        val mImageUri = preferences.getString("imageuri", null)
        val url = Uri.parse(mImageUri)*/

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
                binding.imageview.setImageURI(imageUri)
                val sharedPrefrences = getSharedPreferences("shared",Context.MODE_PRIVATE)
                val editor = sharedPrefrences.edit()
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(this, data.data!!)
                editor.apply{
                    putString("uri",filePath.toString())
                }.apply()

            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        when(item.itemId) {
            R.id.wallpaper ->{
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
                gallery.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                gallery.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
           /* R.id.wallpaper ->{
                *//*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser()


                } else {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }*//*

            }
            R.id.profilePic -> {
               *//* if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser()
                    clicked = true
                    val b = NavHeaderBinding.inflate(layoutInflater)
                    b.yourpicture.setImageURI(uri)
                } else {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }*//*
            }*/

        }
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

   /* private fun showImageChooser() {
        // An intent for launching the image selection of phone storage.
        *//*val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )*//*
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    companion object {
        //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
        private const val READ_STORAGE_PERMISSION_CODE = 1

        private const val PICK_IMAGE_REQUEST_CODE = 2
    }*/


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            uri = data.data!!
            *//*this.grantUriPermission(this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            this.getContentResolver().takePersistableUriPermission(uri, takeFlags);*//*
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString("imageuri",uri.toString())
                apply()
            }
        }
        try{
            binding.imageview.setImageURI(uri)
        }catch (e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }*/
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}