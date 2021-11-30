package com.complete.convo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.complete.convo.databinding.RecieveMessageBinding
import com.complete.convo.databinding.SentBinding
import com.complete.convo.databinding.UserLayoutBinding
import com.complete.convo.model.Messages
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter(val context : Context,val messageList : ArrayList<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 1
    val ITEM_RECIEVE = 2

    class SentHolder(val binding : SentBinding) : RecyclerView.ViewHolder(binding.root){
        val sentMessage = binding.sentMessage
    }
    class RecieverHolder(val binding : RecieveMessageBinding) : RecyclerView.ViewHolder(binding.root){
        val RecievedMessage = binding.reciversMessge
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val binding = SentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return SentHolder(binding)
        }else{
            val binding = RecieveMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return RecieverHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == SentHolder::class.java){
            val viewHolder = holder as SentHolder
            holder.sentMessage.text = currentMessage.message

        }else{
            val viewHolder = holder as RecieverHolder
            holder.RecievedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid!! == currentMessage.senderId){
            ITEM_SENT
        }else{
            ITEM_RECIEVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}