package com.adhibuchori.domain.payment.paymentMethod

import androidx.lifecycle.LiveData
import com.adhibuchori.domain.Resource

interface IPaymentMethodRepository {
    fun checkPaymentConfigUpdate(): LiveData<Resource<List<PaymentMethodModel>>>
    fun getListPaymentMethods()
}