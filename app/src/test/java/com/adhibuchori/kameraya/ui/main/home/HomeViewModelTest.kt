package com.adhibuchori.kameraya.ui.main.home

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.store.IStoreRepository
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.getOrAwaitValue
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @Mock
    private lateinit var storeRepository: IStoreRepository

    @Mock
    private lateinit var firebaseAnalytics: IFirebaseAnalyticsRepository

    @Mock
    private lateinit var bundle: Bundle

    @get:Rule
    var mainDispatcher = MainDispatcherRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private fun createViewModel(): HomeViewModel {
        val emptyPagingData = PagingData.empty<ProductsModel>()
        val liveData: LiveData<PagingData<ProductsModel>> = MutableLiveData(emptyPagingData)
        lenient().`when`(storeRepository.getProducts(ProductsParameter(sort = ""))).thenReturn(liveData)

        return HomeViewModel(storeRepository, firebaseAnalytics)
    }

    @Test
    fun `toggleRecyclerViewLayout should toggle between list and grid view`() = runTest {
        val viewModel = createViewModel()
        assertEquals(true, viewModel.isListView.getOrAwaitValue())

        viewModel.toggleRecyclerViewLayout()
        assertEquals(false, viewModel.isListView.getOrAwaitValue())

        viewModel.toggleRecyclerViewLayout()
        assertEquals(true, viewModel.isListView.getOrAwaitValue())
    }

    @Test
    fun `updateFilter should update productFilter LiveData`() = runTest {
        val viewModel = createViewModel()
        val expectedSort = "price"

        viewModel.updateFilter(expectedSort)
        advanceUntilIdle()

        val actualFilter = viewModel.productFilter.getOrAwaitValue()
        assertEquals(expectedSort, actualFilter.sort)
    }

    @Test
    fun `fetchInitialData should return ProductsParameter with empty sort`() = runTest {
        val viewModel = createViewModel()

        val initialFilter = viewModel.fetchInitialData().getOrAwaitValue()
        assertEquals("", initialFilter.sort)
    }

    @Test
    fun `fetchProducts should call storeRepository with correct parameters`() = runTest {
        val viewModel = createViewModel()
        val parameter = ProductsParameter(sort = "price")

        val expectedPagingData = PagingData.empty<ProductsModel>()
        `when`(storeRepository.getProducts(parameter)).thenReturn(MutableLiveData(expectedPagingData))

        viewModel.fetchProducts(parameter)
        advanceUntilIdle()

        verify(storeRepository).getProducts(parameter)
    }

    @Test
    fun `logScreenView should call firebaseAnalytics with SCREEN_VIEW event`() = runTest {
        val viewModel = createViewModel()
        viewModel.logScreenView(bundle)
        verify(firebaseAnalytics).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    @Test
    fun `logButtonEvent should call firebaseAnalytics with BUTTON_CLICK event`() = runTest {
        val viewModel = createViewModel()
        viewModel.logButtonEvent(bundle)
        verify(firebaseAnalytics).logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }

    @Test
    fun `logSelectItem should call firebaseAnalytics with SELECT_ITEM event`() = runTest {
        val viewModel = createViewModel()
        viewModel.logSelectItem(bundle)
        verify(firebaseAnalytics).logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }
}
