package com.adhibuchori.data.utils.extension

fun String?.orEmpty() = this ?: ""

fun Int?.orZero() = this ?: 0

fun Double?.orZero() = this ?: 0.0