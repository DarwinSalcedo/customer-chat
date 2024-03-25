package com.customer.support.domain

data class QuickButtonQuestion(
    val question: String,
    val key: String,
    var typeMessage: String = "DEFAULT"
)