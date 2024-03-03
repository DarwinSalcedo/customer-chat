package com.customer.support.dao

import com.google.gson.annotations.SerializedName

data class OutgoingMessageDao(
    @SerializedName("ConversationId")
    val conversationId: String,
    @SerializedName("Message")
    val message: String,
    private val StoreType: String = "Seleccionar",
    private val UserBizName: String = "",
    private val UserMail: String = "",
    private val UserName: String = "",
    private val IsTest: Boolean = true,
)


