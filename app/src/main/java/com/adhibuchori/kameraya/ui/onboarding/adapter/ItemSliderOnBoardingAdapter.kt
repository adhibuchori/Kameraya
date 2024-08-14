package com.adhibuchori.kameraya.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adhibuchori.kameraya.databinding.ItemSliderOnBoardingBinding

class ItemSliderOnBoardingAdapter(
    private val items: List<OnBoardingItem>
) : RecyclerView.Adapter<ItemSliderOnBoardingAdapter.OnBoardingViewHolder>() {

    data class OnBoardingItem(
        val imageResId: Int,
        val title: String,
        val description: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding = ItemSliderOnBoardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class OnBoardingViewHolder(private val binding: ItemSliderOnBoardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnBoardingItem) {
            binding.ivSliderOnBoarding.setImageResource(item.imageResId)
            binding.tvSliderOnBoardingTitle.text = item.title
            binding.tvSliderOnBoardingDescription.text = item.description
        }
    }
}