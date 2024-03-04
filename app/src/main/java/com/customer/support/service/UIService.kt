package com.customer.support.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.customer.support.R
import com.customer.support.activity.MainActivity
import com.customer.support.dao.MessageDao
import com.customer.support.dao.OutgoingMessageDao
import com.customer.support.network.Repository
import com.customer.support.network.Repository.Companion.LOCAL_PREFIX_MARK
import com.customer.support.service.chathead.ChatHeads
import com.customer.support.service.chathead.HandlerUIChat
import com.customer.support.utilis.SharedPreferences
import com.customer.support.utilis.runOnMainLoop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UIService : Service() {
    companion object {
        lateinit var instance: UIService
        var initialized = false
    }

    lateinit var windowManager: WindowManager

    lateinit var chatHeads: ChatHeads

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val repository = Repository()

    private lateinit var innerReceiver: InnerReceiver

    override fun onCreate() {
        super.onCreate()

        instance = this
        initialized = true

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        chatHeads = ChatHeads(this)

        innerReceiver = InnerReceiver()

        registerReceiver(innerReceiver, IntentFilter("com.customer.chat.BROADCAST"))

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("overlay_service", "Asistente")
            } else {
                ""
            }

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setContentTitle("Atencion al cliente")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent).build()

        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        initialized = false
        unregisterReceiver(innerReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Thread {
            startUIView()
        }.start()

        return START_STICKY
    }


    fun onProcessMessage(
        outgoingMessageDao: OutgoingMessageDao,
        completation: (MessageDao?) -> Unit
    ) {
        instance.serviceScope.launch {
            val result = instance.repository.sendMessages(outgoingMessageDao = outgoingMessageDao)


            if (instance.chatHeads.activeChatHead == null) {
                instance.chatHeads.topChatHead?.let {
                    it.handlerUIChat.notifications = 1
                    it.updateNotifications()
                }
            }
            result?.let { completation(result) }

            checkToSendBroadcast(result?.message ?: "")

        }

    }

    private fun checkToSendBroadcast(outgoingMessage: String) {
        val decodeData = outgoingMessage.split("|")
        if (decodeData.size > 1) {
            val intent = Intent("com.customer.chat.BROADCAST")
            when (decodeData.last()) {
                "CHKPRINTCONFIG" -> {
                    intent.putExtra("CHKPRINTCONFIG", "TEST 1")
                }

                "TESTPRINT1" -> {
                    intent.putExtra("TESTPRINT1", "TEST 2 ")
                }

                "TESTPRINT2" -> {
                    intent.putExtra("TESTPRINT2", "TEST 3")
                }
            }
            sendBroadcast(intent)
        }
    }

    private fun startUIView() {

        runOnMainLoop {

            instance.chatHeads.add(
                HandlerUIChat(
                    conversationId = SharedPreferences.retrieveConversationId(this),
                )
            )
        }
    }
}


internal class InnerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
            val reason = intent.getStringExtra("reason")
            if (reason != null) {
                UIService.instance.chatHeads.collapse()
            }
        }

        val bubble = UIService.instance.chatHeads.activeChatHead
        bubble?.handlerUIChat?.checkMessage(LOCAL_PREFIX_MARK + "noconfiguracion")
    }
}