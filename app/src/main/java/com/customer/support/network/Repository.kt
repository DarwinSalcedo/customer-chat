package com.customer.support.network

import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.utilis.toFormatDate

class Repository {
    suspend fun sendMessages(
        outgoingMessageDao: OutgoingMessageDao
    ): MessageDao? {

        val res = RetrofitInstance.api.sendMessage(outgoingMessageDao)
        res.body()?.let {
            if (res.isSuccessful) {
                return MessageDao(
                    Math.random().toString(),
                    outgoingMessageDao.conversationId,
                    false,
                    res.body()?.message ?: "Error",
                    System.currentTimeMillis().toFormatDate()
                )
            }
        }
        return null
    }
}