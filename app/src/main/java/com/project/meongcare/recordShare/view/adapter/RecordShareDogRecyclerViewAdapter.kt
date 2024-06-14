package com.project.meongcare.recordShare.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemPetShareBinding
import com.project.meongcare.recordShare.viewmodel.RecordShareViewModel

class RecordShareDogRecyclerViewAdapter(
    private val recordShareViewModel: RecordShareViewModel,
    private val context: Context,
    ) : RecyclerView.Adapter<RecordShareDogRecyclerViewAdapter.RecordShareDogViewHolder>() {

    val dogList = recordShareViewModel.dogList.value!!.body()!!.dogs
    inner class RecordShareDogViewHolder(itemPetShareBinding: ItemPetShareBinding) :
        RecyclerView.ViewHolder(itemPetShareBinding.root) {
        val itemPetShareImg: ImageView
        val itemPetShareName: TextView
        val itemPetShareCheck: ImageView

        init {
            itemPetShareImg =
                itemPetShareBinding.circleimageviewPetshareImage
            itemPetShareName =
                itemPetShareBinding.textviewPetshareName
            itemPetShareCheck =
                itemPetShareBinding.imageviewPetshareCheck
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecordShareDogViewHolder {
        val itemPetShareBinding =
            ItemPetShareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val allViewHolder = RecordShareDogViewHolder(itemPetShareBinding)

        itemPetShareBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        return allViewHolder
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(
        holder: RecordShareDogViewHolder,
        position: Int,
    ) {
        Glide.with(context)
            .load(dogList[position].imageUrl)
            .placeholder(R.drawable.home_dog_default)
            .error(R.drawable.home_dog_default)
            .fallback(R.drawable.home_dog_default)
            .into(holder.itemPetShareImg)

        holder.itemPetShareName.text = dogList[position].name
    }
}
