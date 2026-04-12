package com.mrnoone.cinescope.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrnoone.cinescope.R
import com.mrnoone.cinescope.data.model.CastMember
import com.mrnoone.cinescope.databinding.ItemCastMemberBinding
import com.mrnoone.cinescope.util.Constants
import com.mrnoone.cinescope.util.loadImage

class CastAdapter(
    private val onCastClick: (CastMember) -> Unit
) : ListAdapter<CastMember, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castMember = getItem(position)
        holder.bind(castMember)
        
        // Add animation
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in_slide_up)
        )
    }

    inner class CastViewHolder(
        private val binding: ItemCastMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCastClick(getItem(position))
                }
            }
        }

        fun bind(cast: CastMember) {
            binding.apply {
                tvName.text = cast.name
                tvCharacter.text = cast.character

                val profileUrl = if (cast.profilePath != null) {
                    if (cast.profilePath.startsWith("http")) {
                        cast.profilePath
                    } else {
                        "${Constants.TMDB_IMAGE_BASE_URL}${Constants.PROFILE_SIZE_SMALL}${cast.profilePath}"
                    }
                } else {
                    null // Will load placeholder
                }
                
                ivProfile.loadImage(profileUrl)
            }
        }
    }

    private class CastDiffCallback : DiffUtil.ItemCallback<CastMember>() {
        override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
            return oldItem == newItem
        }
    }
}
