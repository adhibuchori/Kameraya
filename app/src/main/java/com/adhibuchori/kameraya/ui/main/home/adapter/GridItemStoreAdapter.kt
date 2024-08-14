package com.adhibuchori.kameraya.ui.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.adhibuchori.domain.repository.store.ProductsModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.ItemRowCameraStoreGridBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class GridItemStoreAdapter : BaseListAdapter<ProductsModel, ItemRowCameraStoreGridBinding>(
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
                Glide.with(binding.root.context)
                    .load(data.image)
                    .into(sivRowCameraStoreGridCameraImage)

                tvRowCameraStoreGridCameraName.text = data.productName
                tvRowCameraStoreGridCameraPrice.text = formatPrice(data.productPrice)
                tvRowCameraStoreGridCameraStore.text = data.store
                tvRowWishlistGridCameraReview.text = formatRating(
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