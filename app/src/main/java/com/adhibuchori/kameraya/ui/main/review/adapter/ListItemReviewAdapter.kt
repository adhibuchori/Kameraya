package com.adhibuchori.kameraya.ui.main.review.adapter

import android.view.View
import com.adhibuchori.domain.repository.productDetail.ProductReviewModel
import com.adhibuchori.kameraya.databinding.ItemRowReviewBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter
import com.bumptech.glide.Glide

class ListItemReviewAdapter : BaseListAdapter<ProductReviewModel, ItemRowReviewBinding>(
    ItemRowReviewBinding::inflate
) {
    override fun onItemBind(): (ProductReviewModel, ItemRowReviewBinding, View, Int) -> Unit =
        { data, binding, _, _ ->
            with(binding) {
                tvRowReviewUserName.text = data.userName

                Glide.with(binding.root.context)
                    .load(data.userImage)
                    .into(ivRowReviewUserImage)

                tvRowReviewUserReviewData.text = data.userReview
                rbRowReviewUserRating.rating = data.userRating.toFloat()
            }
        }
}