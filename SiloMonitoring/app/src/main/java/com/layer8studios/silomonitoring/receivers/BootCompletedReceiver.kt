package com.layer8studios.silomonitoring.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.layer8studios.silomonitoring.models.Date
import java.util.*


class BootCompletedReceiver
    : BroadcastReceiver() {

    companion object {
        var isStarted = false

        fun startReminder(context: Context, date: Date) {
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

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, future.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            isStarted = true
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        if(Intent.ACTION_BOOT_COMPLETED == intent.action)
            startReminder(context, Date(2022, 7, 20))
    }

}