package com.adhibuchori.kameraya.ui.main.wishlist.adapter

import android.annotation.SuppressLint
import android.view.View
import com.adhibuchori.data.utils.mapWishlistItemToCartModel
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.wishlist.WishlistModel
import com.adhibuchori.kameraya.databinding.ItemRowWishlistLinearBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.bumptech.glide.Glide

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

                tvRowWishlistLinearCameraPrice.text = data.productPrice.formatPrice()
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
}