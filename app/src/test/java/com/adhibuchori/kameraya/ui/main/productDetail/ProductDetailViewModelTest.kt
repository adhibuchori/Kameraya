package com.adhibuchori.kameraya.ui.main.productDetail

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.productDetail.IProductDetailRepository
import com.adhibuchori.domain.productDetail.ProductDetailModel
import com.adhibuchori.domain.productDetail.ProductVariant
import com.adhibuchori.domain.payment.cart.AddCartUseCase
import com.adhibuchori.domain.wishlist.usecase.AddWishlistItemUseCase
import com.adhibuchori.domain.wishlist.usecase.IsItemInWishlistUseCase
import com.adhibuchori.domain.wishlist.usecase.RemoveWishlistITemUseCase
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProductDetailViewModelTest {

    @Mock
    private lateinit var detailRepository: IProductDetailRepository

    @Mock
    private lateinit var addCartUseCase: AddCartUseCase

    @Mock
    private lateinit var addWishlistItemUseCase: AddWishlistItemUseCase

    @Mock
    private lateinit var removeWishlistITemUseCase: RemoveWishlistITemUseCase

    @Mock
    private lateinit var isItemInWishlistUseCase: IsItemInWishlistUseCase

    @Mock
    private lateinit var firebaseAnalytics: IFirebaseAnalyticsRepository

    @Mock
    private lateinit var bundle: Bundle

    private lateinit var viewModel: ProductDetailViewModel

    @get:Rule
    var mainDispatcher = MainDispatcherRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private fun createViewModel(): ProductDetailViewModel {
        return ProductDetailViewModel(
            detailRepository,
            addCartUseCase,
            addWishlistItemUseCase,
            removeWishlistITemUseCase,
            isItemInWishlistUseCase,
            firebaseAnalytics
        )
    }

    @Test
    fun `fetchProductDetail should set productDetail StateFlow to Loading initially`() = runTest {
        viewModel = createViewModel()
        viewModel.fetchProductDetail("product_id")

        assertEquals(Resource.Loading, viewModel.productDetail.first())
    }

    @Test
    fun `fetchProductDetail should call detailRepository with correct id`() = runTest {
        viewModel = createViewModel()
        val productDetailModel = ProductDetailModel(
            productId = "product_id",
            productName = "Sample Product",
            productPrice = 1000,
            image = listOf("image_url"),
            brand = "Sample Brand",
            description = "Sample Description",
            store = "Sample Store",
            sale = 10,
            stock = 100,
            totalRating = 4.5,
            totalReview = 50,
            totalSatisfaction = 80,
            productRating = 4.5,
            productVariant = listOf(ProductVariant("Variant 1", 1000))
        )
        `when`(detailRepository.getProductDetail("product_id")).thenReturn(
            Resource.Success(
                productDetailModel
            )
        )

        viewModel.fetchProductDetail("product_id")
        advanceUntilIdle()

        verify(detailRepository).getProductDetail("product_id")
    }

    @Test
    fun `logScreenView should call firebaseAnalytics with SCREEN_VIEW event`() = runTest {
        viewModel = createViewModel()
        viewModel.logScreenView(bundle)

        verify(firebaseAnalytics).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    @Test
    fun `logButtonEvent should call firebaseAnalytics with BUTTON_CLICK event`() = runTest {
        viewModel = createViewModel()
        viewModel.logButtonEvent(bundle)

        verify(firebaseAnalytics).logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}