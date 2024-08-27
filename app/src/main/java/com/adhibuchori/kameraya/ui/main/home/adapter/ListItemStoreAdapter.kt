package com.adhibuchori.kameraya.ui.main.home.adapter

import android.annotation.SuppressLint
import android.view.View
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.kameraya.databinding.ItemRowCameraStoreLinearBinding
import com.adhibuchori.kameraya.utils.base.paging.BasePagingAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.formatRating
import com.bumptech.glide.Glide

class ListItemStoreAdapter : BasePagingAdapter<ProductsModel, ItemRowCameraStoreLinearBinding>(
    ItemRowCameraStoreLinearBinding::inflate
) {

    private var onClick: ((ProductsModel) -> Unit)? = null

    fun setOnItemClickListener(action: (ProductsModel) -> Unit) {
        onClick = action
    }

    @SuppressLint("SetTextI18n")
    override fun onItemBind(): (ProductsModel, ItemRowCameraStoreLinearBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                Glide.with(root.context)
                    .load(data.image)
                    .into(ivRowCameraStoreLinearCameraImage)

                tvRowCameraStoreLinearCameraName.text = data.productName
                tvRowCameraStoreLinearCameraPrice.text = data.productPrice.formatPrice()
                tvRowCameraStoreLinearCameraStore.text = data.store
                tvRowCameraStoreLinearCameraReview.text =
                    data.productRating.formatRating(root.context, data.sale)
            }
            view.setOnClickListener { onClick?.invoke(data) }
        }
}