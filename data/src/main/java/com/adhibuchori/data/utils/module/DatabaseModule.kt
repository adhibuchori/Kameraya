package com.adhibuchori.data.utils.module

import androidx.room.Room
import com.adhibuchori.data.transaction.cart.database.CartDatabase
import com.adhibuchori.data.utils.Constant
import com.adhibuchori.data.wishlist.database.WishlistDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            WishlistDatabase::class.java,
            Constant.DBWISHLIST
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
            Constant.DBCART
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<CartDatabase>().cartDao()
    }
}