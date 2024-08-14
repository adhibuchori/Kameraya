package com.adhibuchori.kameraya.ui.main.wishlist.adapter

import android.annotation.SuppressLint
import android.view.View
import com.adhibuchori.domain.repository.wishlist.WishlistModel
import com.adhibuchori.kameraya.databinding.ItemRowWishlistGridBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class GridItemWishlistAdapter : BaseListAdapter<WishlistModel, ItemRowWishlistGridBinding>(
    ItemRowWishlistGridBinding::inflate
) {
    private var onClick: ((WishlistModel) -> Unit)? = null
    private var onDelete: ((WishlistModel) -> Unit)? = null
    private var onAddCart: ((WishlistModel) -> Unit)? = null

    fun setOnItemClickListener(action: (WishlistModel) -> Unit) {
        onClick = action
    }

    fun setOnDeleteItemListener(action: (WishlistModel) -> Unit) {
        onDelete = action
    }

    fun setOnAddCartItemListener(action: (WishlistModel) -> Unit) {
        onAddCart = action
    }

    @SuppressLint("SetTextI18n")
    override fun onItemBind(): (WishlistModel, ItemRowWishlistGridBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                tvRowWishlistGridCameraName.text = data.productName

                Glide.with(root.context)
                    .load(data.productImage)
                    .into(sivRowWishlistGridCameraImage)

                tvRowWishlistGridCameraPrice.text = formatPrice(data.productPrice)
                tvRowWishlistGridCameraStore.text = data.productStore
                tvRowWishlistGridCameraReview.text = data.productReview.toString()
                tvRowWishlistGridCameraVariant.text = data.productVariant

                mcvRowWishlistGridCameraRemoveFavoriteContainer.setOnClickListener {
                    onDelete?.invoke(data)
                }

                btnRowWishlistGridAddToCart.setOnClickListener {
                    onAddCart?.invoke(data)
                }
            }

            view.setOnClickListener { onClick?.invoke(data) }
        }

    private fun formatPrice(productPrice: Int): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            currencySymbol = "Rp"
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        return "Rp${decimalFormat.format(productPrice)}"
    }
}