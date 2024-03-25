package com.customer.support.dao

data class Message(
    val id: String,
    val conversationId: String,
    val sender: Boolean = false,
    var message: MessageType,
    val timestamp: String
)

class  MessageType(
    var value: String,
    var type: String = "DEFAULT",
    var mark: String = "DEFAULT",
    var key: String = "",
){
    override fun toString(): String {
        return " value :: $value "+
                " type :: $type "+
                " mark :: $mark "
                " key :: $key "

    }

}