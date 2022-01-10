package com.complete.convo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.complete.convo.R
import com.complete.convo.activities.ChatActivity
import com.complete.convo.activities.ProfileActivity

import com.complete.convo.databinding.UserLayoutBinding
import com.complete.convo.model.User
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter (val context : Context, private var userList : ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var storage: FirebaseStorage

    class ViewHolder(binding: UserLayoutBinding/*view:View*/) : RecyclerView.ViewHolder(binding.root) {
        val textName = binding.name
        val emailorphn = binding.emailorphone
        val profilepic = binding.profilePic

        val v = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.textName.text = currentUser.name?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        holder.profilepic.setOnClickListener {
            val intent = Intent(context,ProfileActivity::class.java)

            intent.putExtra("name", currentUser.name)
            if(currentUser.email != null){
                intent.putExtra("emailorphone",currentUser.email)
            }else{
                intent.putExtra("emailorphone",currentUser.phoneNumber)
            }
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }
        storage= FirebaseStorage.getInstance()
        val uid=currentUser.uid

        val storagRef = storage.reference.child(uid!!).child(uid+"profile")
        val localFile = File.createTempFile("temp","jpg")
        var bitMap : Bitmap? = null
        storagRef.getFile(localFile).addOnSuccessListener {
            bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.profilepic.setImageBitmap(bitMap)
        }



        holder.v.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            if(currentUser.email != null){
                intent.putExtra("emailorphone",currentUser.email)
            }else{
                intent.putExtra("emailorphone",currentUser.phoneNumber)
            }
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }
        if(currentUser.email != null){
            holder.emailorphn.text = currentUser.email.toString()
        }else{
            holder.emailorphn.text = currentUser.phoneNumber.toString()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    fun filterList(filterllist: ArrayList<User>) {
        // below line is to add our filtered
        // list in our course array list.
        userList = filterllist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
}