package com.customer.support.dao

import com.google.gson.annotations.SerializedName

data class OutgoingMessageDao(
    @SerializedName("ConversationId")
    val conversationId: String,
    @SerializedName("Message")
    val message: String,
    private val StoreType: String = "Seleccionar",
    private val UserBizName: String = "",
    private val InstanceCode: String = "AR",
    private val UserMail: String = "test@pdssa.com.ar",
    private val UserName: String = "Test",
    private val UserPhone: String = "",
    private val IsTest: Boolean = true
)


