package com.adhibuchori.kameraya.ui.main.wishlist

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.wishlist.IWishlistRepository
import com.adhibuchori.domain.wishlist.WishlistModel
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: IWishlistRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {

    val wishlistItems = wishlistRepository.getAllWishlistItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isListView = MutableLiveData(true)
    val isListView: LiveData<Boolean> get() = _isListView

    fun toggleRecyclerViewLayout() {
        _isListView.value = _isListView.value?.not()
    }

    fun removeWishlistItem(item: WishlistModel) {
        viewModelScope.launch {
            wishlistRepository.deleteWishlistItem(item)
        }
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