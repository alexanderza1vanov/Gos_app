package com.example.govservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.dto.ChatMessageResponse

class ChatMessageAdapter(
    private var items: List<ChatMessageResponse>,
    private val currentUserRole: String?
) : RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderTextView: TextView = view.findViewById(R.id.senderTextView)
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = items[position]

        val sender = if (message.senderRole == "EMPLOYEE") {
            "Сотрудник"
        } else {
            "Заявитель"
        }

        holder.senderTextView.text = sender
        holder.messageTextView.text = message.text
        holder.dateTextView.text = message.createdAt.replace("T", " ")

        if (message.senderRole == currentUserRole) {
            holder.senderTextView.text = "$sender · вы"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ChatMessageResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}