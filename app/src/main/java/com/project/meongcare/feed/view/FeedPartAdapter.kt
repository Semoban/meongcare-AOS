package com.project.meongcare.feed.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.databinding.ItemFeedBinding
import com.project.meongcare.feed.model.entities.FeedRecord

class FeedPartAdapter : ListAdapter<FeedRecord, FeedPartAdapter.FeedPartViewHolder>(diffUtil) {
    inner class FeedPartViewHolder(private val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeedRecord) {
            binding.run {
                if (item.feedImageURL.isNotEmpty()) {
                    Glide.with(itemView)
                        .load(item.feedImageURL)
                        .into(imageviewFeed)
                }
                val period = "${item.startDate}~ ${item.endDate}"
                textviewFeedBrand.text = item.brandName
                textviewFeedName.text = item.feedName
                textviewFeedDate.text = period
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeedPartViewHolder {
        val itemFeedBinding =
            ItemFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return FeedPartViewHolder(itemFeedBinding)
    }

    override fun onBindViewHolder(
        holder: FeedPartViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<FeedRecord>() {
                override fun areItemsTheSame(
                    oldItem: FeedRecord,
                    newItem: FeedRecord,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: FeedRecord,
                    newItem: FeedRecord,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
