package com.layer8studios.silomonitoring.receivers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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

            schedule(context, refillDate.toDate(), silo)
            println("Scheduled for ${refillDate.toDate()} (${silo.notificationID})")
        }

        fun cancelNotification(context: Context, silo: Silo) {
            cancel(context, silo)
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

                schedule(context, refillDate.toDate(), silo)
                println("Scheduled for ${refillDate.toDate()} (${silo.notificationID})")
            }
            areNotificationsScheduled = true
        }

        fun cancelNotifications(context: Context) {
            if(!Preferences.isInitialized())
                Preferences.init(context)

            Preferences.getSilos().forEach { silo ->
                cancel(context, silo)
                println("Canceled (${silo.notificationID})")
            }
            areNotificationsScheduled = false
        }


        private fun schedule(context: Context, date: Date, silo: Silo) {
            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(ARG_SILO, silo)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, silo.notificationID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            /* TODO(APPLY AFTER TESTS)
            val future = Calendar.getInstance().apply {
                set(Calendar.MONTH, date.month)
                set(Calendar.YEAR, date.year)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 6)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }*/

            // TODO(REMOVE AFTER TESTS)
            val future = Calendar.getInstance().apply {
                add(Calendar.SECOND, 15)
            }

            if(Build.VERSION.SDK_INT >= 23)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, future.timeInMillis, pendingIntent)
            else if(Build.VERSION.SDK_INT >= 19)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, future.timeInMillis, pendingIntent)
            else alarmManager.set(AlarmManager.RTC_WAKEUP, future.timeInMillis, pendingIntent)
        }

        private fun cancel(context: Context, silo: Silo) {
            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(ARG_SILO, silo)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, silo.notificationID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

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
        sendNotification(context, silo!!)
    }

    private fun createChannels(context: Context) {
        channelID = context.getString(R.string.app_name)
        val channel = NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_HIGH).apply {
            this.description = context.getString(R.string.app_name)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(context: Context, silo: Silo) {
        val contentLeft = Utils.getContentLeft(silo)
        val daysLeft = ceil(contentLeft / silo.needPerDay).toLong()
        val dayString = context.getString(if(daysLeft.toInt() == 1) R.string.day else R.string.days)

        val title = context.getString(R.string.silo_is_soon_empty)
        val text = "${silo.name} ${context.getString(R.string.is_empty_in)} $daysLeft $dayString"

        val intent = Intent(context, MainActivity::class.java).apply {
            // TODO(OPEN THE RIGHT SILO)
        }
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
            setAutoCancel(true)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(silo.notificationID, builder.build())
            notify(0, summary.build())
        }
    }

}