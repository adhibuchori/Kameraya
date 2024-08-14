package com.adhibuchori.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.adhibuchori.data.auth.preference.AuthPreference
import com.adhibuchori.data.auth.repositoryImpl.AuthRepositoryImpl
import com.adhibuchori.data.transaction.cart.repositoryImpl.CartRepositoryImpl
import com.adhibuchori.data.productDetail.repositoryImpl.ProductDetailRepositoryImpl
import com.adhibuchori.data.store.repositoryImpl.StoreRepositoryImpl
import com.adhibuchori.data.wishlist.repositoryImpl.WishlistRepositoryImpl
import com.adhibuchori.domain.repository.IAuthRepository
import com.adhibuchori.domain.repository.cart.AddCartUseCase
import com.adhibuchori.domain.repository.cart.ICartRepository
import com.adhibuchori.domain.repository.productDetail.IProductDetailRepository
import com.adhibuchori.domain.repository.store.IStoreRepository
import com.adhibuchori.domain.repository.wishlist.IWishlistRepository
import com.adhibuchori.domain.repository.wishlist.useCase.AddWishlistItemUseCase
import com.adhibuchori.domain.repository.wishlist.useCase.IsItemInWishlistUseCase
import com.adhibuchori.domain.repository.wishlist.useCase.RemoveWishlistITemUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USER_PREFERENCES_NAME = "auth"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

val appModule = module {
    single { androidContext().dataStore }
    single { AuthPreference.getInstance(get()) }
    single<IAuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<IStoreRepository> { StoreRepositoryImpl(get()) }
    single<IProductDetailRepository> { ProductDetailRepositoryImpl(get()) }
    single<IWishlistRepository> { WishlistRepositoryImpl(get()) }
    single<ICartRepository> { CartRepositoryImpl(get()) }

    single { AddCartUseCase(get()) }
    single { RemoveWishlistITemUseCase(get()) }
    single { AddWishlistItemUseCase(get()) }
    single { IsItemInWishlistUseCase(get()) }
}