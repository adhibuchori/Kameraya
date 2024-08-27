package com.adhibuchori.kameraya.ui.main.productDetail

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.data.utils.mapProductDetailToCartModel
import com.adhibuchori.data.utils.mapProductDetailToWishlistModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.productDetail.IProductDetailRepository
import com.adhibuchori.domain.productDetail.ProductDetailModel
import com.adhibuchori.domain.productDetail.ProductVariant
import com.adhibuchori.domain.payment.cart.AddCartUseCase
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.wishlist.usecase.AddWishlistItemUseCase
import com.adhibuchori.domain.wishlist.usecase.IsItemInWishlistUseCase
import com.adhibuchori.domain.wishlist.usecase.RemoveWishlistITemUseCase
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val detailRepository: IProductDetailRepository,
    private val addCartUseCase: AddCartUseCase,
    private val addWishlistItemUseCase: AddWishlistItemUseCase,
    private val removeWishlistITemUseCase: RemoveWishlistITemUseCase,
    private val isItemInWishlistUseCase: IsItemInWishlistUseCase,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {
    private val _productDetail = MutableStateFlow<Resource<ProductDetailModel>>(Resource.Loading)
    val productDetail: StateFlow<Resource<ProductDetailModel>> get() = _productDetail

    private val _isItemInWishlist = MutableStateFlow(false)
    val isItemInWishlist = _isItemInWishlist.asStateFlow()

    private fun removeWishlistItem(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val wishlistItem = mapProductDetailToWishlistModel(data, selectedVariant)
            removeWishlistITemUseCase.invoke(wishlistItem)
            isItemInWishlist(data, selectedVariant?.variantName)
        }
    }

    fun handleWishlistItem(
        data: ProductDetailModel?,
        selectedVariant: ProductVariant?,
        onHandle: (Boolean) -> Unit,
    ) {
        val status = _isItemInWishlist.value.not()
        onHandle(status)
        if (status) {
            addWishlistItem(data, selectedVariant)
        } else {
            removeWishlistItem(data, selectedVariant)
        }
    }

    private fun addWishlistItem(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val wishlistItem = mapProductDetailToWishlistModel(data, selectedVariant)
            addWishlistItemUseCase.invoke(wishlistItem)
            isItemInWishlist(data, selectedVariant?.variantName)
        }
    }

    fun isItemInWishlist(
        data: ProductDetailModel?,
        selectedVariant: String?,
    ) {
        viewModelScope.launch {
            val value = isItemInWishlistUseCase.invoke(
                data?.productId.orEmpty(),
                selectedVariant.orEmpty()
            )
            _isItemInWishlist.getAndUpdate { value }
        }
    }

    fun addProductToCart(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val cartItem = mapProductDetailToCartModel(data, selectedVariant)
            addCartUseCase.invoke(cartItem)
        }
    }

    fun mapProductDetailToCart(
        data: ProductDetailModel?,
        selectedVariant: ProductVariant?,
    ): CartModel = mapProductDetailToCartModel(data, selectedVariant)

    fun fetchProductDetail(id: String) {
        viewModelScope.launch {
            _productDetail.value = detailRepository.getProductDetail(id)
        }
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}