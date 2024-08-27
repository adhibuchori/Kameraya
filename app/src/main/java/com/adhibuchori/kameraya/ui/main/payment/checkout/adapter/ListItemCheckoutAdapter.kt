package com.adhibuchori.kameraya.ui.main.payment.checkout.adapter

import android.view.View
import com.adhibuchori.domain.payment.checkout.CheckoutModel
import com.adhibuchori.kameraya.databinding.ItemRowCartBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.formatProductCount
import com.adhibuchori.kameraya.utils.extension.getProductStockItem
import com.adhibuchori.kameraya.utils.extension.gone
import com.bumptech.glide.Glide

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
                tvRowCartPrice.text = data.productPrice.formatPrice()
                tvRowCartCameraVariant.text = data.productVariant

                val (productStockText, productStockColor) = data.productStock.getProductStockItem(root.context)
                tvRowCartCameraStock.text = productStockText
                tvRowCartCameraStock.setTextColor(productStockColor)

                tvCartPageCameraCount.text = data.productCount.formatProductCount(root.context)
                mcvRowCartLinearRemoveCartContainer.gone()
                ivCartPageSubtractionIcon.gone()
                ivCartPageAdditionIcon.gone()
            }
        }
}