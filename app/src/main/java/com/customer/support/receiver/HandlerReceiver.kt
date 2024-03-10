package com.customer.support.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.customer.support.activity.MainActivity
import com.customer.support.domain.PrinterConfiguration
import com.customer.support.domain.PrinterModel
import com.customer.support.domain.PrinterResponse
import com.customer.support.service.UIService
import com.customer.support.utilis.SharedPreferences
import com.customer.support.utilis.toBeauty
import com.google.gson.Gson

class HandlerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.getStringExtra("data") ?: ""

        when (intent.getStringExtra("type") ?: "") {

            "CHKPRINTCONFIG-RSP" -> {
                val gson = Gson()
                val printerConfiguration = gson.fromJson(data, PrinterConfiguration::class.java)
                Log.e("TAG", "onReceive: " + printerConfiguration.printers.size)

                val bubble = UIService.instance.chatHeads.activeChatHead
                var message = "Detectamos la configuracion siguiente:\n"
                val newPrinters = mutableListOf<PrinterModel>()
                printerConfiguration.printers.forEach { printer ->
                    message += printer.toPrintFormat()
                    if (printer.mPrinterModel != "NINGUNA") newPrinters.add(printer)

                }
                message += "\nVamos a proceder a revisarlas"

                if (printerConfiguration.printers.any { it.mPrinterModel != "NINGUNA" }) {
                    bubble?.handlerUIChat?.addAgentMessage(message)
                    /**
                     * Start printer check
                     *
                     * store printers Done
                     * <key printer1, value printermodel> Done
                     *
                     * active flag printer interaction [cancel or finish this parameter will be empty]
                     * <key printer1, value Boolean>
                     *
                     */

                    SharedPreferences.savePrintersByList(context, newPrinters)

                    Intent("com.pds.bistrov2.ChatApiReceiver").apply {
                        setPackage("com.pds.bistrov2")
                        putExtra("type", "PRINTTEST")
                        putExtra("model", newPrinters.first().mPrinterModel)
                        putExtra("out", newPrinters.first().mPrinterOut)
                    }.also {
                        context.sendBroadcast(it)
                    }

                    bubble?.handlerUIChat?.addAgentMessage("Recuerda tener tu impresora  [" + newPrinters.first().mPrinterOut.toBeauty() + "] conectada, vamos a proceder a revisarla.\nAguarde unos segundos .... ")


                } else {
                    bubble?.handlerUIChat?.addAgentMessage("No encontramos impresoras configuradas, puedes hacerlo desde 1) Configuracion Tecnica -> 2) Perifericos")
                }
            }

            "PRINTTEST-RSP" -> {
                val gson = Gson()
                val printerResponse = gson.fromJson(data, PrinterResponse::class.java)

                Log.e("TAG", "onReceive: " + printerResponse)

                val bubble = UIService.instance.chatHeads.activeChatHead

                if (printerResponse.isConnected) {

                    bubble?.handlerUIChat?.addAgentMessage("Se logro conxión exitosa con la impresora [" + printerResponse.printerOut.toBeauty() + "]")

                    bubble?.handlerUIChat?.addAgentMessage(
                        "Revise por favor que la impresora " + SharedPreferences.get(
                            "context_printer", context
                        )
                            .toBeauty() + "\n" + "Revise por favor que la impresion fue exitosa.." +
                                "\nCuando haya verificado presione:\n" +
                                " 1 Si imprimio bien\n" + " 0 para cancelar"
                    )

                    SharedPreferences.activeSuccessFlag(context)

                } else {
                    bubble?.handlerUIChat?.addAgentMessage(
                        "Código de error: " + printerResponse.responseCode + "\n" + printerResponse.responseMessage
                    )

                    bubble?.handlerUIChat?.addAgentMessage(
                        "Revise por favor que la impresora " + SharedPreferences.get(
                            "context_printer", context
                        )
                            .toBeauty() + " este correctamente enchufada!\n"
                                + "\nLuego apaguela y prendale nuevamente.\n"
                                + "\nCuando haya verificado el estado de la impresora presione:\n"
                                + " 1 para reintentar\n" + " 0 para cancelar"
                    )

                }


            }

            "TRYAGAING-LOCAL" -> {

                Log.e("TAG", "onReceive: TRYAGAING")

                val printers = SharedPreferences.getPrinters(
                    context
                )
                val contextPrinter = SharedPreferences.getContextPrinter(
                    context
                )

                if (printers.isNotEmpty() && contextPrinter != "-1") {
                    val currentInteraction = printers[contextPrinter]



                    Intent("com.pds.bistrov2.ChatApiReceiver").apply {
                        setPackage("com.pds.bistrov2")
                        putExtra("type", "PRINTTEST")
                        putExtra("model", currentInteraction?.mPrinterModel)
                        putExtra(
                            "out", currentInteraction?.mPrinterOut
                        )
                    }.also {
                        context.sendBroadcast(it)
                    }

                    val bubble = UIService.instance.chatHeads.activeChatHead

                    bubble?.handlerUIChat?.addAgentMessage("Recuerda tener tu impresora  [" + currentInteraction?.mPrinterOut?.toBeauty() + "] conectada, vamos a proceder a revisarla.\nAguarde unos segundos .... ")

                }

            }

            "SUCCESSCONTEXT-LOCAL" -> {

                Log.e("TAG", "onReceive: SUCCESSCONTEXT")
                //reset success flag
                SharedPreferences.resetSuccessFlag(context)
                //change print context
                val printers = SharedPreferences.getPrinters(
                    context
                )
                val contextPrinter = SharedPreferences.getContextPrinter(
                    context
                )
                val bubble = UIService.instance.chatHeads.activeChatHead

                if (printers.size == 1) {
                    SharedPreferences.resetPrinters(context)
                    bubble?.handlerUIChat?.addAgentMessage("Listo, revision finalizada.\nSi su problema continua no dude en contactarnos")

                } else { //there are N <1,{P1}>,<2,{P2}>,<N,{PN}> ?
                    //remove context
                    val newPrinters = printers.filterKeys { it != contextPrinter }
                    Log.e("TAG", "onReceive: " + newPrinters)
                    SharedPreferences.resetPrinters(context)
                    // renew context
                    SharedPreferences.savePrintersByMap(context, newPrinters)
                    val firstElement = newPrinters.toList().first()

                    Intent("com.pds.bistrov2.ChatApiReceiver").apply {
                        setPackage("com.pds.bistrov2")
                        putExtra("type", "PRINTTEST")
                        putExtra("model", firstElement.second.mPrinterModel)
                        putExtra("out", firstElement.first)
                    }.also {
                        context.sendBroadcast(it)
                    }


                    bubble?.handlerUIChat?.addAgentMessage("Listo, vamos a probar la otra impresora [" + firstElement.second.mPrinterOut.toBeauty() + "]")
                    bubble?.handlerUIChat?.addAgentMessage("Recuerda tener tu impresora  [" + firstElement.second.mPrinterOut.toBeauty() + "] conectada, vamos a proceder a revisarla.\nAguarde unos segundos .... ")
                }

            }

            "CANCELAR-LOCAL" -> {

                Log.e("TAG", "onReceive: CANCELAR")
                SharedPreferences.resetPrinters(context)
                SharedPreferences.resetSuccessFlag(context)


                UIService.instance.chatHeads.activeChatHead.let {
                    it?.handlerUIChat?.clearMessages()

                    val id = SharedPreferences.renewConversationId(context)
                    val chatHead = UIService.instance.chatHeads.activeChatHead!!

                    chatHead.handlerUIChat.updateChannel(id)

                }
            }

            "STARTSERVICE" -> {
                Log.e("TAG", "onReceive: STARTSERVICE")


                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_SINGLE_TOP
                }.also {
                    context.startActivity(it)
                }


            }

        }

    }

}

