package com.adhibuchori.domain.payment.paymentMethod

data class PaymentMethodItemModel(
    val label: String = "",
    val image: String = "",
    val status: Boolean = false,
)

data class PaymentMethodModel(
    val title: String = "",
    val item: List<PaymentMethodItemModel> = emptyList(),
)