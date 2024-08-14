package com.adhibuchori.data.store.repositoryImpl

import android.util.Log
import com.adhibuchori.data.store.request.ProductsRequest
import com.adhibuchori.data.store.source.StoreApiService
import com.adhibuchori.data.utils.toQueryMap
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.store.IStoreRepository
import com.adhibuchori.domain.repository.store.ProductsModel
import com.adhibuchori.domain.repository.store.ProductsParameter
import retrofit2.HttpException
import java.io.IOException

class StoreRepositoryImpl(
    private val storeApiService: StoreApiService,
) : IStoreRepository {

    override suspend fun getProducts(
        productsParameter: ProductsParameter,
    ): Resource<List<ProductsModel>> {
        return try {
            val request = ProductsRequest(
                search = productsParameter.search,
                brand = productsParameter.brand,
                lowest = productsParameter.lowest,
                highest = productsParameter.highest,
                sort = productsParameter.sort,
                limit = productsParameter.limit,
                page = productsParameter.page
            )

            val response = storeApiService.products(request.toQueryMap())
            val storeItems = response.data?.items?.mapNotNull { item ->
                item?.let {
                    ProductsModel(
                        productId = it.productId.orEmpty(),
                        productName = it.productName,
                        productPrice = it.productPrice?.toFloat(),
                        image = it.image,
                        brand = it.brand,
                        store = it.store,
                        sale = it.sale?.toFloat(),
                        productRating = it.productRating
                    )
                }
            } ?: emptyList()
            Resource.Success(storeItems)
        } catch (e: Exception) {
            Log.d("StoreRepository", "getProducts: ${e.message.toString()}")
            when (e) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.HttpError(e.code(), e.message())
                else -> Resource.Error(e.message)
            }
        }
    }

    override suspend fun searchProducts(query: String): Resource<List<ProductsModel>> {
        return try {
            val queryMap = mapOf("search" to query)
            val response = storeApiService.products(queryMap)
            val searchResults = response.data?.items?.mapNotNull { item ->
                item?.let {
                    ProductsModel(
                        productId = it.productId,
                        productName = it.productName,
                        productPrice = it.productPrice?.toFloat(),
                        image = it.image,
                        brand = it.brand,
                        store = it.store,
                        sale = it.sale?.toFloat(),
                        productRating = it.productRating
                    )
                }
            } ?: emptyList()

            Resource.Success(searchResults)
        } catch (e: Exception) {
            Log.d("StoreRepository", "searchProducts: ${e.message.toString()}")
            when (e) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.HttpError(e.code(), e.message())
                else -> Resource.Error(e.message)
            }
        }
    }
}