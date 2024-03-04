package com.customer.support.network

import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.utilis.toFormatDate
import java.net.UnknownHostException

class Repository {
    suspend fun sendMessages(
        outgoingMessageDao: OutgoingMessageDao
    ): MessageDao? {
        if (outgoingMessageDao.message.startsWith(LOCAL_PREFIX_MARK)) {
            return MessageDao(
                Math.random().toString(),
                outgoingMessageDao.conversationId,
                false,
                quickResponse(outgoingMessageDao.message.removePrefix(LOCAL_PREFIX_MARK)),
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
            put("Necesito cambiar y actualizar los precios", "https://www.youtube.com/watch?v=hx9GHUf1xHs&autoplay=1&fs=1&controls=0&loop=1")
            put("Como aplico descuentos?", "https://www.youtube.com/watch?v=PF_8X9B3to8&autoplay=1&fs=1&controls=0&loop=1")
            put("Como aplico descuentos no youtube?", "https://firebasestorage.googleapis.com/v0/b/android-js-interface.appspot.com/o/Como%20aplicar%20descuentos.mp4?alt=media&token=ff5e1888-a2d6-4b50-bd50-acfb8c9ea746")
            put("Tengo un problema con una impresora", "Revisando configuracion  |CHKPRINTCONFIG")
            put("noconfiguracion", "No detectamos configuracion en tu equipo")
        }

        const val LOCAL_PREFIX_MARK = "answering-from-device:"
    }
}