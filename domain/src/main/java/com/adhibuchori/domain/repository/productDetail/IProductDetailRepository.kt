package com.adhibuchori.domain.repository.productDetail

import com.adhibuchori.domain.Resource

interface IProductDetailRepository {
    suspend fun getProductDetail(id: String): Resource<ProductDetailModel>
    suspend fun getProductReview(id: String): Resource<List<ProductReviewModel>>
}