package com.customer.support.utilis

import com.customer.support.dao.MessageDao


sealed class Constants{
    companion object{
        val REQUEST_CODE = 5469
        const val BASE_URL:String = "https://global-aichat.bistrosoft.com/"
        var MSGLIST: MutableMap<String, MutableList<MessageDao>> = mutableMapOf()
    }
}
