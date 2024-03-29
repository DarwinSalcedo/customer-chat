package com.customer.support.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.customer.support.R
import com.customer.support.activity.MainActivity
import com.customer.support.dao.Message
import com.customer.support.dao.MessageRequest
import com.customer.support.dao.MessageType
import com.customer.support.network.LocalData.Companion.CHKPRINTCONFIG
import com.customer.support.network.LocalData.Companion.PROCCESSCONTEXT
import com.customer.support.network.LocalData.Companion.PROCCESSCONTEXTCANCELAR
import com.customer.support.network.LocalData.Companion.SUCCESSCONTEXTCANCELAR
import com.customer.support.network.LocalData.Companion.SUCCESSCONTEXTPRINTAGAIN
import com.customer.support.network.LocalData.Companion.SUCCESSCONTEXTPRINTOK
import com.customer.support.network.Repository
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


    override fun onCreate() {
        super.onCreate()

        instance = this
        initialized = true

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        chatHeads = ChatHeads(this)

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
        conversationId: String,
        message: MessageType,
        completion: (Message?) -> Unit
    ) {
        instance.serviceScope.launch {
            val email = SharedPreferences.getEmail(context = this@UIService)
            val country = SharedPreferences.getCountry(context = this@UIService)
            val username = SharedPreferences.getName(context = this@UIService)
            val request = MessageRequest(conversationId, message.value, email, country, username)

            val result =
                instance.repository.sendMessages(messageRequest = request, messageType = message)

            //update notification quantity
            if (instance.chatHeads.activeChatHead == null) {
                instance.chatHeads.topChatHead?.let {
                    it.handlerUIChat.notifications = 1
                    it.updateNotifications()
                }
            }
            result?.let { completion(result) }

            BroadcastUtil.emit(this@UIService, result)

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
