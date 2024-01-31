package com.customer.support.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.network.Repository
import com.customer.support.utilis.Resource
import com.customer.support.utilis.toFormatDate
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {
    private val repo = Repository()

    val incomingRes: MutableLiveData<Resource<List<MessageDao>>> = MutableLiveData()
    val sendData: MutableLiveData<Resource<MessageDao>> = MutableLiveData()


    fun getIncomingMessages() {
        incomingRes.value = Resource.Loading()
        viewModelScope.launch {
            incomingRes.postValue(Resource.Success(getConversations()))
        }
    }

    fun sendMessages(outgoingMessageDao: OutgoingMessageDao) {
        sendData.value = Resource.Loading()
        viewModelScope.launch {
            publishMessage(outgoingMessageDao)

            val result = repo.sendMessages(outgoingMessageDao)
            result?.let {
                sendData.postValue(Resource.Success(result))
            }
        }
    }

    private fun publishMessage(outgoingMessageDao: OutgoingMessageDao) {
        sendData.postValue(
            Resource.Success(
                MessageDao(
                    Math.random().toString(),
                    outgoingMessageDao.conversationId,
                    true,
                    outgoingMessageDao.message,
                    System.currentTimeMillis().toFormatDate()
                )
            )
        )

    }

    fun getConversations(): List<MessageDao> {
        return listOf(MessageDao(System.currentTimeMillis().toString(),  System.currentTimeMillis().toString(), true, "this is a message 1",   System.currentTimeMillis().toFormatDate()))
    }
}