package com.example.callforwarder

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.telephony.TelephonyManager

object ForwardHelper {
    fun prefs(c: Context) = c.getSharedPreferences("p", Context.MODE_PRIVATE)

    @SuppressLint("MissingPermission")
    private fun ussd(c: Context, code: String, onDone: () -> Unit) {
        val tm = c.getSystemService(TelephonyManager::class.java)
        try {
            tm.sendUssdRequest(code, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(t: TelephonyManager, r: String, s: CharSequence) = onDone()
                override fun onReceiveUssdResponseFailed(t: TelephonyManager, r: String, f: Int) = onDone()
            }, Handler(Looper.getMainLooper()))
        } catch (e: Exception) { onDone() }
    }

    /** Set forward-on-busy to [number], then reject the ringing call. */
    @SuppressLint("MissingPermission")
    fun forwardAndReject(c: Context, number: String, finished: () -> Unit) {
        var done = false
        val reject = {
            if (!done) {
                done = true
                try { c.getSystemService(TelecomManager::class.java).endCall() } catch (_: Exception) {}
                finished()
            }
        }
        prefs(c).edit().putBoolean("pending", true).apply()
        ussd(c, "**67*$number#", reject)
        Handler(Looper.getMainLooper()).postDelayed(reject, 3000) // fallback if no USSD reply
    }

    fun reset(c: Context, finished: () -> Unit = {}) {
        prefs(c).edit().putBoolean("pending", false).apply()
        ussd(c, "##67#", finished)
    }
}
