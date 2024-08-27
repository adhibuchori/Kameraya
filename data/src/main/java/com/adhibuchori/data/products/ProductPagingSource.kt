package com.adhibuchori.data.products

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adhibuchori.data.store.source.StoreApiService
import com.adhibuchori.data.utils.toProductsModelList
import com.adhibuchori.data.utils.toQueryMap
import com.adhibuchori.data.utils.toRequest
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter

class ProductPagingSource(
    private val storeApiService: StoreApiService,
    private val productsParameter: ProductsParameter
) : PagingSource<Int, ProductsModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductsModel> {
        val page = params.key ?: 1
        return try {
            val request = productsParameter.toRequest(loadSize = params.loadSize, page = page)
            val result = storeApiService.products(request.toQueryMap())
            val storeItems = result.productData?.items?.toProductsModelList() ?: emptyList()
            val hasMore = result.productData?.totalPages?.let { page < it } ?: false
            LoadResult.Page(
                data = storeItems,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (storeItems.isEmpty() || !hasMore) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}