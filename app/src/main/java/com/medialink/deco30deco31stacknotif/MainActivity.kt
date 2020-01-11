package com.medialink.deco30deco31stacknotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "Dicoding Channel"
        private const val GROUP_KEY_EMAILS = "group_key_emails"
        private const val NOTIFICATION_REQUEST_CODE = 200
        private const val MAX_NOTIFICATION = 2
    }

    private var mIdNotif = 0
    private val mStackNotif = ArrayList<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val sender = edt_sender.text.toString()
        val message = edt_message.text.toString()
        if (sender.isEmpty() || message.isEmpty()) {
            val methodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            methodManager.hideSoftInputFromWindow(edt_message.windowToken, 0)

            val rootView = window.decorView.rootView
            Snackbar.make(rootView, "Data harus diisi", Snackbar.LENGTH_SHORT).show()
            return
        }

        edt_sender.requestFocus()

        mStackNotif.add(NotificationItem(mIdNotif, sender, message))
        sendNotif()
        mIdNotif++
        edt_sender.text?.clear()
        edt_message.text?.clear()


    }

    private fun sendNotif() {
        Log.d("debug", "send notif here")

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.pikachu_smile)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder

        //Melakukan pengecekan jika idNotification lebih kecil dari Max Notif
        if (mIdNotif < MAX_NOTIFICATION) {
            builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New email from ${mStackNotif[mIdNotif].sender}")
                .setContentText(mStackNotif[mIdNotif].message)
                .setSmallIcon(R.drawable.ic_email_black_24dp)
                .setLargeIcon(largeIcon)
                .setGroup(GROUP_KEY_EMAILS)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        } else {
            val inboxStyle = NotificationCompat.InboxStyle()
                .addLine("New email from ${mStackNotif[mIdNotif].sender}")
                .addLine("New email from ${mStackNotif[mIdNotif - 1].sender}")
                .setBigContentTitle("$mIdNotif new email")
                .setSummaryText("summary@mail.com")

            builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("$mIdNotif new emails")
                .setContentText("content@mail.com")
                .setSmallIcon(R.drawable.ic_email_black_24dp)
                .setGroup(GROUP_KEY_EMAILS)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
        }

        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
        Materi ini akan dibahas lebih lanjut di modul extended
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            notifManager.createNotificationChannel(channel)

            builder.setChannelId(CHANNEL_ID)
        }

        val notification = builder.build()
        notifManager.notify(mIdNotif, notification)
    }
}
