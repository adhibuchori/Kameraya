package com.adhibuchori.kameraya.utils.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<T, VB : ViewBinding>(
    private val itemList: List<T>,
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseRecyclerViewAdapter<T, VB>.BaseViewHolder>() {

    private var onItemClickListener: ((T) -> Unit)? = null

    abstract fun bindViews(binding: VB, data: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater(inflater, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    inner class BaseViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: T) {
            bindViews(binding, data)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(data)
            }
        }
    }
}