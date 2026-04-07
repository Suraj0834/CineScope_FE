package com.cinescope.app.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.data.model.ChatMessage
import com.cinescope.app.databinding.ItemChatMessageUserBinding
import com.cinescope.app.databinding.ItemChatMessageAiBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
        
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).role == "user") VIEW_TYPE_USER else VIEW_TYPE_AI
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = ItemChatMessageUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            UserMessageViewHolder(binding)
        } else {
            val binding = ItemChatMessageAiBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            AiMessageViewHolder(binding)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AiMessageViewHolder -> holder.bind(message)
        }
    }
    
    class UserMessageViewHolder(
        private val binding: ItemChatMessageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                // Set message text
                tvMessage.text = message.message

                // Show timestamp
                tvTimestamp.text = formatTime(message.timestamp)

                // Optional: Show message status (can be extended to show sent/delivered/read status)
                // For now, keep it hidden or show a simple indicator
                ivStatus.visibility = android.view.View.GONE

                // Optional: Show user avatar
                // For now, keep it hidden - can be shown if user profile data is available
                avatarCard.visibility = android.view.View.GONE
            }
        }
    }
    
    class AiMessageViewHolder(
        private val binding: ItemChatMessageAiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                // Show typing indicator or message
                if (message.isTyping) {
                    tvMessage.visibility = android.view.View.GONE
                    typingIndicator.visibility = android.view.View.VISIBLE
                } else {
                    tvMessage.visibility = android.view.View.VISIBLE
                    typingIndicator.visibility = android.view.View.GONE
                    tvMessage.text = message.message
                }

                // Show timestamp
                tvTimestamp.text = formatTime(message.timestamp)

                // Copy button
                btnCopy.setOnClickListener {
                    val clipboard = root.context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                        as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("AI Message", message.message)
                    clipboard.setPrimaryClip(clip)

                    // Show feedback
                    com.google.android.material.snackbar.Snackbar.make(
                        root,
                        "Message copied",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }

                // Share button
                btnShare.setOnClickListener {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, message.message)
                    }
                    root.context.startActivity(
                        android.content.Intent.createChooser(shareIntent, "Share AI Response")
                    )
                }
            }
        }
    }
    
    private class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
