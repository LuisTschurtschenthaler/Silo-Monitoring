package com.layer8studios.silomonitoring.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.activities.MainActivity
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


class ReminderReceiver
    : BroadcastReceiver() {

    private val messageGroup = "com.layer8studios.silomonitoring.GROUP_MESSAGES"
    private var isInitialized = false
    private var channelID = ""


    override fun onReceive(context: Context, intent: Intent) {
        val silo = intent.getParcelableExtra<Silo>(ARG_SILO)
        silo!!.contentLeft -= silo!!.needPerDay

        if(!isInitialized) {
            createChannels(context)
            isInitialized = true
        }

        sendNotification(context, "HALLO", "DES ISCH A TESCHT")
        println("Notification send")
    }

    private fun createChannels(context: Context) {
        channelID = context.getString(R.string.app_name)
        val channel = NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_HIGH).apply {
            this.description = context.getString(R.string.app_name)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(context: Context, title: String, text: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, channelID).apply {
            setSmallIcon(R.mipmap.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(text)
            setGroup(messageGroup)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val summary = NotificationCompat.Builder(context, channelID).apply {
            setSmallIcon(R.mipmap.ic_launcher_foreground)
            setContentTitle(title)
            setGroup(messageGroup)
            setGroupSummary(true)
        }

        with(NotificationManagerCompat.from(context)) {
            val notificationId = Calendar.getInstance().timeInMillis.toInt()
            notify(notificationId, builder.build())
            notify(0, summary.build())
        }
    }

}