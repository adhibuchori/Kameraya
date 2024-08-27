package com.adhibuchori.data.payment.fullfilment.request

data class RatingRequest(
    val invoiceId: String? = null,
    val rating: Int? = null,
    val review: String? = null,
)