package com.customer.support.network

import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.utilis.toFormatDate
import java.net.UnknownHostException

class Repository {
    suspend fun sendMessages(
        outgoingMessageDao: OutgoingMessageDao
    ): MessageDao? {
        if (outgoingMessageDao.message.startsWith("answering-from-device:")) {
            return MessageDao(
                Math.random().toString(),
                outgoingMessageDao.conversationId,
                false,
                quickResponse(outgoingMessageDao.message.removePrefix("answering-from-device:")),
                System.currentTimeMillis().toFormatDate()
            )
        } else {
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
                if (ex is UnknownHostException) {
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
        }
        return null
    }

    private fun quickResponse(question: String): String {

        return localResponses.getOrDefault(key = question, "Error procesando la pregunta")

    }

    companion object {
        val localResponses = buildMap<String, String> {
            put("Como aplico descuentos?", "https://s3.ca-central-1.amazonaws.com/codingwithmitch/media/VideoPlayerRecyclerView/Rest+api+teaser+video.mp4")
           // put("Como aplico descuentos?", "https://www.youtube.com/watch?v=PF_8X9B3to8")
        }
    }
}