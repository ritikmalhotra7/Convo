package com.complete.convo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.complete.convo.activities.ChatActivity
import com.complete.convo.activities.MainActivity

import com.complete.convo.databinding.UserLayoutBinding
import com.complete.convo.model.User

class UserAdapter (val context : Context, private val userList : ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(binding: UserLayoutBinding/*view:View*/) : RecyclerView.ViewHolder(binding.root) {
        val textName = binding.name

        val v = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.textName.text = currentUser.name


        holder.v.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}