package com.adhibuchori.kameraya.ui.main.transaction.checkout.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.adhibuchori.domain.repository.checkout.CheckoutModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.ItemRowCartBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.gone
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ListItemCheckoutAdapter : BaseListAdapter<CheckoutModel, ItemRowCartBinding>(
    ItemRowCartBinding::inflate
) {
    override fun onItemBind(): (CheckoutModel, ItemRowCartBinding, View, Int) -> Unit =
        { data, binding, _, _ ->
            with(binding) {
                mcbRowCartSelectProduct.gone()
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

                tvCartPageCameraCount.text = formatProductCount(root.context, data.productCount)
            }
        }

    private fun formatProductCount(context: Context, productCount: Int?): String {
        return String.format(
            context.getString(R.string.checkout_item_quantity),
            productCount.toString()
        )
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