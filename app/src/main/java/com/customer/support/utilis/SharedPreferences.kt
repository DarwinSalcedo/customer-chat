package com.customer.support.utilis

import android.content.Context
import java.util.UUID

class SharedPreferences {
    companion object {
        val pref_name: String = "chat_data"

        fun insert(key: String, value: String, context: Context) {
            val sharedPreference = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun get(key: String, context: Context): String {
            val sharedPreference = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
            return sharedPreference.getString(key, "-1").toString()
        }

        fun delete(key: String, context: Context) {
            val sharedPreference = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
            sharedPreference.edit().remove(key).apply()
        }


        fun retrieveConversationId(context: Context): String {
            var id = get("conversation_id", context)
            if (id == "-1") {
                id = UUID.randomUUID().toString()
                insert("conversation_id", id, context)
            }
            return id
        }

        fun renewConversationId(context: Context): String {
            val id = UUID.randomUUID().toString()
            insert("conversation_id", id, context)
            return id
        }

    }
}