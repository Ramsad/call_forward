package com.example.callforwarder

import android.Manifest
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        requestPermissions(arrayOf(
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE, Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.POST_NOTIFICATIONS), 1)

        val p = ForwardHelper.prefs(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 48, 48, 48) }
        val fields = (1..3).map { k ->
            EditText(this).apply {
                hint = "Number $k (with country code)"
                inputType = android.text.InputType.TYPE_CLASS_PHONE
                setText(p.getString("n$k", ""))
                root.addView(this)
            }
        }
        root.addView(Button(this).apply {
            text = "Save numbers"
            setOnClickListener {
                p.edit().apply { fields.forEachIndexed { i, f -> putString("n${i + 1}", f.text.toString().trim()) } }.apply()
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            }
        })
        root.addView(Button(this).apply {
            text = "Turn off forwarding now"
            setOnClickListener { ForwardHelper.reset(context) }
        })
        setContentView(root)
    }
}
