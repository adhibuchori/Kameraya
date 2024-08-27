package com.adhibuchori.kameraya.ui.main.payment.cart.adapter

import android.annotation.SuppressLint
import android.view.View
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.kameraya.databinding.ItemRowCartBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.getProductStockItem
import com.bumptech.glide.Glide

class ListItemCartAdapter : BaseListAdapter<CartModel, ItemRowCartBinding>(
    ItemRowCartBinding::inflate
) {
    private var onClick: ((CartModel) -> Unit)? = null
    private var onDelete: ((CartModel) -> Unit)? = null
    private var onIncreaseItem: ((CartModel) -> Unit)? = null
    private var onDecreaseItem: ((CartModel) -> Unit)? = null
    private var onCheckboxChanged: ((CartModel, Boolean) -> Unit)? = null

    fun setOnItemClickListener(action: (CartModel) -> Unit) {
        onClick = action
    }

    fun setOnDeleteItemListener(action: (CartModel) -> Unit) {
        onDelete = action
    }

    fun setOnIncreaseListener(action: (CartModel) -> Unit) {
        onIncreaseItem = action
    }

    fun setOnDecreaseListener(action: (CartModel) -> Unit) {
        onDecreaseItem = action
    }

    fun setOnCheckboxChangedListener(action: (CartModel, Boolean) -> Unit) {
        onCheckboxChanged = action
    }

    @SuppressLint("SetTextI18n")
    override fun onItemBind(): (CartModel, ItemRowCartBinding, View, Int) -> Unit =
        { data, binding, _, _ ->
            with(binding) {
                Glide.with(root.context)
                    .load(data.productImage)
                    .into(sivRowCartCameraImage)

                tvRowCartCameraName.text = data.productName
                tvRowCartPrice.text = data.productPrice.formatPrice()
                tvRowCartCameraVariant.text = data.productVariant

                val (productStockText, productStockColor) = data.productStock.getProductStockItem(
                    root.context
                )

                tvRowCartCameraStock.text = productStockText
                tvRowCartCameraStock.setTextColor(productStockColor)

                mcvRowCartLinearRemoveCartContainer.setOnClickListener { onDelete?.invoke(data) }
                mcvRowCartCameraContainer.setOnClickListener { onClick?.invoke(data) }

                tvCartPageCameraCount.text = data.productCount.toString()

                ivCartPageAdditionIcon.setOnClickListener {
                    onIncreaseItem?.invoke(data)
                }

                ivCartPageSubtractionIcon.setOnClickListener {
                    onDecreaseItem?.invoke(data)
                }

                mcbRowCartSelectProduct.run {
                    setOnCheckedChangeListener { _, isChecked ->
                        onCheckboxChanged?.invoke(data, isChecked)
                    }
                    isChecked = data.isChecked
                }
            }
        }
}