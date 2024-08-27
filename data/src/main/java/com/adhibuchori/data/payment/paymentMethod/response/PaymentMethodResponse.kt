package com.adhibuchori.data.payment.paymentMethod.response

data class PaymentMethodResponse(
    val code: Int? = null,
    val data: List<PaymentMethod?>? = null,
    val message: String? = null,
)

data class PaymentMethodItem(
    val image: String? = null,
    val label: String? = null,
    val status: Boolean? = null,
)

data class PaymentMethod(
    val item: List<PaymentMethodItem?>? = null,
    val title: String? = null,
)