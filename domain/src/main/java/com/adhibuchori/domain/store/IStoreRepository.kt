package com.adhibuchori.domain.store

import androidx.lifecycle.LiveData
import androidx.paging.PagingData

interface IStoreRepository {
    fun getProducts(
        productsParameter: ProductsParameter,
    ): LiveData<PagingData<ProductsModel>>

    fun searchProducts(query: String): LiveData<PagingData<ProductsModel>>
}