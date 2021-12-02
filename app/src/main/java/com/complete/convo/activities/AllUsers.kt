package com.complete.convo.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.R
import com.complete.convo.adapters.UserAdapter
import com.complete.convo.databinding.ActivityAllUsersBinding
import com.complete.convo.databinding.ActivityMainBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AllUsers : AppCompatActivity() {
    private var _binding : ActivityAllUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var userList:ArrayList<User>
    private lateinit var adapter : UserAdapter

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAllUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        binding.recyclerViewforall.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewforall.adapter = adapter
        binding.recyclerViewforall.setHasFixedSize(true)

        dbReference.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(snap in snapshot.children){
                    val currentUser = snap.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}