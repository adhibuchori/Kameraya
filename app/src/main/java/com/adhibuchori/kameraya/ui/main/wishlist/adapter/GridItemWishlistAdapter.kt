package com.adhibuchori.kameraya.ui.main.wishlist.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.adhibuchori.data.utils.mapWishlistItemToCartModel
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.wishlist.WishlistModel
import com.adhibuchori.kameraya.databinding.ItemRowWishlistGridBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.bumptech.glide.Glide

class GridItemWishlistAdapter : BaseListAdapter<WishlistModel, ItemRowWishlistGridBinding>(
    ItemRowWishlistGridBinding::inflate
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
    override fun onItemBind(): (WishlistModel, ItemRowWishlistGridBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                sivRowWishlistGridCameraImage.run {
                    Glide.with(context)
                        .load(data.productImage)
                        .into(this)
                    post {
                        val lp = layoutParams as ConstraintLayout.LayoutParams
                        lp.height = measuredWidth
                        layoutParams = lp
                        requestLayout()
                    }
                }
                tvRowWishlistGridCameraName.text = data.productName

                tvRowWishlistGridCameraPrice.text = data.productPrice.formatPrice()
                tvRowWishlistGridCameraStore.text = data.productStore
                tvRowWishlistGridCameraReview.text = data.productReview.toString()
                tvRowWishlistGridCameraVariant.text = data.productVariant

                mcvRowWishlistGridCameraRemoveFavoriteContainer.setOnClickListener {
                    onDelete?.invoke(data)
                }

                btnRowWishlistGridAddToCart.setOnClickListener {
                    val cartItem = mapWishlistItemToCartModel(data)
                    onAddCart?.invoke(cartItem)
                }
            }

            view.setOnClickListener { onClick?.invoke(data) }
        }
}