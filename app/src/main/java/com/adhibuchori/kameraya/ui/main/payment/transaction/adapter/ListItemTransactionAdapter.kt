package com.adhibuchori.kameraya.ui.main.payment.transaction.adapter

import android.view.View
import com.adhibuchori.domain.payment.transaction.TransactionModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.ItemRowTransactionBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.formatProductCount
import com.adhibuchori.kameraya.utils.extension.formatTransactionDate
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.bumptech.glide.Glide

class ListItemTransactionAdapter : BaseListAdapter<TransactionModel, ItemRowTransactionBinding>(
    ItemRowTransactionBinding::inflate
) {
    private var onClick: ((TransactionModel) -> Unit)? = null
    private var onReview: ((TransactionModel) -> Unit)? = null

    fun setOnItemClickListener(action: (TransactionModel) -> Unit) {
        onClick = action
    }

    fun setOnReviewListener(action: (TransactionModel) -> Unit) {
        onReview = action
    }

    override fun onItemBind(): (TransactionModel, ItemRowTransactionBinding, View, Int) -> Unit =
        { data, binding, view, _ ->
            with(binding) {
                Glide.with(root.context)
                    .load(data.image)
                    .into(sivRowTransactionCameraImage)
                tvRowTransactionCameraName.text = data.name

                val totalQuantity = data.items?.sumOf { it?.quantity ?: 0 }
                tvRowTransactionTransactionQuantity.text =
                    totalQuantity.formatProductCount(root.context)

                tvRowTransactionTransactionTotal.text = data.total.formatPrice()
                tvRowTransactionTransactionDate.text = data.date.formatTransactionDate(root.context)
                tvRowTransactionTransactionStatus.text =
                    if (data.status == true) {
                        root.context.getString(R.string.transaction_completed)
                    } else {
                        root.context.getString(R.string.transaction_not_completed)
                    }

                if (data.review?.isEmpty() == true) {
                    btnRowTransactionReview.visible()
                } else {
                    btnRowTransactionReview.gone()
                }
                view.setOnClickListener { onClick?.invoke(data) }
                btnRowTransactionReview.setOnClickListener { onReview?.invoke(data) }
            }
        }
}