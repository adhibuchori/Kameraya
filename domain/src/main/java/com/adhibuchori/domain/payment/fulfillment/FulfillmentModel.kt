package com.adhibuchori.domain.payment.fulfillment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FulfillmentModel(
    val date: String? = null,
    val total: Int? = null,
    val invoiceId: String? = null,
    val payment: String? = null,
    val time: String? = null,
    val status: Boolean? = null,
    val rating: Double? = null,
    val review: String? = null
) : Parcelable