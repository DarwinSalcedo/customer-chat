package com.customer.support.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.adaptor.MainActivityAdapter
import com.customer.support.dao.MessageDao
import com.customer.support.utilis.Constants
import com.customer.support.utilis.Resource
import com.customer.support.viewModel.NetworkViewModel
import java.sql.Timestamp
import java.time.Instant


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: MainActivityAdapter
    private var msgList: MutableMap<String, MutableList<MessageDao>> = mutableMapOf()
    val viewModel = NetworkViewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MainActivityAdapter(msgList)
        recyclerView.adapter = mAdapter
        setObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {

        viewModel.getIncomingMessages()

        viewModel.incomingRes.observe(this) { it ->
            when (it) {
                is Resource.Error -> {
                    Toast.makeText(this, "Error Loading the Messages", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    msgList.clear()
                    Constants.MSGLIST.clear()

                    it.data.let { resourse ->
                        resourse?.forEach {
                            if (msgList.containsKey(it.conversationId)) {
                                msgList[it.conversationId]?.add(it)
                            } else {
                                msgList.set(it.conversationId, mutableListOf(it))
                            }
                        }
                    }

                    msgList.forEach { listEntry ->
                        listEntry.value.sortByDescending { Timestamp.from(Instant.parse(it.timestamp)) }
                    }

                    Constants.MSGLIST = msgList
                    mAdapter.notifyDataSetChanged()

                }
            }
        }
    }
}