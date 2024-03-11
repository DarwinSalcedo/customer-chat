package com.customer.support.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.customer.support.R
import com.customer.support.service.UIService
import com.customer.support.utilis.Constants.Companion.REQUEST_CODE
import com.customer.support.utilis.SharedPreferences
import com.facebook.rebound.BuildConfig


class MainActivity : AppCompatActivity() {
    private lateinit var start: Button
    private lateinit var country: EditText
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var version: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            view.onApplyWindowInsets(windowInsets)
        }

        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(intent, REQUEST_CODE)
        }

        if (SharedPreferences.isSettingUp(this)) {
            val service = Intent(this, UIService::class.java)
            startService(service)
            finish()
        }

        setContentView(R.layout.activity_main)

        version = findViewById(R.id.version)
        version.text = "Version :: ${BuildConfig.VERSION_NAME}  ${BuildConfig.VERSION_CODE}"
        start = findViewById(R.id.startService)
        email = findViewById(R.id.mail)
        country = findViewById(R.id.country)
        name = findViewById(R.id.name)

        start.setOnClickListener {
            startService()
        }
    }


    private fun startService() {
        if (!UIService.initialized && Settings.canDrawOverlays(this)) {
            if (!email.text.isNullOrEmpty()) {
                SharedPreferences.setEmail(this, email.text.toString().trim())
            }

            if (!country.text.isNullOrEmpty()) {
                SharedPreferences.setCountry(this, country.text.toString().trim())
            }

            if (!name.text.isNullOrEmpty()) {
                SharedPreferences.setName(this, name.text.toString().trim())
            }

            SharedPreferences.activeSettings(this)

            val service = Intent(this, UIService::class.java)
            service.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startService(service)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && Settings.canDrawOverlays(this)) {
            startService()
        }
    }
}