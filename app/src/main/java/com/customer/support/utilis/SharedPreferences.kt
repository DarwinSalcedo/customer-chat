package com.customer.support.utilis

import android.content.Context
import android.util.Log
import com.customer.support.domain.PrinterModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class SharedPreferences {
    companion object {
        private val pref_name: String = "chat_data"

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

        fun savePrintersByList(context: Context, printers: MutableList<PrinterModel>) {
            val value = printers.toMapByModel()
            val data = Gson().toJson(value)
            Log.e("TAG", "savePrinters: ")
            insert("printers", data, context)
            insert("context_printer", printers.first().mPrinterOut, context)
        }

        fun savePrintersByMap(context: Context, printers: Map<String, PrinterModel>) {
            val data = Gson().toJson(printers)
            Log.e("TAG", "savePrinters: " + data)
            Log.e("TAG", "savePrinters: " + printers.toList().first().first)
            insert("printers", data, context)
            insert("context_printer", printers.toList().first().first, context)
        }

        fun getContextPrinter(context: Context): String {
            return get("context_printer", context)
        }

        fun getPrinters(context: Context): Map<String, PrinterModel> {
            val result = get("printers", context)
            if (result != "-1") {
                val mapTypeToken = object : TypeToken<Map<String, PrinterModel>?>() {}.type
                Log.e("TAG", "getPrinters: $result")
                return Gson().fromJson<Map<String, PrinterModel>>(result, mapTypeToken)
            }
            return emptyMap()
        }

        fun resetPrinters(context: Context) {
            Log.e("TAG", "resetPrinters: ")
            insert("printers", "-1", context)
            insert("context_printer", "-1", context)
        }

        fun activeSuccessFlag(context: Context) {
            insert("success_check", "1", context)
        }

        fun resetSuccessFlag(context: Context) {
            insert("success_check", "-1", context)
        }

        fun isActiveSuccessFlag(context: Context): Boolean {
            val result = get("success_check", context)
            return result != "-1"
        }

        fun setEmail(context: Context, value: String) {
            insert("email", value, context)
        }

        fun getEmail(context: Context): String {
            val result = get("email", context)
            if (result != "-1") {
                return result
            }
            return "test@pdssa.com.ar"
        }

        fun setCountry(context: Context, value: String) {
            insert("country", value, context)
        }

        fun getCountry(context: Context): String {
            val result = get("country", context)
            if (result != "-1") {
                return result
            }
            return "AR"
        }
        fun setName(context: Context, value: String) {
            insert("name", value, context)
        }

        fun getName(context: Context): String {
            val result = get("name", context)
            if (result != "-1") {
                return result
            }
            return "No-Name"
        }

        fun activeSettings(context: Context) {
            insert("set_up", "1", context)
        }
        fun resetSettings(context: Context) {
            insert("set_up", "-1", context)
        }

        fun isSettingUp(context: Context): Boolean {
            val result = get("set_up", context)
            return result != "-1"
        }
    }
}