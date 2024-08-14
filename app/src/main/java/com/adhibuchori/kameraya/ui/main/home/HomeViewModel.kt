package com.adhibuchori.kameraya.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.store.IStoreRepository
import com.adhibuchori.domain.repository.store.ProductsModel
import com.adhibuchori.domain.repository.store.ProductsParameter
import kotlinx.coroutines.launch

class HomeViewModel(private val storeRepository: IStoreRepository) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<ProductsModel>>>()
    val products: LiveData<Resource<List<ProductsModel>>> get() = _products

    private val _isListView = MutableLiveData(true)
    val isListView: LiveData<Boolean> get() = _isListView

    private var isFetchEligible = true
    private var currentSort: String? = ""

    fun toggleRecyclerViewLayout() {
        _isListView.value = _isListView.value?.not()
    }

    fun fetchProducts(productsParameter: ProductsParameter) {
        if (isFetchEligible.not() || productsParameter.sort == currentSort) return
        isFetchEligible = false
        currentSort = productsParameter.sort

        viewModelScope.launch {
            _products.value = Resource.Loading
            val result = storeRepository.getProducts(productsParameter)
            _products.value = result
        }
    }

    fun setFetchEligible(isEligible: Boolean) {
        isFetchEligible = isEligible
    }
}