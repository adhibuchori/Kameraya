package com.adhibuchori.domain.payment.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    val date: String? = null,
    val image: String? = null,
    val total: Int? = null,
    val review: String? = null,
    val rating: Double? = null,
    val name: String? = null,
    val invoiceId: String? = null,
    val payment: String? = null,
    val time: String? = null,
    val items: List<TransactionModelItem?>? = null,
    val status: Boolean? = null,
) : Parcelable

@Parcelize
data class TransactionModelItem(
    val quantity: Int? = null,
    val productId: String? = null,
    val variantName: String? = null,
) : Parcelable