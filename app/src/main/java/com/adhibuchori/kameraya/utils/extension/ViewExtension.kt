package com.adhibuchori.kameraya.utils.extension

import android.view.View
import androidx.core.view.isVisible

fun View.gone() {
    isVisible = false
}

fun View.visible() {
    isVisible = true
}

fun String ?.orEmpty() = this ?: ""

fun Int ?.orZero() = this ?: 0