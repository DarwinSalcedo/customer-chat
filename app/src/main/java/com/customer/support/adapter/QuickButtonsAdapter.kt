package com.customer.support.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.domain.QuickButtonQuestion
import com.customer.support.network.Repository.Companion.LOCAL_PREFIX_MARK

class QuickButtonsAdapter(
    var quickButtonQuestions: MutableList<QuickButtonQuestion> = defaultOptions,
    val context: Context,
    val itemCallback: ItemCallback
) :
    RecyclerView.Adapter<QuickResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickResponseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quick_response_items, parent, false)
        return QuickResponseViewHolder(view)
    }


    fun setNewQuickButtonQuestions(newList: MutableList<QuickButtonQuestion>) {
        this.quickButtonQuestions = newList
        notifyDataSetChanged()
    }
    fun reset() {
        this.quickButtonQuestions = defaultOptions
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

    companion object {
        val defaultOptions = mutableListOf(
            QuickButtonQuestion(
                "Tengo un problema con una impresora",
                 "k1"
            ),
            QuickButtonQuestion(
                "Como aplico descuentos?",
                  "k2"
            ),
            QuickButtonQuestion(
                "Necesito cambiar y actualizar los precios",
                 "k3"
            )
        )
    }

    class ItemCallback(val clickListener: (data: QuickButtonQuestion) -> Unit) {
        fun onClick(data: QuickButtonQuestion) = clickListener(data)
    }

}
