package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemOldFeedBinding
import com.project.meongcare.feed.model.entities.FeedRecord

class PreviousFeedAdapter : ListAdapter<FeedRecord, PreviousFeedAdapter.PreviousFeedViewHolder>(diffUtil) {
    inner class PreviousFeedViewHolder(private val binding: ItemOldFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeedRecord) {
            binding.run {
                if (!item.imageURL.isNullOrEmpty()) {
                    Glide.with(itemView)
                        .load(item.imageURL)
                        .into(imageviewOldfeed)
                }

                val period =
                    if (item.endDate == null) {
                        "${item.startDate}~ 모름"
                    } else {
                        "${item.startDate}~ ${item.endDate}"
                    }

                textviewOldfeedBrand.text = item.brand
                textviewOldfeedName.text = item.feedName
                textviewOldfeedDate.text = period
                root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putLong("feedId", item.feedId)
                    bundle.putLong("feedRecordId", item.feedRecordId)
                    it.findNavController().navigate(R.id.action_oldFeedFragment_to_feedInfoFragment, bundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PreviousFeedViewHolder {
        val itemOldFeedBinding =
            ItemOldFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return PreviousFeedViewHolder(itemOldFeedBinding)
    }

    override fun onBindViewHolder(
        holder: PreviousFeedViewHolder,
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
