package com.adhibuchori.kameraya.ui.main.payment.checkout

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.data.utils.toCheckoutModel
import com.adhibuchori.data.utils.toFulfillmentItemParameter
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.notification.NotificationModel
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.payment.checkout.CheckoutModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentItemParameter
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentParameter
import com.adhibuchori.domain.payment.fulfillment.usecase.FulfillTransactionUseCase
import com.adhibuchori.domain.payment.paymentMethod.IPaymentMethodRepository
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    application: Application,
    private val paymentRepository: IPaymentMethodRepository,
    private val fulfillTransactionUseCase: FulfillTransactionUseCase,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : AndroidViewModel(application) {
    init {
        paymentRepository.getListPaymentMethods()
    }

    val paymentList get() = paymentRepository.checkPaymentConfigUpdate()

    private val _fulfillmentData =
        MutableStateFlow<Resource<FulfillmentModel>>(Resource.Loading)
    val fulfillmentData: StateFlow<Resource<FulfillmentModel>> get() = _fulfillmentData

    fun fulfillTransaction(fulfillmentParameter: FulfillmentParameter) {
        viewModelScope.launch {
            _fulfillmentData.value = fulfillTransactionUseCase.invoke(fulfillmentParameter)
        }
    }

    fun mapToFulfillmentParameter(
        cartItems: List<CartModel>,
    ): List<FulfillmentItemParameter?> = cartItems.map { it.toFulfillmentItemParameter() }.toList()


    fun mapToCheckoutModel(
        cartItems: List<CartModel>,
    ): List<CheckoutModel> = cartItems.map { it.toCheckoutModel() }.toList()

    fun getNotificationModelData(data: FulfillmentModel): NotificationModel {
        val context = getApplication<Application>()

        val notificationStatus = if (data.status == true) {
            context.getString(R.string.payment_successful)
        } else {
            context.getString(R.string.payment_failed)
        }

        val notificationDescription = if (!data.invoiceId.isNullOrEmpty()) {
            context.getString(R.string.payment_successful_description, data.invoiceId)
        } else {
            context.getString(R.string.payment_failed_description)
        }

        return NotificationModel(
            notificationStatus = notificationStatus,
            notificationDescription = notificationDescription
        )
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