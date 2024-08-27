package com.adhibuchori.data.store.repositoryImpl

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.adhibuchori.data.products.ProductPagingSource
import com.adhibuchori.data.store.source.StoreApiService
import com.adhibuchori.domain.store.IStoreRepository
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter

class StoreRepositoryImpl(
    private val storeApiService: StoreApiService,
) : IStoreRepository {

    override fun getProducts(
        productsParameter: ProductsParameter,
    ): LiveData<PagingData<ProductsModel>> {
        val productPagingSource = ProductPagingSource(storeApiService, productsParameter)
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {productPagingSource}
        ).liveData
    }

    override fun searchProducts(
        query: String
    ): LiveData<PagingData<ProductsModel>> {
        val searchParameters = ProductsParameter(search = query)
        val searchPagingSource = ProductPagingSource(storeApiService, searchParameters)
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { searchPagingSource }
        ).liveData
    }
}