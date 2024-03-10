package com.customer.support.dao

data class Message(
    val id: String,
    val conversationId: String,
    val sender: Boolean = false,
    var message: String,
    val timestamp: String,
)