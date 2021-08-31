package com.android.filmlibrary.view.message

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.databinding.ItemMessageBinding
import com.android.filmlibrary.model.data.MessageNot

class MessageFragmentAdapter : RecyclerView.Adapter<MessageFragmentAdapter.MyViewHolder>() {

    private var messages: List<MessageNot> = listOf()

    fun fillMessages(messages: List<MessageNot>) {
        this.messages = messages
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MessageFragmentAdapter.MyViewHolder {

        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messages[position]
        holder.header.text = message.header
        holder.body.text = message.body
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MyViewHolder(binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val header: TextView = binding.header
        val body: TextView = binding.body
    }
}