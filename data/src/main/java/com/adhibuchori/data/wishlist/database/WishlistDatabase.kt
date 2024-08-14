package com.adhibuchori.data.wishlist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adhibuchori.data.wishlist.dao.WishlistDao
import com.adhibuchori.data.wishlist.entity.WishlistEntity

@Database(entities = [WishlistEntity::class], version = 1)
abstract class WishlistDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
}