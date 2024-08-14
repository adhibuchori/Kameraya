package com.adhibuchori.kameraya.ui.main.wishlist.adapter

import android.annotation.SuppressLint
import android.view.View
import com.adhibuchori.data.utils.mapWishlistItemToCartModel
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.domain.repository.wishlist.WishlistModel
import com.adhibuchori.kameraya.databinding.ItemRowWishlistLinearBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ListItemWishlistAdapter : BaseListAdapter<WishlistModel, ItemRowWishlistLinearBinding>(
    ItemRowWishlistLinearBinding::inflate
) {
    private var onClick: ((WishlistModel) -> Unit)? = null
    private var onDelete: ((WishlistModel) -> Unit)? = null
    private var onAddCart: ((CartModel) -> Unit)? = null

    fun setOnItemClickListener(action: (WishlistModel) -> Unit) {
        onClick = action
    }

    fun setOnDeleteItemListener(action: (WishlistModel) -> Unit) {
        onDelete = action
    }

    fun setOnAddCartItemListener(action: (CartModel) -> Unit) {
        onAddCart = action
    }

    @SuppressLint("SetTextI18n")
    override fun onItemBind(): (WishlistModel, ItemRowWishlistLinearBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                tvRowWishlistLinearCameraName.text = data.productName

                Glide.with(root.context)
                    .load(data.productImage)
                    .into(sivRowWishlistLinearCameraImage)

                tvRowWishlistLinearCameraPrice.text = formatPrice(data.productPrice)
                tvRowWishlistLinearCameraStore.text = data.productStore
                tvRowWishlistLinearCameraReview.text = data.productReview.toString()
                tvRowWishlistLinearCameraVariant.text = data.productVariant

                mcvRowWishlistLinearRemoveFavoriteContainer.setOnClickListener {
                    onDelete?.invoke(data)
                }

                mcvRowWishlistLinearAddToCart.setOnClickListener {
                    val cartItem = mapWishlistItemToCartModel(data)
                    onAddCart?.invoke(cartItem)
                }
            }

            view.setOnClickListener {
                onClick?.invoke(data)
            }
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