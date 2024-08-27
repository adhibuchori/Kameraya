package com.adhibuchori.kameraya.ui.main.payment.checkout.adapter

import android.view.View
import com.adhibuchori.domain.payment.paymentMethod.PaymentMethodItemModel
import com.adhibuchori.kameraya.databinding.ItemRowPaymentMethodBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.createGrayscaleFilter
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.setEnableText
import com.adhibuchori.kameraya.utils.extension.setImageColor
import com.adhibuchori.kameraya.utils.extension.visible
import com.bumptech.glide.Glide

class ListItemPaymentMethodAdapter :
    BaseListAdapter<PaymentMethodItemModel, ItemRowPaymentMethodBinding>(
        ItemRowPaymentMethodBinding::inflate
    ) {
    private var onClick: ((PaymentMethodItemModel) -> Unit)? = null

    fun setOnItemClickListener(action: (PaymentMethodItemModel) -> Unit) {
        onClick = action
    }

    override fun onItemBind(): (PaymentMethodItemModel, ItemRowPaymentMethodBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            view.run {
                isEnabled = data.status
                isClickable = data.status
                setOnClickListener { onClick?.invoke(data) }
            }
            with(binding) {
                ivItemRowPaymentMethodArrowNextIcon.setImageColor(data.status)
                Glide.with(root.context)
                    .load(data.image)
                    .into(ivItemRowPaymentMethodImage)
                tvItemRowPaymentMethodName.run {
                    setEnableText(data.status)
                    text = data.label
                }
                ivItemRowPaymentMethodImage.createGrayscaleFilter(data.status)
                if (data.status) {
                    mcvItemRowPaymentMethodUnavailable.gone()
                    ivItemRowPaymentMethodArrowNextIcon.visible()
                    civItemRowPaymentMethodContainerArrowNextIcon.visible()
                } else {
                    mcvItemRowPaymentMethodUnavailable.visible()
                    ivItemRowPaymentMethodArrowNextIcon.gone()
                    civItemRowPaymentMethodContainerArrowNextIcon.gone()
                }
            }
        }
}