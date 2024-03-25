package com.customer.support.service.chathead

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.activity.MainActivity
import com.customer.support.adapter.MessagesAdapter
import com.customer.support.adapter.QuickButtonsAdapter
import com.customer.support.adapter.QuickButtonsAdapter.ItemCallback
import com.customer.support.dao.MessageType
import com.customer.support.domain.QuickButtonQuestion
import com.customer.support.network.LocalData
import com.customer.support.service.UIService
import com.customer.support.utilis.SharedPreferences
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem


class Content(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()


    var messagesView: RecyclerView
    var messagesAdapter = MessagesAdapter(mutableListOf(), context)

    var quickResponsesView: RecyclerView
    var quickButtonsAdapter: QuickButtonsAdapter
    var layoutManager = LinearLayoutManager(context)


    var menuBtn: LinearLayout
    var mainCointainer: LinearLayout
    var mainCointainerA: LinearLayout


    init {
        inflate(context, R.layout.chat_head_content, this)

        messagesView = findViewById(R.id.messages)
        menuBtn = findViewById(R.id.menu_btn)
        mainCointainer = findViewById(R.id.mainContainerB)
        mainCointainerA = findViewById(R.id.mainContainerA)

        quickResponsesView = findViewById(R.id.quickResponses)
        quickResponsesView.layoutManager = LinearLayoutManager(context)

        quickButtonsAdapter = QuickButtonsAdapter(
            context = context,
            itemCallback = ItemCallback {
                sendMessagesByOption(
                    it
                )
            }
        )

        quickResponsesView.adapter = quickButtonsAdapter

        layoutManager.stackFromEnd = true

        messagesView.layoutManager = layoutManager

        messagesView.adapter = messagesAdapter

        messagesView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            UIService.instance.chatHeads.activeChatHead?.let {
                it.handlerUIChat.notifications = 0
                it.notifications = 0
            }
        }

        val editText: EditText = findViewById(R.id.editText)
        val sendBtn: LinearLayout = findViewById(R.id.chat_send)
        val newChat: LinearLayout = findViewById(R.id.new_chat)


        newChat.setOnClickListener {
            resetChat()
        }

        sendBtn.setOnClickListener {
            val bubble = UIService.instance.chatHeads.activeChatHead
            sendBtn.postOnAnimationDelayed({
                if (!editText.text.isNullOrEmpty()) {
                    //It should check if there is a context printer running

                    if (SharedPreferences.isActiveSuccessFlag(context) || (SharedPreferences.getContextPrinter(
                            context
                        ) != "-1")
                    )
                        Toast.makeText(
                            context,
                            "Usa las opciones de los botones",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    else {
                        bubble?.handlerUIChat?.sendMessage(editText.text.toString().trim())
                    }

                    editText.text.clear()

                }
            }, 500)

        }

        menuBtn.setOnClickListener {
            SharedPreferences.resetSettings(context)

            UIService.instance.chatHeads.collapse()


            this.postDelayed({

                UIService.instance.chatHeads.onClose()

                Intent(context, UIService::class.java).also {
                    context.stopService(it)
                }

                Intent(context, MainActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_NEW_TASK + FLAG_ACTIVITY_SINGLE_TOP
                }.also {
                    context.startActivity(it)
                }
            }, 500)


        }

        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE

        scaleSpring.currentValue = 0.0

    }

    fun resetChat() {
        Log.e("TAG", "resetChat: ")
        quickButtonsAdapter.reset()
        SharedPreferences.resetPrinters(context)
        SharedPreferences.resetSuccessFlag(context)
        UIService.instance.chatHeads.activeChatHead.let {
            it?.handlerUIChat?.clearMessages()

            val id = SharedPreferences.renewConversationId(context)
            val chatHead = UIService.instance.chatHeads.activeChatHead!!

            chatHead.handlerUIChat.updateChannel(id)

        }
    }

    private fun sendMessagesByOption(quickButtonQuestion: QuickButtonQuestion) {
        val bubble = UIService.instance.chatHeads.activeChatHead
        this.postOnAnimationDelayed({
            bubble?.handlerUIChat?.addMessage(
                MessageType(
                    value = quickButtonQuestion.question,
                    mark = LocalData.LOCAL_PREFIX_MARK,
                    type = quickButtonQuestion.typeMessage,
                    key = quickButtonQuestion.key
                )
            )
        }, 50)
    }


    fun hideContent() {
        scaleSpring.endValue = 0.0

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun showContent() {
        scaleSpring.endValue = 1.0

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun clearMessages() {
        messagesAdapter = MessagesAdapter(mutableListOf(), context)
        messagesView.adapter = messagesAdapter
    }
}