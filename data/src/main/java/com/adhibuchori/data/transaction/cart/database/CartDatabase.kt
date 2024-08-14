package com.adhibuchori.data.transaction.cart.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adhibuchori.data.transaction.cart.dao.CartDao
import com.adhibuchori.data.transaction.cart.entity.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}