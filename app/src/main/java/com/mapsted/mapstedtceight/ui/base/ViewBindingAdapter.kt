package com.mapsted.mapstedtceight.ui.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mapsted.mapstedtceight.ui.base.ViewBindingAdapter.ListenerVB
import com.mapsted.mapstedtceight.utils.ViewInflate

open class ViewBindingAdapter<E, B : ViewBinding>(
    var dataItemList: MutableList<E> = ArrayList(),
    private val bindingReference: ViewInflate<B>,
) : RecyclerView.Adapter<ViewBindingAdapter<E, B>.ViewHolder<E, B>>() {

    override fun getItemCount(): Int {
        return dataItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        super.onAttachedToRecyclerView(recyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyAllItems() {
        notifyDataSetChanged()
    }

    inner class ViewHolder<E, B : ViewBinding>(val binding: B, var dataItem: E, var index: Int) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder<E, B> {
        val binding = bindingReference.invoke(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, dataItemList[position], position)
    }

    override fun onBindViewHolder(holder: ViewHolder<E, B>, position: Int) {
        listener.onBind(holder.binding, dataItemList[position], position)
    }

    var listener: ListenerVB<E, B> = ListenerVB { itemBinding, dataItem, position ->

    }

    fun interface ListenerVB<E, B : ViewBinding> {
        fun onBind(itemBinding: B, dataItem: E, position: Int)
    }
}