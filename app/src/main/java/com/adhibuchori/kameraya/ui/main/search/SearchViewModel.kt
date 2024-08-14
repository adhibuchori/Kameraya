package com.adhibuchori.kameraya.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.store.IStoreRepository
import com.adhibuchori.domain.repository.store.ProductsModel
import kotlinx.coroutines.launch

class SearchViewModel(
    private val storeRepository: IStoreRepository,
) : ViewModel() {

    private val _searchResults = MutableLiveData<Resource<List<ProductsModel>>>()
    val searchResults: LiveData<Resource<List<ProductsModel>>> = _searchResults

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _searchResults.value = Resource.Loading
            val result = storeRepository.searchProducts(query)
            _searchResults.value = result
        }
    }
}