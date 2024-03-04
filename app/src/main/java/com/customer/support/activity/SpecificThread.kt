package com.customer.support.activity

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.adaptor.SpecificThreadAdapter
import com.customer.support.dao.ChatDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.utilis.Constants
import com.customer.support.utilis.Resource
import com.customer.support.utilis.SharedPreferences
import com.customer.support.viewModel.NetworkViewModel


class SpecificThread : AppCompatActivity() {

    private lateinit var _recyclerView: RecyclerView
    private lateinit var _inputMessage: EditText
    private lateinit var _sendButton: Button
    private lateinit var _mAdapter: SpecificThreadAdapter

    private val _dataList: MutableList<ChatDao> = mutableListOf<ChatDao>()
    private val viewmodel = NetworkViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.specific_messages_layout)

        _recyclerView = findViewById(R.id.specificRecyclerView)
        _inputMessage = findViewById(R.id.InputMessageText)
        _sendButton = findViewById(R.id.buttonSend)

        val data = Constants.MSGLIST

        val threadID = SharedPreferences.retrieveConversationId(this)

        print(data[threadID])

        data[threadID]?.forEach {
            //val userTimestamp = Timestamp.from(Instant.parse(it.timestamp))
            _dataList.add(ChatDao(it.conversationId, it.id, it.sender, it.message, it.timestamp))
        }

        _recyclerView.layoutManager = LinearLayoutManager(this)
        _mAdapter = SpecificThreadAdapter(_dataList,this)
        _recyclerView.adapter = _mAdapter


        _sendButton.setOnClickListener {
            val message = _inputMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewmodel.sendMessages(OutgoingMessageDao(threadID, message))
            }
            _inputMessage.text.clear()
        }
        setObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        viewmodel.sendData.observe(this) { resource ->
            when (resource) {
                is Resource.Error -> Toast.makeText(
                    this,
                    "Error Sending the Messages",
                    Toast.LENGTH_SHORT
                ).show()

                is Resource.Loading -> Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                is Resource.Success -> {
                    resource.data?.let { it ->
                        // val agenttimestamp: Date? = Timestamp.from(Instant.parse(it.timestamp))
                        _dataList.add(
                            ChatDao(
                                it.conversationId,
                                it.id,
                                it.sender,
                                it.message,
                                it.timestamp
                            )
                        )
                        _dataList.sortBy { it.time }
                        _mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}