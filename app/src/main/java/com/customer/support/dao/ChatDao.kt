package com.customer.support.dao

data class ChatDao(
    val conversationId: String? = "",
    val _id: String? = "",
    val sender: Boolean,
    val message: String? = "",
    val time: String?
)
