package com.adhibuchori.kameraya.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.adhibuchori.domain.repository.store.ProductsModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.ItemRowCameraStoreLinearBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ListItemStoreAdapter : BaseListAdapter<ProductsModel, ItemRowCameraStoreLinearBinding>(
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
                tvRowCameraStoreLinearCameraPrice.text = formatPrice(data.productPrice)
                tvRowCameraStoreLinearCameraStore.text = data.store
                tvRowCameraStoreLinearCameraReview.text = formatRating(
                    root.context,
                    data.productRating,
                    data.sale
                )
            }
            view.setOnClickListener { onClick?.invoke(data) }
        }

    private fun formatRating(context: Context, productRating: Float?, sale: Float?): String {
        return String.format(
            context.getString(R.string.camera_review),
            productRating.toString(),
            sale.toString()
        )
    }

    private fun formatPrice(productPrice: Float?): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            currencySymbol = "Rp"
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        return "Rp${decimalFormat.format(productPrice)}"
    }
}