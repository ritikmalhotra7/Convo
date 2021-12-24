package com.complete.convo.actvities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
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
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference



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


        toggle = ActionBarDrawerToggle(this,binding.drawerlayout,R.string.open,R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        val b = NavHeaderBinding.inflate(layoutInflater)

        dbReference.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children){
                    val currentUser = snap.getValue(User::class.java)
                    val receiversuid = currentUser!!.uid
                    val senderUid = mAuth.currentUser!!.uid
                    val senderRoom = senderUid+receiversuid


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
        /*dbReference.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(snap in snapshot.children){
                    val currentUser = snap.getValue(User::class.java)
                    var hasChat = false
                    try {
                        val receiversuid = currentUser!!.uid
                        val senderUid = mAuth.currentUser!!.uid
                        val senderRoom = senderUid+receiversuid

                        dbReference.child("chats").child(senderRoom).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshots: DataSnapshot) {
                                Log.d("tagets",snapshots.toString())
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }catch (e:IllegalAccessException){

                    }
                    if(mAuth.currentUser?.uid != currentUser?.uid && hasChat){
                        userList.add(currentUser!!)
                        Log.d("tagethaschat",hasChat.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        )*/
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
                storageRef.child(mAuth.currentUser!!.uid.toString())
                    .putFile(data.data!!).addOnSuccessListener {
                        Toast.makeText(this,"saved",Toast.LENGTH_SHORT).show()
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