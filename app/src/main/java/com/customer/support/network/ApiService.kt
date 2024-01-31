package com.customer.support.network

import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.dao.RawMessageDao
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface ApiService {

    @GET("api/messages")
    suspend fun getMessages(@HeaderMap headers: Map<String, String>): Response<ArrayList<MessageDao>>


    @POST("api/V1/Conversation")
    suspend fun sendMessage( @Body outgoingmessageDao: OutgoingMessageDao) : Response<RawMessageDao>
}