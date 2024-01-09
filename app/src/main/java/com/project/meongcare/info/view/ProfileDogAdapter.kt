package com.project.meongcare.info.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemPetBinding
import com.project.meongcare.home.model.entities.DogProfile
import de.hdodenhof.circleimageview.CircleImageView

class ProfileDogAdapter(
    private val layoutInflater: LayoutInflater,
    private val context: Context,
) : RecyclerView.Adapter<ProfileDogAdapter.DogProfileViewHolder>() {
    private var dogList: List<DogProfile> = emptyList()

    fun updateDogList(newList: List<DogProfile>) {
        this.dogList = newList
        notifyDataSetChanged()
    }

    inner class DogProfileViewHolder(itemPetBinding: ItemPetBinding) :
        RecyclerView.ViewHolder(itemPetBinding.root) {
        val cardviewPet: CardView
        val circleimageviewPetProfile: CircleImageView
        val textviewPetName: TextView

        init {
            cardviewPet = itemPetBinding.cardviewPet
            circleimageviewPetProfile = itemPetBinding.circleimageviewPetProfile
            textviewPetName = itemPetBinding.textviewPetName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DogProfileViewHolder {
        val itemPetBinding = ItemPetBinding.inflate(layoutInflater)
        itemPetBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return DogProfileViewHolder(itemPetBinding)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(
        holder: DogProfileViewHolder,
        position: Int,
    ) {
        holder.textviewPetName.text = dogList[position].name
        Glide.with(context)
            .load(dogList[position].imageUrl)
            .placeholder(R.drawable.home_dog_default)
            .error(R.drawable.home_dog_default)
            .into(holder.circleimageviewPetProfile)

        holder.cardviewPet.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("dogId", dogList[position].dogId)
            it.findNavController()
                .navigate(R.id.action_profileFragment_to_petAddFragment, bundle)
        }
    }
}
