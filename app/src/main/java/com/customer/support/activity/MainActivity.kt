package com.customer.support.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.customer.support.R
import com.customer.support.service.UIService
import com.customer.support.utilis.Constants.Companion.REQUEST_CODE


class MainActivity : AppCompatActivity() {
    private lateinit var sttar: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(intent, REQUEST_CODE)
        }

        /*recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MainActivityAdapter(msgList)
        recyclerView.adapter = mAdapter*/

        sttar =  findViewById(R.id.startService)
        sttar.setOnClickListener{
            startService()
        }
    }





    fun startService() {
        if (!UIService.initialized && Settings.canDrawOverlays(this)) {
            val service = Intent(this, UIService::class.java)
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