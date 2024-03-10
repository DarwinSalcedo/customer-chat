package com.customer.support.domain

data class PrinterResponse(
    var isConnectionTest: Boolean,
    var isConnected: Boolean,
    var responseCode: Int,
    var responseMessage: String,
    var printerModel: String,
    var printerOut: String,
    var pathPrinter: String
)
