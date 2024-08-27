package com.adhibuchori.kameraya.ui.main.payment.paymentStatus

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.payment.rating.RatingParameter
import com.adhibuchori.domain.payment.rating.usecase.SubmitRatingUseCase
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentStatusViewModel(
    private val submitRatingUseCase: SubmitRatingUseCase,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {
    private val _ratingData = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val ratingData: StateFlow<Resource<Unit>> get() = _ratingData

    fun mapToRatingParameter(rating: Int, review: String, invoiceId: String): RatingParameter {
        return RatingParameter(
            invoiceId = invoiceId,
            rating = rating,
            review = review
        )
    }

    fun submitRating(ratingParameter: RatingParameter) {
        viewModelScope.launch {
            _ratingData.value = submitRatingUseCase.invoke(ratingParameter)
        }
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}