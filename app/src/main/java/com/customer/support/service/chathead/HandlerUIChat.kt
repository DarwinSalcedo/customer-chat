package com.customer.support.service.chathead

import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.dao.MessageType
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

    fun addMessage(message: MessageType) {

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

        checkMessage(message)
    }


    fun checkMessage(message: MessageType) {

        UIService.instance.onProcessMessage(
            conversationId = conversationId,
            message = message
        )
        { _messageProcessed ->

            Log.e("TAG", "checkMessage: :: _messageProcessed :: $message")
            if (!_messageProcessed?.message?.value.isNullOrEmpty()) {

                val chatHeads = UIService.instance.chatHeads
                val adapter = chatHeads.content.messagesAdapter

                adapter.messages.add(
                    Message(
                        System.currentTimeMillis().toString(),
                        conversationId,
                        false,
                        MessageType(
                            _messageProcessed?.message?.value ?: "Error",
                            _messageProcessed?.message?.type?:"DEFAULT",
                            _messageProcessed?.message?.mark?:"DEFAULT",
                            _messageProcessed?.message?.key?:""
                        ),
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
                MessageType(message),
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
        text?.let { addMessage(MessageType(text)) }
    }


}
