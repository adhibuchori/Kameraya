package com.adhibuchori.kameraya.ui.main.payment.paymentMethod.adapter

import android.view.View
import com.adhibuchori.domain.repository.payment.PaymentItem
import com.adhibuchori.kameraya.databinding.ItemRowPaymentMethodBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter

class ListPaymentMethodAdapter : BaseListAdapter<PaymentItem, ItemRowPaymentMethodBinding>(
    ItemRowPaymentMethodBinding::inflate
){
    override fun onItemBind(): (PaymentItem, ItemRowPaymentMethodBinding, View, Int) -> Unit =  {
        data, binding, view, position ->
        // TODO: Bind item here

    }
}