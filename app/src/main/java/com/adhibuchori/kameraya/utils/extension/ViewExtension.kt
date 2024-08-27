package com.adhibuchori.kameraya.utils.extension

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.adhibuchori.kameraya.R
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun View.gone() {
    isVisible = false
}

fun View.visible() {
    isVisible = true
}

fun ImageView.createGrayscaleFilter(state: Boolean) {
    val colorMatrix = ColorMatrix()
    if (!state) colorMatrix.setSaturation(0f)
    colorFilter = ColorMatrixColorFilter(colorMatrix)
}

fun TextView.setEnableText(state: Boolean) {
    val color = if (state) R.color.neutral_40 else R.color.neutral_20
    setTextColor(ResourcesCompat.getColor(context.resources, color, null))
}

fun ImageView.setImageColor(state: Boolean) {
    val color = if (state) R.color.color_primary else R.color.neutral_10
    imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, color, null))
}

fun String?.orEmpty() = this ?: ""

fun Int?.orZero() = this ?: 0

fun Double?.orZero() = this ?: 0.0

fun Float?.formatRating(context: Context, sale: Float?): String {
    return String.format(
        context.getString(R.string.camera_review),
        this?.toString(),
        sale?.toString()
    )
}

fun Int?.formatPrice(): String {
    val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        currencySymbol = "Rp"
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
    return "Rp${decimalFormat.format(this)}"
}

fun Int.getProductStockItem(context: Context): Pair<String, Int> {
    return when {
        this > 1 -> Pair(
            context.getString(R.string.camera_stock_available, this.toString()),
            ContextCompat.getColor(context, android.R.color.holo_green_dark)
        )

        else -> Pair(
            context.getString(R.string.camera_stock_remaining, this.toString()),
            ContextCompat.getColor(context, android.R.color.holo_red_dark)
        )
    }
}

fun Int?.formatProductCount(context: Context): String {
    return String.format(
        context.getString(R.string.checkout_item_quantity),
        this.toString()
    )
}

fun String?.formatTransactionDate(context: Context): String {
    return String.format(
        context.getString(R.string.transaction_date),
        this.toString()
    )
}

fun TextPaint.updateTextAppearance(context: Context) {
    this.color = ContextCompat.getColor(context, R.color.color_auth_nav)
    this.isUnderlineText = false
}

fun SpannableStringBuilder.setBoldStyleSpan(termsStart: Int, termsEnd: Int) {
    val boldSpan = StyleSpan(Typeface.BOLD)
    this.setSpan(boldSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun TextView.configureWithSpannable(spannableStringBuilder: SpannableStringBuilder) {
    this.text = spannableStringBuilder
    this.movementMethod = LinkMovementMethod.getInstance()
    this.highlightColor = Color.TRANSPARENT
}

fun Context.createAlertDialog(binding: ViewBinding): AlertDialog {
    return AlertDialog.Builder(this)
        .setView(binding.root)
        .setCancelable(true)
        .create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
}

fun ViewBinding.setDialogMargins() {
    val layoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
    val horizontalMarginInDp = 45
    val horizontalMarginInPx = (horizontalMarginInDp * root.resources.displayMetrics.density).toInt()
    layoutParams.marginStart = horizontalMarginInPx
    layoutParams.marginEnd = horizontalMarginInPx
    root.layoutParams = layoutParams
}

fun AlertDialog.setDialogSize() {
    window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}