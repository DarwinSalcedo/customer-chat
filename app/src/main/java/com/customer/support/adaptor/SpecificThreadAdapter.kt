package com.customer.support.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.dao.ChatDao

class SpecificThreadAdapter(val specificMsgList: MutableList<ChatDao>) :
    RecyclerView.Adapter<SpecificThreadAdapter.SpecificViewHolder>() {

    inner class SpecificViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sendermsg = view.findViewById<TextView>(R.id.textViewSenderMessage)
        val senderId = view.findViewById<TextView>(R.id.UserId)
        val senderTime = view.findViewById<TextView>(R.id.SenderTime)

        val receivermsg = view.findViewById<TextView>(R.id.textViewReceiverMessage)
        val agentId = view.findViewById<TextView>(R.id.AgentId)
        val agentTime = view.findViewById<TextView>(R.id.AgentTime)

        val AgentCard = view.findViewById<View>(R.id.AgentCard)
        val SenderCard = view.findViewById<View>(R.id.SenderCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.specific_thread_items, parent, false)
        specificMsgList.sortBy { it.time }
        return SpecificViewHolder(view)
    }

    override fun getItemCount(): Int {
        return specificMsgList.size
    }

    override fun onBindViewHolder(holder: SpecificViewHolder, position: Int) {
        val currentItem = specificMsgList[position]
        if (currentItem.sender) {
            holder.sendermsg.text = currentItem.message
            "UserID:-${currentItem._id}".also { holder.senderId.text = it }
            "Time:- ${currentItem.time.toString()}".also { holder.senderTime.text = it }
            holder.SenderCard.visibility = View.VISIBLE
            holder.AgentCard.visibility = View.GONE
        } else {
            holder.receivermsg.text = currentItem.message
            "AgentID:-${currentItem._id}".also { holder.agentId.text = it }
            "Time:- ${currentItem.time.toString()}".also { holder.agentTime.text = it }
            holder.SenderCard.visibility = View.GONE
            holder.AgentCard.visibility = View.VISIBLE
        }

    }

}
