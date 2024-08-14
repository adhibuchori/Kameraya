package com.adhibuchori.kameraya.ui.main.productDetail.adapter

import android.view.View
import com.adhibuchori.kameraya.databinding.ItemSliderProductDetailBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide

class ItemSliderProductDetailAdapter : BaseListAdapter<String, ItemSliderProductDetailBinding>(
    ItemSliderProductDetailBinding::inflate
) {
    override fun onItemBind(): (String, ItemSliderProductDetailBinding, View, Int) -> Unit =
        { data, binding, _, _ ->
            with(binding) {
                Glide.with(ivSliderContent)
                    .load(data)
                    .into(ivSliderContent)
            }
        }
}