package com.customer.support.network

import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.dao.MessageRequest
import com.customer.support.utilis.toFormatDate
import java.net.UnknownHostException

class Repository {
    suspend fun sendMessages(
        messageRequest: MessageRequest
    ): Message? {
        Log.e("TAG", "sendMessages::: " + messageRequest)
        if (messageRequest.message.startsWith(LOCAL_PREFIX_MARK)) {
            return Message(
                Math.random().toString(),
                messageRequest.conversationId,
                false,
                quickResponse(messageRequest.message.removePrefix(LOCAL_PREFIX_MARK)),
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
                            res.body()?.message ?: "Conversacion finalizada",
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
        return localResponses.getOrDefault(key = question.trim(), "Error procesando la pregunta")
    }

    companion object {
        val localResponses = buildMap {
            put(
                "Necesito cambiar y actualizar los precios",
                "https://www.youtube.com/watch?v=hx9GHUf1xHs&autoplay=1&fs=1&controls=0&loop=1"
            )
            put(
                "Como aplico descuentos?",
                "https://www.youtube.com/watch?v=PF_8X9B3to8&autoplay=1&fs=1&controls=0&loop=1"
            )
            put(
                "Como aplico descuentos no youtube?",
                "https://firebasestorage.googleapis.com/v0/b/android-js-interface.appspot.com/o/Como%20aplicar%20descuentos.mp4?alt=media&token=ff5e1888-a2d6-4b50-bd50-acfb8c9ea746"
            )
            put("Tengo un problema con una impresora", "Revisando configuracion  |CHKPRINTCONFIG")
            put("0 |PROCCESSCONTEXT", "Seleccion : 0 |PROCCESSCONTEXT")
            put("1 |PROCCESSCONTEXT", "Seleccion : 1 |PROCCESSCONTEXT")
            put("0 |SUCCESSCONTEXT", "Seleccion : 0 |SUCCESSCONTEXT")
            put("1 |SUCCESSCONTEXT", "Seleccion : 1 |SUCCESSCONTEXT")
        }

        const val LOCAL_PREFIX_MARK = "answering-from-device:"
        const val PROCCESSCONTEXT = "|PROCCESSCONTEXT"
        const val SUCCESSCONTEXT = "|SUCCESSCONTEXT"
        const val CHKPRINTCONFIG = "|CHKPRINTCONFIG"
    }
}