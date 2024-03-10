package com.customer.support.domain

import com.customer.support.utilis.toBeauty


class PrinterModel {
    var mPrinterPath = ""

    var mPrinterProductId = ""

    var mPrinterModel = ""

    var mPrinterModelValue = ""

    var mPrinterOut = ""

    var mPrinterIp = ""

    var mIsNetPrinterConfig = false

    var mCashdrawer = ""

    var printerIps: HashMap<String, String>? = null

    override fun toString(): String {
        return "PrinterModel{" +
                "mPrinterPath='" + mPrinterPath + '\'' +
                ", mPrinterProductId='" + mPrinterProductId + '\'' +
                ", mPrinterModel='" + mPrinterModel + '\'' +
                ", mPrinterModelValue='" + mPrinterModelValue + '\'' +
                ", mPrinterOut='" + mPrinterOut + '\'' +
                ", mPrinterIp='" + mPrinterIp + '\'' +
                ", mIsNetPrinterConfig=" + mIsNetPrinterConfig +
                ", mCashdrawer='" + mCashdrawer + '\'' +
                ", printerIps='" + printerIps + '\'' +
                '}' + "\n"

    }

    fun toPrintFormat(): String {
        return if (mPrinterModel != "NINGUNA")
            "- Impresora ${mPrinterOut.toBeauty()}[$mPrinterModel]\n" else ""
    }


}


