package com.adhibuchori.data.payment.cart.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adhibuchori.data.payment.cart.dao.CartDao
import com.adhibuchori.data.payment.cart.entity.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}