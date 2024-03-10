package com.customer.support.dao

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("ConversationId")
    val conversationId: String,
    @SerializedName("Message")
    val message: String,
    val UserMail: String,
    val InstanceCode: String = "AR",
    val UserName: String = "Test",
    private val StoreType: String = "Seleccionar",
    private val UserBizName: String = "",
    private val UserPhone: String = "",
    private val IsTest: Boolean = true
)


