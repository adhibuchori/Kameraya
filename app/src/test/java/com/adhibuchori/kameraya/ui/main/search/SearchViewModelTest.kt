package com.adhibuchori.kameraya.ui.main.search

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.store.IStoreRepository
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.getOrAwaitValue
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

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

    private fun createViewModel(): SearchViewModel {
        return SearchViewModel(storeRepository, firebaseAnalytics)
    }

    @Test
    fun `setSearchQuery should update searchQuery LiveData`() = runTest {
        val viewModel = createViewModel()
        val query = "test query"

        viewModel.setSearchQuery(query)

        assertEquals(query, viewModel.searchQuery.getOrAwaitValue())
    }

    @Test
    fun `searchProducts should return correct PagingData for the query`() = runTest {
        val viewModel = createViewModel()
        val query = "Phincon Camera"
        val expectedPagingData = PagingData.empty<ProductsModel>()

        `when`(storeRepository.searchProducts(query)).thenReturn(MutableLiveData())

        viewModel.searchProducts(query).observeForever { pagingData ->
            assertEquals(expectedPagingData, pagingData)
        }

        runBlocking {
            delay(1000)
            val liveData = viewModel.searchProducts(query) as MutableLiveData
            liveData.postValue(expectedPagingData)
        }
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