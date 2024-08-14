package com.adhibuchori.kameraya.utils.module

import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.ui.auth.preference.UserViewModel
import com.adhibuchori.kameraya.ui.main.home.HomeViewModel
import com.adhibuchori.kameraya.ui.main.productDetail.ProductDetailViewModel
import com.adhibuchori.kameraya.ui.main.review.ReviewViewModel
import com.adhibuchori.kameraya.ui.main.search.SearchViewModel
import com.adhibuchori.kameraya.ui.main.transaction.cart.CartViewModel
import com.adhibuchori.kameraya.ui.main.wishlist.WishlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {
    viewModel { AuthViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { ProductDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { ReviewViewModel(get()) }
    viewModel { WishlistViewModel(get()) }
    viewModel { CartViewModel(get()) }
}