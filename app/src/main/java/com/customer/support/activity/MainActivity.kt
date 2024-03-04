package com.customer.support.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.customer.support.R
import com.customer.support.service.UIService
import com.customer.support.utilis.Constants.Companion.REQUEST_CODE


class MainActivity : AppCompatActivity() {
    private lateinit var start: Button
    private lateinit var country: EditText
    private lateinit var name: EditText
    private lateinit var email: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(intent, REQUEST_CODE)
        }


        start = findViewById(R.id.startService)
        email = findViewById(R.id.mail)
        country = findViewById(R.id.country)
        name = findViewById(R.id.name)
        start.setOnClickListener {
            startService()
        }
    }


    fun startService() {
        if (!UIService.initialized && Settings.canDrawOverlays(this)) {
            val service = Intent(this, UIService::class.java)
            service.putExtra("name", name.text.toString().trim())
            service.putExtra("email", email.text.toString().trim())
            service.putExtra("country", country.text.toString().trim())
            startService(service)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && Settings.canDrawOverlays(this)) {
            startService()
        }
    }
}