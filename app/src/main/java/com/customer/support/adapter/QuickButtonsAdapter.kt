package com.customer.support.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.domain.QuickButtonQuestion
import com.customer.support.network.LocalData

class QuickButtonsAdapter(
    var quickButtonQuestions: MutableList<QuickButtonQuestion> = LocalData.defaultOptions,
    val context: Context,
    val itemCallback: ItemCallback
) :
    RecyclerView.Adapter<QuickResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickResponseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quick_response_items, parent, false)
        return QuickResponseViewHolder(view)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setNewQuickButtonQuestions(newList: MutableList<QuickButtonQuestion>) {
        this.quickButtonQuestions = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        this.quickButtonQuestions = LocalData.defaultOptions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return quickButtonQuestions.size
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: QuickResponseViewHolder, position: Int) {
        val currentItem = quickButtonQuestions[position]
        holder.bind(currentItem, this.itemCallback)
    }


    class ItemCallback(val clickListener: (data: QuickButtonQuestion) -> Unit) {
        fun onClick(data: QuickButtonQuestion) = clickListener(data)
    }

}
