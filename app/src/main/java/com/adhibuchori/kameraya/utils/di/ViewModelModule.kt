package com.adhibuchori.kameraya.utils.di

import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.ui.main.home.HomeViewModel
import com.adhibuchori.kameraya.ui.main.notification.NotificationViewModel
import com.adhibuchori.kameraya.ui.main.productDetail.ProductDetailViewModel
import com.adhibuchori.kameraya.ui.main.review.ReviewViewModel
import com.adhibuchori.kameraya.ui.main.search.SearchViewModel
import com.adhibuchori.kameraya.ui.main.payment.cart.CartViewModel
import com.adhibuchori.kameraya.ui.main.payment.checkout.CheckoutViewModel
import com.adhibuchori.kameraya.ui.main.payment.paymentStatus.PaymentStatusViewModel
import com.adhibuchori.kameraya.ui.main.payment.transaction.TransactionViewModel
import com.adhibuchori.kameraya.ui.main.profile.ProfileViewModel
import com.adhibuchori.kameraya.ui.main.wishlist.WishlistViewModel
import com.adhibuchori.kameraya.ui.onboarding.OnBoardingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { OnBoardingViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ReviewViewModel(get(), get()) }
    viewModel { WishlistViewModel(get(), get()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { PaymentStatusViewModel(get(), get()) }
    viewModel { TransactionViewModel(get(), get()) }
    viewModel { NotificationViewModel(get(), get(), get()) }
}