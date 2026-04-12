package com.mrnoone.cinescope.ui.details

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrnoone.cinescope.data.model.WatchmodeSource
import com.mrnoone.cinescope.databinding.ItemWatchmodeSourceBinding
import com.mrnoone.cinescope.util.Constants
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
                // Platform name
                tvSourceName.text = source.name

                // Type badge with uppercase text
                val typeText = when(source.type.lowercase()) {
                    "sub" -> "SUBSCRIPTION"
                    "rent" -> "RENT"
                    "buy" -> "BUY"
                    else -> source.type.uppercase()
                }
                tvSourceType.text = typeText

                // Region
                tvSourceRegion.text = source.region?.uppercase() ?: "IN"

                // Quality/Format badge
                tvSourceFormat.text = source.format?.uppercase() ?: "HD"

                // Price display (backend already sends prices in INR)
                if (source.price != null && source.price > 0) {
                    // Display price as-is (backend already converted to INR)
                    tvSourcePrice.text = source.price.toInt().toString()

                    // Show price period for subscriptions
                    if (source.type.lowercase() == "sub") {
                        tvPricePeriod.text = "/mo"
                        tvPricePeriod.visibility = android.view.View.VISIBLE
                    } else {
                        tvPricePeriod.visibility = android.view.View.GONE
                    }
                } else {
                    // Free/Included
                    tvSourcePrice.text = "0"
                    tvPricePeriod.visibility = android.view.View.GONE
                }

                // Watch Now button click
                btnWatchNow.setOnClickListener {
                    openStreamingSource(source)
                }

                // Card click
                root.setOnClickListener {
                    openStreamingSource(source)
                }
            }
        }

        private fun openStreamingSource(source: WatchmodeSource) {
            val url = source.webUrl
            if (!url.isNullOrEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    binding.root.context.startActivity(intent)
                } catch (e: Exception) {
                    Snackbar.make(
                        binding.root,
                        "Unable to open ${source.name}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Available on ${source.name}",
                    Snackbar.LENGTH_SHORT
                ).show()
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
