package com.cinescope.app.ui.details

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cinescope.app.data.model.WatchmodeSource
import com.cinescope.app.databinding.ItemWatchmodeSourceBinding
import com.google.android.material.snackbar.Snackbar

class SourcesAdapter : ListAdapter<WatchmodeSource, SourcesAdapter.SourceViewHolder>(SourceDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val binding = ItemWatchmodeSourceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SourceViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class SourceViewHolder(
        private val binding: ItemWatchmodeSourceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(source: WatchmodeSource) {
            binding.apply {
                tvSourceName.text = source.name
                tvSourceType.text = when(source.type.lowercase()) {
                    "sub" -> "Subscription"
                    "rent" -> "Rent"
                    "buy" -> "Buy"
                    else -> source.type.replaceFirstChar { it.uppercase() }
                }
                tvSourceRegion.text = source.region ?: "IN"
                tvSourceFormat.text = source.format?.uppercase() ?: "HD"
                tvSourcePrice.text = if (source.price != null && source.price > 0) {
                    "₹${source.price.toInt()}"
                } else {
                    "Included"
                }
                
                // Handle click to open streaming source URL
                root.setOnClickListener {
                    val url = source.webUrl
                    if (!url.isNullOrEmpty()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            root.context.startActivity(intent)
                        } catch (e: Exception) {
                            Snackbar.make(
                                root,
                                "Unable to open ${source.name}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            root,
                            "No URL available for ${source.name}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    
    private class SourceDiffCallback : DiffUtil.ItemCallback<WatchmodeSource>() {
        override fun areItemsTheSame(oldItem: WatchmodeSource, newItem: WatchmodeSource): Boolean {
            return oldItem.name == newItem.name && oldItem.type == newItem.type
        }
        
        override fun areContentsTheSame(oldItem: WatchmodeSource, newItem: WatchmodeSource): Boolean {
            return oldItem == newItem
        }
    }
}
