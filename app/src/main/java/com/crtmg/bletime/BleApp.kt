package com.crtmg.bletime

import android.app.Application
import android.content.Intent

class BleApp : Application() {
    companion object {
        val CHANNEL_ID = "my_channel_01"
        val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        startService(Intent(this, MyService::class.java))

    }

}