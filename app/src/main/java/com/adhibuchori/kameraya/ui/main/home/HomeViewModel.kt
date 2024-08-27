package com.adhibuchori.kameraya.ui.main.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.store.IStoreRepository
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class HomeViewModel(
    private val storeRepository: IStoreRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {

    private val _productFilter = MutableLiveData<ProductsParameter>()
    val productFilter: LiveData<ProductsParameter> get() = _productFilter

    private val _isListView = MutableLiveData(true)
    val isListView: LiveData<Boolean> get() = _isListView

    fun toggleRecyclerViewLayout() {
        _isListView.value = _isListView.value?.not()
    }

    fun updateFilter(sort: String) {
        viewModelScope.launch {
            _productFilter.value = ProductsParameter(sort = sort)
        }
    }

    fun fetchInitialData(): LiveData<ProductsParameter> {
        val initialParameter = ProductsParameter(sort = "")
        _productFilter.value = initialParameter
        return productFilter
    }

    fun fetchProducts(productsParameter: ProductsParameter): LiveData<PagingData<ProductsModel>> {
        return storeRepository.getProducts(productsParameter).cachedIn(viewModelScope)
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }

    fun logSelectItem(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }
}