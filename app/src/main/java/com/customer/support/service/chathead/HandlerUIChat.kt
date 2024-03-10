package com.customer.support.service.chathead

import android.util.Log
import com.customer.support.dao.ChatDao
import com.customer.support.dao.OutgoingMessageDao
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
            ChatDao(
                conversationId,
                System.currentTimeMillis().toString(),
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
            OutgoingMessageDao(conversationId = conversationId, message)
        ) { messageDao ->

            if(message.contains("|PROCCESSCONTEXT") ||  message.contains("|SUCCESSCONTEXT")) return@onProcessMessage
            Log.e("TAG", "checkMessage: ", )
            val messageClean = messageDao?.message?.removeSuffix("|CHKPRINTCONFIG")
                ?.removeSuffix("|PROCCESSCONTEXT")?.removeSuffix("|SUCCESSCONTEXT") ?: ""
            if (messageClean.isNotEmpty()) {

                val chatHeads = UIService.instance.chatHeads
                val adapter = chatHeads.content.messagesAdapter

                adapter.messages.add(
                    ChatDao(
                        conversationId,
                        System.currentTimeMillis().toString(),
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
            ChatDao(
                conversationId,
                System.currentTimeMillis().toString(),
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
