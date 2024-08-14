package com.adhibuchori.kameraya.utils.base.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder<T : Any, VB : ViewBinding>(
    view: View,
    private val binding: VB,
    private val onItemBind: (T, VB, View, Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bind(item: T) {
        onItemBind(item, binding, itemView, adapterPosition)
    }
}