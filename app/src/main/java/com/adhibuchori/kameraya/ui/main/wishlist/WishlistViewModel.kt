package com.adhibuchori.kameraya.ui.main.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.repository.wishlist.IWishlistRepository
import com.adhibuchori.domain.repository.wishlist.WishlistModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(private val wishlistRepository: IWishlistRepository) : ViewModel() {

    val wishlistItems = wishlistRepository.getAllWishlistItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isListView = MutableLiveData(true)
    val isListView: LiveData<Boolean> get() = _isListView

    fun toggleRecyclerViewLayout() {
        _isListView.value = _isListView.value?.not()
    }

    fun addWishlistItem(item: WishlistModel) {
        viewModelScope.launch {
            wishlistRepository.insertWishlistItem(item)
        }
    }

    fun removeWishlistItem(item: WishlistModel) {
        viewModelScope.launch {
            wishlistRepository.deleteWishlistItem(item)
        }
    }

    fun isItemInWishlist(itemId: String, variantName: String): LiveData<Boolean> {
        return wishlistRepository.getAllWishlistItems().map { items ->
            items.any {
                it.productId == itemId && it.productVariant == variantName
            }
        }.asLiveData()
    }
}