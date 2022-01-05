package com.complete.convo.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.R
import com.complete.convo.adapters.UserAdapter
import com.complete.convo.databinding.ActivityAllUsersBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
        val actionBar = supportActionBar
        actionBar?.title = "All Users"
        dbReference = FirebaseDatabase.getInstance("https://convo-8ee5b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        binding.recyclerViewforAll.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewforAll.adapter = adapter
        binding.recyclerViewforAll.setHasFixedSize(true)
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
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_menu1,menu)
        val search = menu!!.findItem(R.id.search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO()
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<User> = ArrayList()

        // running a for loop to compare elements.
        for (item in userList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name!!.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist)
        }
    }
}