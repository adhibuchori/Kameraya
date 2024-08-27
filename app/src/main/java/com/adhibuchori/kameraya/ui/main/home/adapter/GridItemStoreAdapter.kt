package com.adhibuchori.kameraya.ui.main.home.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.kameraya.databinding.ItemRowCameraStoreGridBinding
import com.adhibuchori.kameraya.utils.base.paging.BasePagingAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.formatRating
import com.bumptech.glide.Glide

class GridItemStoreAdapter : BasePagingAdapter<ProductsModel, ItemRowCameraStoreGridBinding>(
    ItemRowCameraStoreGridBinding::inflate
) {
    private var onClick: ((ProductsModel) -> Unit)? = null

    fun setOnItemClickListener(action: (ProductsModel) -> Unit) {
        onClick = action
    }

    @SuppressLint("SetTextI18n")
    override fun onItemBind(): (ProductsModel, ItemRowCameraStoreGridBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                sivRowCameraStoreGridCameraImage.run {
                    Glide.with(context)
                        .load(data.image)
                        .into(this)
                    post {
                        val lp = layoutParams as ConstraintLayout.LayoutParams
                        lp.height = measuredWidth
                        layoutParams = lp
                        requestLayout()
                    }
                }

                tvRowCameraStoreGridCameraName.text = data.productName
                tvRowCameraStoreGridCameraPrice.text = data.productPrice.formatPrice()
                tvRowCameraStoreGridCameraStore.text = data.store
                tvRowWishlistGridCameraReview.text =
                    data.productRating.formatRating(root.context, data.sale)
            }
            view.setOnClickListener { onClick?.invoke(data) }
        }
}