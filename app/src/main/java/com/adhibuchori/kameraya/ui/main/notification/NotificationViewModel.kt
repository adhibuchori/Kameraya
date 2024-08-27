package com.adhibuchori.kameraya.ui.main.notification

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adhibuchori.data.utils.toNotificationModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.notification.NotificationModel
import com.adhibuchori.domain.payment.transaction.ITransactionRepository
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class NotificationViewModel(
    application: Application,
    private val transactionRepository: ITransactionRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : AndroidViewModel(application) {
    private val _notifications = MutableLiveData<Resource<List<NotificationModel>>>()
    val notifications: LiveData<Resource<List<NotificationModel>>> get() = _notifications

    private val notificationList = mutableListOf<NotificationModel>()

    fun fetchNotifications() {
        viewModelScope.launch {
            _notifications.value = Resource.Loading
            val context = getApplication<Application>()
            when (val transactionResult = transactionRepository.getTransactions()) {
                is Resource.Success -> {
                    val notificationData = transactionResult.data.mapNotNull {
                        it.toNotificationModel(
                            paymentSuccessfulText = context.getString(R.string.payment_successful),
                            paymentFailedText = context.getString(R.string.payment_failed),
                            paymentSuccessfulDescriptionText = context.getString(R.string.payment_successful_description),
                            paymentStatusNotAvailableText = context.getString(R.string.payment_failed_description)
                        )
                    }
                    notificationList.clear()
                    notificationList.addAll(notificationData)
                    _notifications.value = Resource.Success(notificationList)
                }

                is Resource.HttpError -> {
                    _notifications.value =
                        Resource.HttpError(transactionResult.errorCode, transactionResult.message)
                }

                is Resource.NetworkError -> {
                    _notifications.value = Resource.NetworkError
                }

                is Resource.Error -> {
                    _notifications.value = Resource.Error(transactionResult.message)
                }

                else -> {
                    Log.e("NotificationViewModel", "Unexpected result type: $transactionResult")
                }
            }
        }
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}