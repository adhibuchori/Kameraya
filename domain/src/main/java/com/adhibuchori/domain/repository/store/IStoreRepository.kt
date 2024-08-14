package com.adhibuchori.domain.repository.store

import com.adhibuchori.domain.Resource

interface IStoreRepository {
    suspend fun getProducts(
        productsParameter: ProductsParameter,
    ): Resource<List<ProductsModel>>

    suspend fun searchProducts(query: String): Resource<List<ProductsModel>>
}