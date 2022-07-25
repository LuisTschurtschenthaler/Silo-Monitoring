package com.layer8studios.silomonitoring.receivers

import android.app.AlarmManager
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
import com.layer8studios.silomonitoring.models.Date
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.Utils.toDate
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil


class NotificationReceiver
    : BroadcastReceiver() {

    companion object {
        var areNotificationsScheduled = false

        fun scheduleNotification(context: Context, silo: Silo) {
            val contentLeft = Utils.getContentLeft(silo)
            val daysLeft = ceil(contentLeft / silo.needPerDay).toLong()
            val refillDate = LocalDate.now().plusDays(daysLeft)
            // TODO(INCLUDE DAYS BEFORE)

            schedule(context, refillDate.toDate(), silo.notificationID)
            println("Scheduled for ${refillDate.toDate()} (${silo.notificationID})")
        }

        fun cancelNotification(context: Context, silo: Silo) {
            cancel(context, silo.notificationID)
            println("Canceled (${silo.notificationID})")
        }


        fun scheduleNotifications(context: Context) {
            if(areNotificationsScheduled)
                return

            if(!Preferences.isInitialized())
                Preferences.init(context)

            Preferences.getSilos().forEach { silo ->
                val contentLeft = Utils.getContentLeft(silo)
                val daysLeft = ceil(contentLeft / silo.needPerDay).toLong()
                val refillDate = LocalDate.now().plusDays(daysLeft)
                // TODO(INCLUDE DAYS BEFORE)

                schedule(context, refillDate.toDate(), silo.notificationID)
                println("Scheduled for ${refillDate.toDate()} (${silo.notificationID})")
            }
            areNotificationsScheduled = true
        }

        fun cancelNotifications(context: Context) {
            if(!Preferences.isInitialized())
                Preferences.init(context)

            Preferences.getSilos().forEach { silo ->
                cancel(context, silo.notificationID)
                println("Canceled (${silo.notificationID})")
            }
            areNotificationsScheduled = false
        }


        private fun schedule(context: Context, date: Date, notificationID: Int) {
            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val future = Calendar.getInstance().apply {
                set(Calendar.MONTH, date.month)
                set(Calendar.YEAR, date.year)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 6)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, future.timeInMillis, pendingIntent)
        }

        private fun cancel(context: Context, notificationID: Int) {
            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.cancel(pendingIntent)
        }
    }


    private val messageGroup = "com.layer8studios.silomonitoring.GROUP_MESSAGES"
    private var isChannelCreated = false
    private var channelID = ""


    override fun onReceive(context: Context, intent: Intent) {
        if(!Preferences.isInitialized())
            Preferences.init(context)

        if(!isChannelCreated) {
            createChannels(context)
            isChannelCreated = true
        }

        val silo = intent.getParcelableExtra<Silo>(ARG_SILO)
        sendNotification(context, silo!!.notificationID)
    }

    private fun createChannels(context: Context) {
        channelID = context.getString(R.string.app_name)
        val channel = NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_HIGH).apply {
            this.description = context.getString(R.string.app_name)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(context: Context, notificationID: Int) {
        val title = "TITLE"
        val text = "TEXT"

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
            notify(notificationID, builder.build())
            notify(0, summary.build())
        }
    }

}