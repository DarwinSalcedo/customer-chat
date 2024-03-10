package com.customer.support.service.chathead

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.customer.support.R
import com.customer.support.adaptor.SpecificThreadAdapter
import com.customer.support.network.Repository.Companion.LOCAL_PREFIX_MARK
import com.customer.support.network.Repository.Companion.PROCCESSCONTEXT
import com.customer.support.network.Repository.Companion.SUCCESSCONTEXT
import com.customer.support.service.UIService
import com.customer.support.utilis.SharedPreferences
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem


class Content(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()


    var messagesView: RecyclerView
    var messagesAdapter = SpecificThreadAdapter(mutableListOf(), context)
    var layoutManager = LinearLayoutManager(context)


    var menuBtn: LinearLayout
    var mainCointainer: LinearLayout
    var mainCointainerA: LinearLayout

    var option1: FrameLayout
    var option2: FrameLayout
    var option3: FrameLayout
    var option4: FrameLayout


    init {
        inflate(context, R.layout.chat_head_content, this)

        messagesView = findViewById(R.id.messages)
        menuBtn = findViewById(R.id.menu_btn)
        mainCointainer = findViewById(R.id.mainContainerB)
        mainCointainerA = findViewById(R.id.mainContainerA)

        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)


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
            SharedPreferences.resetPrinters(context)
            SharedPreferences.resetSuccessFlag(context)
            UIService.instance.chatHeads.activeChatHead.let {
                it?.handlerUIChat?.clearMessages()

                val id = SharedPreferences.renewConversationId(context)
                val chatHead = UIService.instance.chatHeads.activeChatHead!!

                chatHead.handlerUIChat.updateChannel(id)

            }
        }

        sendBtn.setOnClickListener {
            val bubble = UIService.instance.chatHeads.activeChatHead
            sendBtn.postOnAnimationDelayed({
                if (!editText.text.isNullOrEmpty()) {
                    //It should check if there is a context printer running
                    val message =
                        if (SharedPreferences.isActiveSuccessFlag(context))
                            LOCAL_PREFIX_MARK + " " + editText.text.toString() + " " + SUCCESSCONTEXT
                        else if (SharedPreferences.getContextPrinter(context) != "-1")
                            LOCAL_PREFIX_MARK + " " + editText.text.toString() + " " + PROCCESSCONTEXT
                        else editText.text.toString()
                    bubble?.handlerUIChat?.sendMessage(message)
                    editText.text.clear()

                }
            }, 500)

        }

        menuBtn.setOnClickListener {
            // drawerLayout.openDrawer(Gravity.START)
        }

        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE

        scaleSpring.currentValue = 0.0

        option1.setOnClickListener {
            sendMessagesByOption(
                sendBtn,
                option1,
                LOCAL_PREFIX_MARK + "Tengo un problema con una impresora"
            )
        }
        option2.setOnClickListener {

            sendMessagesByOption(sendBtn, option2, LOCAL_PREFIX_MARK + "Como aplico descuentos?")

        }
        option3.setOnClickListener {
            sendMessagesByOption(
                sendBtn,
                option3,
                LOCAL_PREFIX_MARK + "Como aplico descuentos no youtube?"
            )

        }
        option4.setOnClickListener {
            sendMessagesByOption(
                sendBtn,
                option4,
                LOCAL_PREFIX_MARK + "Necesito cambiar y actualizar los precios"
            )
        }
    }

    private fun sendMessagesByOption(sendBtn: LinearLayout, button: FrameLayout, text: String) {
        button.isEnabled = false
        val bubble = UIService.instance.chatHeads.activeChatHead
        sendBtn.postOnAnimationDelayed({
            bubble?.handlerUIChat?.sendMessage(text)
            button.isEnabled = true
        }, 500)
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
        messagesAdapter = SpecificThreadAdapter(mutableListOf(), context)
        messagesView.adapter = messagesAdapter
    }
}