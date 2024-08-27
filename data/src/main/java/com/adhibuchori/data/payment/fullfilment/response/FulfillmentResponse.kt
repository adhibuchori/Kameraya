package com.adhibuchori.data.payment.fullfilment.response

data class FulfillmentResponse(
    val code: Int? = null,
    val data: FulfillmentResponseData? = null,
    val message: String? = null,
)

data class FulfillmentResponseData(
    val date: String? = null,
    val total: Int? = null,
    val invoiceId: String? = null,
    val payment: String? = null,
    val time: String? = null,
    val status: Boolean? = null,
)

