package com.project.meongcare.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemHomeDogProfileBinding
import com.project.meongcare.home.model.data.local.DogProfileClickListener
import com.project.meongcare.home.model.entities.DogProfile
import de.hdodenhof.circleimageview.CircleImageView

class HomeDogProfileAdapter(
    private val layoutInflater: LayoutInflater,
    private val context: Context,
    private val clickListener: DogProfileClickListener,
) : RecyclerView.Adapter<HomeDogProfileAdapter.DogProfileViewHolder>() {
    private var dogProfileList: List<DogProfile> = emptyList()
    private var selectedPos: Int = RecyclerView.NO_POSITION

    fun updateDogProfileList(newList: List<DogProfile>) {
        this.dogProfileList = newList
        notifyDataSetChanged()
    }

    fun updateSelectedPos(newPos: Int) {
        this.selectedPos = newPos
        notifyDataSetChanged()
    }

    inner class DogProfileViewHolder(itemHomeDogProfileBinding: ItemHomeDogProfileBinding) :
        RecyclerView.ViewHolder(itemHomeDogProfileBinding.root) {
        val layoutDogProfile: ConstraintLayout
        val imageViewDogProfileBg: CircleImageView
        val imageViewDogProfile: CircleImageView
        val textViewDogName: TextView

        init {
            layoutDogProfile = itemHomeDogProfileBinding.layoutDogProfile
            imageViewDogProfileBg = itemHomeDogProfileBinding.imageviewDogProfileBg
            imageViewDogProfile = itemHomeDogProfileBinding.imageviewDogProfile
            textViewDogName = itemHomeDogProfileBinding.textviewDogName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DogProfileViewHolder {
        val itemHomeDogProfileBinding = ItemHomeDogProfileBinding.inflate(layoutInflater)
        itemHomeDogProfileBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return DogProfileViewHolder(itemHomeDogProfileBinding)
    }

    override fun getItemCount(): Int {
        return dogProfileList.size
    }

    override fun onBindViewHolder(
        holder: DogProfileViewHolder,
        position: Int,
    ) {
        Glide.with(context)
            .load(dogProfileList[position].imageUrl)
            .placeholder(R.drawable.home_dog_default)
            .error(R.drawable.home_dog_default)
            .fallback(R.drawable.home_dog_default)
            .into(holder.imageViewDogProfile)
        holder.textViewDogName.text = dogProfileList[position].name

        holder.layoutDogProfile.setOnClickListener {
            clickListener.onDogProfileClick(position)
        }

        updateProfileBg(holder.imageViewDogProfileBg, position)
    }

    fun updateProfileBg(
        imageView: CircleImageView,
        currentPos: Int,
    ) {
        if (selectedPos == currentPos) {
            imageView.setImageResource(R.drawable.home_dog_profile_bg_select)
        } else {
            imageView.setImageResource(R.drawable.home_dog_profile_bg_unselect)
        }
    }
}
