package com.adhibuchori.data.payment.fullfilment.request

data class FulfillmentItemRequest(
    val productId: String? = null,
    val variantName: String? = null,
    val quantity: Int? = null,
)

data class FulfillmentRequest(
    val payment: String? = null,
    val items: List<FulfillmentItemRequest?>? = null,
)