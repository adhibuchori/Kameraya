package com.adhibuchori.kameraya.ui.main.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.productDetail.IProductDetailRepository
import com.adhibuchori.domain.repository.productDetail.ProductReviewModel
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val detailRepository: IProductDetailRepository,
) : ViewModel() {
    private val _productReviews = MutableLiveData<Resource<List<ProductReviewModel>>>()
    val productReviews: LiveData<Resource<List<ProductReviewModel>>> get() = _productReviews

    fun fetchProductReviews(id: String) {
        viewModelScope.launch {
            _productReviews.value = detailRepository.getProductReview(id)
        }
    }
}