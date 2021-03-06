package com.android.filmlibrary.presentation.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.databinding.ItemContactBinding
import com.android.filmlibrary.data.model.Contact

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private var onContactClickListener: (Contact) -> Unit = {}

    fun setOnContactClickListener(onContactClickListener: (Contact) -> Unit) {
        this.onContactClickListener = onContactClickListener
    }

    private var contacts: List<Contact> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun fillContacts(contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var contact: Contact? = null

        fun bind(contact: Contact) {
            binding.contactName.text = contact.name
            if (contact.numbers.isNotEmpty()) {
                binding.contactPhone.text = contact.numbers[0]
                binding.contactPhone.setOnClickListener {
                    onContactClickListener(contact)
                }
            }
        }
    }
}