package com.customer.support.network

import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.utilis.toFormatDate
import java.net.UnknownHostException

class Repository {
    suspend fun sendMessages(
        outgoingMessageDao: OutgoingMessageDao
    ): MessageDao? {
        try {
            val res = RetrofitInstance.api.sendMessage(outgoingMessageDao)
            res.body()?.let {
                if (res.isSuccessful) {
                    return MessageDao(
                        Math.random().toString(),
                        outgoingMessageDao.conversationId,
                        false,
                        res.body()?.message ?: "Conversacion finalizada",
                        System.currentTimeMillis().toFormatDate()
                    )
                }
            }
        } catch (ex: Exception) {
            if(ex is UnknownHostException){
                return MessageDao(
                    Math.random().toString(),
                    outgoingMessageDao.conversationId,
                    false,
                     "Revisa la conexion de internet",
                    System.currentTimeMillis().toFormatDate()
                )
            }
            return null
        }

        return null
    }
}