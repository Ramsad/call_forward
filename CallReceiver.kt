package com.example.callforwarder

import android.app.*
import android.content.*
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        when (i.getStringExtra(TelephonyManager.EXTRA_STATE)) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val caller = i.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Unknown"
                showChooser(c, caller)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                c.getSystemService(NotificationManager::class.java).cancel(1)
                if (ForwardHelper.prefs(c).getBoolean("pending", false)) {
                    val p = goAsync()
                    // wait so the carrier finishes forwarding the rejected call, then clear
                    Handler(Looper.getMainLooper()).postDelayed({
                        ForwardHelper.reset(c) { p.finish() }
                        Handler(Looper.getMainLooper()).postDelayed({ p.finish() }, 2000)
                    }, 6000)
                }
            }
        }
    }

    private fun showChooser(c: Context, caller: String) {
        val nm = c.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel("calls", "Incoming calls", NotificationManager.IMPORTANCE_HIGH))
        val p = ForwardHelper.prefs(c)
        val b = NotificationCompat.Builder(c, "calls")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Call from $caller")
            .setContentText("Forward to:")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
        for (k in 1..3) {
            val num = p.getString("n$k", "") ?: ""
            if (num.isBlank()) continue
            val pi = PendingIntent.getBroadcast(c, k,
                Intent(c, ActionReceiver::class.java).putExtra("number", num),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            b.addAction(0, "Number $k", pi)
        }
        nm.notify(1, b.build())
    }
}
