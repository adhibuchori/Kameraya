package com.adhibuchori.data.productDetail.repositoryImpl

import android.util.Log
import com.adhibuchori.data.productDetail.source.ProductDetailApiService
import com.adhibuchori.data.utils.toProductDetailModel
import com.adhibuchori.data.utils.toProductReviewList
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.productDetail.IProductDetailRepository
import com.adhibuchori.domain.repository.productDetail.ProductDetailModel
import com.adhibuchori.domain.repository.productDetail.ProductReviewModel
import retrofit2.HttpException
import java.io.IOException

class ProductDetailRepositoryImpl(
    private val productDetailApiService: ProductDetailApiService,
) : IProductDetailRepository {

    override suspend fun getProductDetail(id: String): Resource<ProductDetailModel> {
        return try {
            val response = productDetailApiService.getProductDetail(id)
            val productDetail = response.data?.toProductDetailModel()
            if (productDetail != null) {
                Resource.Success(productDetail)
            } else {
                Resource.Error("Product detail is null")
            }
        } catch (e: Exception) {
            Log.d("ProductRepository", "getProductDetail: ${e.message.toString()}")
            when (e) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.HttpError(e.code(), e.message())
                else -> Resource.Error(e.message)
            }
        }
    }

    override suspend fun getProductReview(id: String): Resource<List<ProductReviewModel>> {
        return try {
            val response = productDetailApiService.getProductReviews(id)
            val reviews = response.data?.toProductReviewList() ?: emptyList()
            Resource.Success(reviews)
        } catch (e: Exception) {
            Log.d("ProductRepository", "getProductReview: ${e.message.toString()}")
            when (e) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.HttpError(e.code(), e.message())
                else -> Resource.Error(e.message)
            }
        }
    }
}