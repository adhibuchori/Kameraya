package com.adhibuchori.data.payment.fullfilment.response

data class TransactionResponse(
    val code: Int? = null,
    val data: List<TransactionResponseData?>? = null,
    val message: String? = null,
)

data class TransactionResponseData(
    val date: String? = null,
    val image: String? = null,
    val total: Int? = null,
    val review: String? = null,
    val rating: Double? = null,
    val name: String? = null,
    val invoiceId: String? = null,
    val payment: String? = null,
    val time: String? = null,
    val items: List<TransactionResponseItem?>? = null,
    val status: Boolean? = null,
)

data class TransactionResponseItem(
    val quantity: Int? = null,
    val productId: String? = null,
    val variantName: String? = null,
)