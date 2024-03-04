package com.customer.support.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.dao.ChatDao
import com.customer.support.network.Repository
import com.customer.support.utilis.isUrl

class SpecificThreadAdapter(
    var messages: MutableList<ChatDao> = mutableListOf(),
    val context: Context
) :
    RecyclerView.Adapter<SpecificThreadAdapter.SpecificViewHolder>() {


    private val TAG: String = "SpecificThreadAdapter"


    inner class SpecificViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sendermsg = view.findViewById<TextView>(R.id.textViewSenderMessage)
        val senderTime = view.findViewById<TextView>(R.id.SenderTime)

        val receivermsg = view.findViewById<TextView>(R.id.textViewReceiverMessage)
        val agentTime = view.findViewById<TextView>(R.id.AgentTime)
        val playerView: WebView = view.findViewById(R.id.playerView)
        val AgentCard = view.findViewById<View>(R.id.AgentCard)
        val SenderCard = view.findViewById<View>(R.id.SenderCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.specific_thread_items, parent, false)
        messages.sortBy { it.time }

        return SpecificViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: SpecificViewHolder, position: Int) {
        val currentItem = messages[position]
        if (currentItem.sender) {
            holder.sendermsg.text = currentItem.message?.removePrefix(Repository.LOCAL_PREFIX_MARK)
            "Hora:- ${currentItem.time.toString()}".also { holder.senderTime.text = it }
            holder.SenderCard.visibility = View.VISIBLE
            holder.AgentCard.visibility = View.GONE
        } else {

            holder.receivermsg.text = currentItem.message?.removePrefix(Repository.LOCAL_PREFIX_MARK)
            "Hora:- ${currentItem.time.toString()}".also { holder.agentTime.text = it }


            holder.SenderCard.visibility = View.GONE
            holder.AgentCard.visibility = View.VISIBLE

            if (currentItem.message.isUrl()) {
                holder.playerView.visibility = View.VISIBLE
                holder.playerView.loadUrl(currentItem.message!!)
                holder.receivermsg.visibility = View.GONE

               holder.playerView.settings.apply {
                   javaScriptEnabled = true
                   useWideViewPort = true
                   layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
               }

            } else{
                holder.playerView.visibility = View.GONE
                holder.receivermsg.visibility = View.VISIBLE
            }


        }

    }

}
