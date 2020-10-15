package com.crtmg.bletime

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class MyService : Service() {


    private val UPDATE_INTERVAL = 2000L
    private val updateWidgetHandler = Handler()
    private var updateWidgetRunnable: Runnable = Runnable {
        run {
            //Update UI
            CentralManager.getConnected().forEach {
                CentralManager.readTime(it)
                Log.e("Worker Runnable", " >>> ${it.name} ")
            }
            updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        if (true) {
            updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        } else {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            stopSelf()
        }
        return START_STICKY
    }


    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        startForeground(
            BleApp.NOTIFICATION_ID, NotificationCompat.Builder(
                this,
                BleApp.CHANNEL_ID
            ) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}