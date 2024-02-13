package com.customer.support.adaptor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.activity.SpecificThread
import com.customer.support.dao.MessageDao

class MainActivityAdapter(val msgList: MutableMap<String, MutableList<MessageDao>>): RecyclerView.Adapter<MainActivityAdapter.MainViewHolder>() {

    var my_data: MutableList<MessageDao> = mutableListOf()
    inner class MainViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val body: TextView = view.findViewById(R.id.body)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thread_item, parent, false)
        msgList.forEach{
            my_data.add(it.value[0])
        }
        return MainViewHolder(view)
    }


    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        val currentItem = my_data[position]
        holder.body.text = currentItem.message
        holder.timestamp.text = currentItem.timestamp

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, SpecificThread()::class.java)
            intent.putExtra("position", position)
            intent.putExtra("thread_id",currentItem.conversationId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

}