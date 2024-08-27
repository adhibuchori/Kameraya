package com.adhibuchori.kameraya.ui.main.payment.transaction

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.data.utils.toFulfillmentModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.transaction.ITransactionRepository
import com.adhibuchori.domain.payment.transaction.TransactionModel
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: ITransactionRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {
    private val _transactions = MutableLiveData<Resource<List<TransactionModel>>>()
    val transactions: LiveData<Resource<List<TransactionModel>>> get() = _transactions

    fun fetchTransactions() {
        viewModelScope.launch {
            _transactions.value = Resource.Loading
            val result = transactionRepository.getTransactions()
            _transactions.value = result
        }
    }

    fun mapTransactionToFulfillmentModelData(data: TransactionModel): FulfillmentModel {
        return data.toFulfillmentModel()
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