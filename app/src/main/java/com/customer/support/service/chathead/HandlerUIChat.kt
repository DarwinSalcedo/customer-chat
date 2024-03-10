package com.customer.support.service.chathead

import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.dao.MessageRequest
import com.customer.support.network.Repository.Companion.CHKPRINTCONFIG
import com.customer.support.network.Repository.Companion.PROCCESSCONTEXT
import com.customer.support.network.Repository.Companion.SUCCESSCONTEXT
import com.customer.support.service.UIService
import com.customer.support.utilis.toFormatDate

class HandlerUIChat(
    val uiID: String = "customer_support",
    var conversationId: String
) {

    var notifications: Int = 0

    fun updateChannel(newId: String) {
        conversationId = newId
    }

    fun clearMessages() {
        val adapter = UIService.instance.chatHeads.content.messagesAdapter
        adapter.messages = mutableListOf()
        adapter.notifyDataSetChanged()
        UIService.instance.chatHeads.content.clearMessages()
    }

    fun addMessage(message: String, checkMessage: Boolean = true) {

        val chatHeads = UIService.instance.chatHeads

        val adapter = chatHeads.content.messagesAdapter

        val lm = chatHeads.content.layoutManager
        val startIndex = adapter.messages.lastIndex
        adapter.messages.add(
            Message(
                System.currentTimeMillis().toString(),
                conversationId,
                true,
                message,
                System.currentTimeMillis().toFormatDate()
            )
        )

        if (lm.findLastVisibleItemPosition() >= startIndex - 1 && adapter.messages.lastIndex >= 0) {
            adapter.notifyItemInserted(adapter.messages.lastIndex)
        } else {
            adapter.notifyDataSetChanged()
        }

        chatHeads.content.messagesView.post {
            chatHeads.content.messagesView.scrollToPosition(adapter.messages.lastIndex)
        }

        if (checkMessage) checkMessage(message)
    }


    fun checkMessage(message: String) {
        UIService.instance.onProcessMessage(
            MessageRequest(conversationId = conversationId, message)
        ) { messageDao ->

            Log.e("TAG", "checkMessage: ")
            val messageClean = messageDao?.message?.removeSuffix(CHKPRINTCONFIG)
                ?.removeSuffix(PROCCESSCONTEXT)?.removeSuffix(SUCCESSCONTEXT) ?: ""
            if (messageClean.isNotEmpty()) {

                val chatHeads = UIService.instance.chatHeads
                val adapter = chatHeads.content.messagesAdapter

                adapter.messages.add(
                    Message(
                        System.currentTimeMillis().toString(),
                        conversationId,
                        false,
                        messageClean,
                        System.currentTimeMillis().toFormatDate()
                    )
                )

                adapter.notifyItemInserted(adapter.messages.lastIndex)

                chatHeads.content.messagesView.post {
                    chatHeads.content.messagesView.smoothScrollToPosition(adapter.messages.lastIndex)
                    chatHeads.activeChatHead?.notifications = 0
                }
            }
        }
    }

    fun addAgentMessage(message: String) {

        val chatHeads = UIService.instance.chatHeads

        val adapter = chatHeads.content.messagesAdapter

        val lm = chatHeads.content.layoutManager
        val startIndex = adapter.messages.lastIndex
        adapter.messages.add(
            Message(
                System.currentTimeMillis().toString(),
                conversationId,
                false,
                message,
                System.currentTimeMillis().toFormatDate()
            )
        )

        if (lm.findLastVisibleItemPosition() >= startIndex - 1 && adapter.messages.lastIndex >= 0) {
            adapter.notifyItemInserted(adapter.messages.lastIndex)
        } else {
            adapter.notifyDataSetChanged()
        }

        chatHeads.content.messagesView.post {
            chatHeads.content.messagesView.scrollToPosition(adapter.messages.lastIndex)
        }

    }


    fun sendMessage(text: String?) {
        text?.let { addMessage(it) }
    }


}
