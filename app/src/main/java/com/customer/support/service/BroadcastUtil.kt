package com.customer.support.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.customer.support.dao.Message
import com.customer.support.network.LocalData

class BroadcastUtil {
    companion object {

        /**
         * The symbol "|"   means there is a command to process
         * EX: "lorem ipsum |COMMANDTOPROCCESS"
         */
        fun emit(context: Context, outgoingMessage: Message?) {
            Log.e("SEND:::", "checkToSendBroadcast :::" + outgoingMessage.toString())
            when (outgoingMessage?.message?.type) {
                LocalData.CHKPRINTCONFIG -> {
                    val intent = Intent("com.pds.bistrov2.ChatApiReceiver")
                    intent.putExtra("type", "CHKPRINTCONFIG")
                    Log.e("SEND:::", "checkToSendBroadcast :::" + outgoingMessage)
                    intent.setPackage("com.pds.bistrov2")
                    context.sendBroadcast(intent)
                }

                LocalData.PROCCESSCONTEXTCANCELAR -> {
                    Intent("com.customer.chat.HANDLER").apply {
                        setPackage("com.customer.support")
                        putExtra("type", "CANCELAR-LOCAL")

                    }.also {
                        context.sendBroadcast(it)
                    }
                }

                LocalData.PROCCESSCONTEXT -> {
                    Intent("com.customer.chat.HANDLER").apply {
                        setPackage("com.customer.support")
                        putExtra("type", "TRYAGAING-LOCAL")

                    }.also {
                        context.sendBroadcast(it)
                    }
                }

                LocalData.SUCCESSCONTEXTPRINTAGAIN -> {
                    Intent("com.customer.chat.HANDLER").apply {
                        setPackage("com.customer.support")
                        putExtra("type", "TRYAGAING-LOCAL")

                    }.also {
                        context.sendBroadcast(it)
                    }
                }

                LocalData.SUCCESSCONTEXTPRINTOK -> {
                    Intent("com.customer.chat.HANDLER").apply {
                        setPackage("com.customer.support")
                        putExtra("type", "SUCCESSCONTEXT-LOCAL")

                    }.also {
                        context.sendBroadcast(it)
                    }
                }


                LocalData.SUCCESSCONTEXTCANCELAR -> {
                    Intent("com.customer.chat.HANDLER").apply {
                        setPackage("com.customer.support")
                        putExtra("type", "CANCELAR-LOCAL")

                    }.also {
                        context.sendBroadcast(it)
                    }
                }

                else -> {
                    println("NOTHING TO PROCESS . . .")
                }
            }

        }
    }

}
