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
import com.layer8studios.silomonitoring.models.SiloHistoryEntry
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.Utils.toDate
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil


class ReminderReceiver
    : BroadcastReceiver() {

    companion object {
        var isInitialized = false

        fun initialize(context: Context) {
            if(!Preferences.isInitialized())
                Preferences.init(context)

            val silos = Preferences.getSilos()
            silos.forEach { silo ->
                val contentLeft = Utils.getContentLeft(silo)
                val daysLeft = ceil(contentLeft / silo.needPerDay).toLong()
                val refillDate = LocalDate.now().plusDays(daysLeft)
                // TODO(INCLUDE DAYS BEFORE)
                startReminder(context, refillDate.toDate())
            }
            isInitialized = true
        }

        private fun startReminder(context: Context, date: Date) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, (0..Int.MAX_VALUE).random(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val now = Calendar.getInstance()
            val future = now.clone() as Calendar
            future.set(Calendar.MONTH, date.month)
            future.set(Calendar.YEAR, date.year)
            future.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            future.set(Calendar.HOUR_OF_DAY, 0)
            future.set(Calendar.MINUTE, 0)
            future.set(Calendar.SECOND, 0)

            if(future.after(now))
                future.add(Calendar.SECOND, 15)

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, future.timeInMillis, pendingIntent)
        }
    }


    private val messageGroup = "com.layer8studios.silomonitoring.GROUP_MESSAGES"
    private var isInitialized = false
    private var channelID = ""


    override fun onReceive(context: Context, intent: Intent) {
        if(!Preferences.isInitialized())
            Preferences.init(context)

        val silo = intent.getParcelableExtra<Silo>(ARG_SILO)
        val newSilo = (silo?.copy() as Silo).apply {
            val newHistoryEntry = SiloHistoryEntry(LocalDate.now().toDate(), silo.needPerDay, false)
            emptyingHistory.add(newHistoryEntry)
        }
        Preferences.replaceSilo(silo, newSilo)

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