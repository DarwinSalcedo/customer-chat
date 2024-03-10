package com.customer.support.network

import com.customer.support.dao.MessageRequest
import com.customer.support.dao.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {


    @POST("api/V1/Conversation")
    suspend fun sendMessage(@Body outgoingmessageRequest: MessageRequest): Response<MessageResponse>
}