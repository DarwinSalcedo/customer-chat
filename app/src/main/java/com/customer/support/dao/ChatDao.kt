package com.customer.support.dao
import java.util.Date


data class ChatDao(
    val thread_id: String? = "",
    val _id: String? = "",
    val sender: Boolean ,
    val message: String? = "",
    val time: Date?
)
