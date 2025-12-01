package com.example.disasternet.ui.shoutbox

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.disasternet.R
import com.example.disasternet.data.DisasterMessage
import java.text.SimpleDateFormat
import java.util.*

class ShoutboxAdapter : ListAdapter<DisasterMessage, ShoutboxAdapter.MessageViewHolder>(MessageDiffCallback()) {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sender: TextView = itemView.findViewById(R.id.text_message_sender)
        private val body: TextView = itemView.findViewById(R.id.text_message_body)
        private val time: TextView = itemView.findViewById(R.id.text_message_time)
        private val layout: LinearLayout = itemView as LinearLayout

        fun bind(message: DisasterMessage) {
            sender.text = if (message.isSentByMe) "Me" else message.senderName
            body.text = message.message
            time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

            // All messages are public and aligned left
            layout.gravity = Gravity.START
            body.background.setTint(ContextCompat.getColor(itemView.context, R.color.received_bubble))
            sender.gravity = Gravity.START
            time.gravity = Gravity.START

            // Highlight SOS messages clearly
            if (message.senderName.contains("SOS")) {
                sender.setTextColor(ContextCompat.getColor(itemView.context, R.color.emergency_red))
                body.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_text))
            } else {
                sender.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent_orange))
                body.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_text))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<DisasterMessage>() {
    override fun areItemsTheSame(oldItem: DisasterMessage, newItem: DisasterMessage): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: DisasterMessage, newItem: DisasterMessage): Boolean {
        return oldItem == newItem
    }
}