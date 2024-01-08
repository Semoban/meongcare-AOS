package com.project.meongcare.feed.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.databinding.ItemSearchFeedBinding
import com.project.meongcare.feed.model.data.local.FeedItemSelectionListener
import com.project.meongcare.feed.model.entities.Feed

class FeedsAdapter(
    private val feedItemSelectionListener: FeedItemSelectionListener,
) : ListAdapter<Feed, FeedsAdapter.FeedsViewHolder>(diffUtil) {
    inner class FeedsViewHolder(private val binding: ItemSearchFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Feed) {
            binding.run {
                if (item.imageURL.isNotEmpty()) {
                    Glide.with(itemView)
                        .load(item.imageURL)
                        .into(imageviewOldfeed)
                }
                textviewSearchfeedBrand.text = item.brandName
                textviewSearchfeedName.text = item.feedName
                root.setOnClickListener {
                    Log.d("feedId", item.feedId.toString())
                    feedItemSelectionListener.onItemSelection(item.feedId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeedsViewHolder {
        val itemSearchFeedBinding =
            ItemSearchFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return FeedsViewHolder(itemSearchFeedBinding)
    }

    override fun onBindViewHolder(
        holder: FeedsViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
