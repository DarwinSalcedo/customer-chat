package com.customer.support.utilis

import com.customer.support.dao.MessageDao


sealed class Constants{
    companion object{
        const val BASE_URL:String = "https://aichat.bistrosoft.com"
        var MSGLIST: MutableMap<String, MutableList<MessageDao>> = mutableMapOf()
    }
}
