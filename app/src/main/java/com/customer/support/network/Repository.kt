package com.customer.support.network

import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.dao.MessageRequest
import com.customer.support.dao.MessageType
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

    companion object {
        val localResponses = buildMap {

            put(
                "k2",
                MessageType("https://www.youtube.com/watch?v=PF_8X9B3to8&autoplay=1&fs=1&controls=0&loop=1")
            )
            put(
                "k3",
                MessageType("https://www.youtube.com/watch?v=hx9GHUf1xHs&autoplay=1&fs=1&controls=0&loop=1")
            )
            put(
                "k33",
                MessageType("https://firebasestorage.googleapis.com/v0/b/android-js-interface.appspot.com/o/Como%20aplicar%20descuentos.mp4?alt=media&token=ff5e1888-a2d6-4b50-bd50-acfb8c9ea746")
            )
            put(
                "k1",
                MessageType("Revisando configuracion", CHKPRINTCONFIG)
            )
            put(
                "kpc1",
                MessageType("Procediendo a cancelar", PROCCESSCONTEXTCANCELAR)
            )
            put(
                "kpc2",
                MessageType("Aguarde unos segundos, reintentando impresion.", PROCCESSCONTEXT)
            )

            put(
                "scp3",
                MessageType("Cancelando ", SUCCESSCONTEXTCANCELAR)
            )
            put(
                "scp2",
                MessageType("Finalizando...", SUCCESSCONTEXTPRINTOK)
            )
            put(
                "scp1",
                MessageType(
                    "Aguarde unos segundos, reintentando impresion",
                    SUCCESSCONTEXTPRINTAGAIN
                )
            )
        }

        const val LOCAL_PREFIX_MARK = "answering-from-device:"

        const val CHKPRINTCONFIG = "CHKPRINTCONFIG"

        const val PROCCESSCONTEXT = "PROCCESSCONTEXT"
        const val PROCCESSCONTEXTCANCELAR = "PROCCESSCONTEXTCANCELAR"

        const val SUCCESSCONTEXT = "SUCCESSCONTEXT"
        const val SUCCESSCONTEXTPRINTAGAIN = "SUCCESSCONTEXTPRINTAGAIN"
        const val SUCCESSCONTEXTPRINTOK = "SUCCESSCONTEXTPRINTOK"
        const val SUCCESSCONTEXTCANCELAR = "SUCCESSCONTEXTCANCELAR"
    }
}