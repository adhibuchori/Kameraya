package com.adhibuchori.domain.payment.fulfillment

data class FulfillmentItemParameter(
    val productId: String? = null,
    val variantName: String? = null,
    val quantity: Int? = null,
)

data class FulfillmentParameter(
    val payment: String? = null,
    val items: List<FulfillmentItemParameter?>? = null,
)