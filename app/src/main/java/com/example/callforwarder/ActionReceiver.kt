package com.example.callforwarder

import android.app.NotificationManager
import android.content.*

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val number = i.getStringExtra("number") ?: return
        val p = goAsync()
        c.getSystemService(NotificationManager::class.java).cancel(1)
        ForwardHelper.forwardAndReject(c, number) { p.finish() }
    }
}
