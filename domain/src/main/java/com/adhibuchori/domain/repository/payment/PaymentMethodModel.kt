package com.adhibuchori.domain.repository.payment

data class PaymentItem(
    val label: String,
    val image: String,
    val status: Boolean
)

data class PaymentMethod(
    val title: String,
    val item: List<PaymentItem>
)

data class PaymentResponse(
    val code: Int,
    val message: String,
    val data: List<PaymentMethod>
)