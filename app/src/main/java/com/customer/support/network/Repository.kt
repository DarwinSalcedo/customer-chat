package com.customer.support.network

import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.dao.MessageRequest
import com.customer.support.dao.MessageType
import com.customer.support.network.LocalData.Companion.LOCAL_PREFIX_MARK
import com.customer.support.network.LocalData.Companion.localResponses
import com.customer.support.utilis.toFormatDate
import java.net.UnknownHostException

class Repository {
    suspend fun sendMessages(
        messageRequest: MessageRequest,
        messageType: MessageType
    ): Message? {

        Log.e("TAG", "messageRequest::: " + messageRequest)
        Log.e("TAG", "messageType::: " + messageType)


        if (messageType.mark == LOCAL_PREFIX_MARK) {

            return Message(
                Math.random().toString(),
                messageRequest.conversationId,
                false,
                quickResponse(messageType.key),
                System.currentTimeMillis().toFormatDate()
            )
        } else {
            try {
                val res = RetrofitInstance.api.sendMessage(messageRequest)
                res.body()?.let {
                    if (res.isSuccessful) {
                        return Message(
                            Math.random().toString(),
                            messageRequest.conversationId,
                            false,
                            MessageType(res.body()?.message ?: "Conversacion finalizada"),
                            System.currentTimeMillis().toFormatDate()
                        )
                    }
                }
            } catch (ex: Exception) {
                if (ex is UnknownHostException) {
                    return Message(
                        Math.random().toString(),
                        messageRequest.conversationId,
                        false,
                        MessageType("Revisa la conexion de internet"),
                        System.currentTimeMillis().toFormatDate()
                    )
                }
                return null
            }
        }
        return null
    }

    private fun quickResponse(key: String): MessageType {
        val result =
            localResponses.getOrDefault(
                key = key,
                MessageType("Error procesando la pregunta")
            )

        Log.e("TAG", "quickResponse: " + result.toString())
        return result
    }


}