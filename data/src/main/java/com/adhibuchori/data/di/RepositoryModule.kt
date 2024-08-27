package com.adhibuchori.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.adhibuchori.data.utils.preference.AppPreference
import com.adhibuchori.data.auth.repositoryImpl.AuthRepositoryImpl
import com.adhibuchori.data.firebaseAnalytics.FirebaseAnalyticsRepositoryImpl
import com.adhibuchori.data.payment.cart.repositoryImpl.CartRepositoryImpl
import com.adhibuchori.data.payment.fullfilment.repositoryImpl.FulfillmentRepositoryImpl
import com.adhibuchori.data.payment.fullfilment.repositoryImpl.RatingRepositoryImpl
import com.adhibuchori.data.payment.fullfilment.repositoryImpl.TransactionRepositoryImpl
import com.adhibuchori.data.payment.paymentMethod.repositoryImpl.PaymentMethodRepositoryImpl
import com.adhibuchori.data.productDetail.repositoryImpl.ProductDetailRepositoryImpl
import com.adhibuchori.data.store.repositoryImpl.StoreRepositoryImpl
import com.adhibuchori.data.wishlist.repositoryImpl.WishlistRepositoryImpl
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.domain.payment.cart.AddCartUseCase
import com.adhibuchori.domain.payment.cart.ICartRepository
import com.adhibuchori.domain.payment.fulfillment.IFulfillmentRepository
import com.adhibuchori.domain.payment.fulfillment.usecase.FulfillTransactionUseCase
import com.adhibuchori.domain.payment.paymentMethod.IPaymentMethodRepository
import com.adhibuchori.domain.payment.rating.IRatingRepository
import com.adhibuchori.domain.payment.rating.usecase.SubmitRatingUseCase
import com.adhibuchori.domain.payment.transaction.ITransactionRepository
import com.adhibuchori.domain.productDetail.IProductDetailRepository
import com.adhibuchori.domain.store.IStoreRepository
import com.adhibuchori.domain.wishlist.IWishlistRepository
import com.adhibuchori.domain.wishlist.usecase.AddWishlistItemUseCase
import com.adhibuchori.domain.wishlist.usecase.IsItemInWishlistUseCase
import com.adhibuchori.domain.wishlist.usecase.RemoveWishlistITemUseCase
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USER_PREFERENCES_NAME = "auth"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

val repositoryModule = module {
    single { androidContext().dataStore }
    single { Firebase.remoteConfig }
    single { Firebase.analytics }
    single { AppPreference.getInstance(get()) }
    single<IAuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<IStoreRepository> { StoreRepositoryImpl(get()) }
    single<IProductDetailRepository> { ProductDetailRepositoryImpl(get()) }
    single<IWishlistRepository> { WishlistRepositoryImpl(get()) }
    single<ICartRepository> { CartRepositoryImpl(get()) }
    single<IPaymentMethodRepository> { PaymentMethodRepositoryImpl(get()) }
    single<IFulfillmentRepository> { FulfillmentRepositoryImpl(get()) }
    single<IRatingRepository> { RatingRepositoryImpl(get()) }
    single<ITransactionRepository> { TransactionRepositoryImpl(get()) }
    single<IFirebaseAnalyticsRepository> { FirebaseAnalyticsRepositoryImpl(get()) }

    single { AddCartUseCase(get()) }
    single { RemoveWishlistITemUseCase(get()) }
    single { AddWishlistItemUseCase(get()) }
    single { IsItemInWishlistUseCase(get()) }
    single { FulfillTransactionUseCase(get()) }
    single { SubmitRatingUseCase(get()) }
}