package com.adhibuchori.data.di

import androidx.room.Room
import com.adhibuchori.data.payment.cart.database.CartDatabase
import com.adhibuchori.data.utils.DatabaseConstant
import com.adhibuchori.data.wishlist.database.WishlistDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            WishlistDatabase::class.java,
            DatabaseConstant.DBWISHLIST
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<WishlistDatabase>().wishlistDao()
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            CartDatabase::class.java,
            DatabaseConstant.DBCART
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<CartDatabase>().cartDao()
    }
}