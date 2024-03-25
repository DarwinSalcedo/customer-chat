package com.customer.support.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.domain.QuickButtonQuestion

class QuickResponseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val question = view.findViewById<TextView>(R.id.textQuestion)


    fun bind(data: QuickButtonQuestion, callback: QuickButtonsAdapter.ItemCallback) {
        question.text = data.question

        itemView.setOnClickListener {
            callback.onClick(data)
        }

    }
}