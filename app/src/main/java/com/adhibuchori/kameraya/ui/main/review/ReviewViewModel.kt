package com.adhibuchori.kameraya.ui.main.review

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.productDetail.IProductDetailRepository
import com.adhibuchori.domain.productDetail.ProductReviewModel
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val detailRepository: IProductDetailRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {
    private val _productReviews = MutableLiveData<Resource<List<ProductReviewModel>>>()
    val productReviews: LiveData<Resource<List<ProductReviewModel>>> get() = _productReviews

    fun fetchProductReviews(id: String) {
        viewModelScope.launch {
            _productReviews.value = detailRepository.getProductReview(id)
        }
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}