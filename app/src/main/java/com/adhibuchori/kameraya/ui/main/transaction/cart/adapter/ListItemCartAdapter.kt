package com.adhibuchori.kameraya.ui.main.transaction.cart.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.ItemRowCartBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

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
                tvRowCartPrice.text = formatPrice(data.productPrice)
                tvRowCartCameraVariant.text = data.productVariant

                val (productStockText, productStockColor) = getProductStockItem(
                    data.productStock,
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

    private fun getProductStockItem(productStock: Int, context: Context): Pair<String, Int> {
        return when {
            productStock > 1 -> Pair(
                context.getString(R.string.camera_stock_available, productStock.toString()),
                ContextCompat.getColor(context, android.R.color.holo_green_dark)
            )

            else -> Pair(
                context.getString(R.string.camera_stock_remaining, productStock.toString()),
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            )
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