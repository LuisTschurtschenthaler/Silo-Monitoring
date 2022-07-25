package com.layer8studios.silomonitoring.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootCompletedReceiver
    : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(Intent.ACTION_BOOT_COMPLETED == intent.action)
            NotificationReceiver.scheduleNotifications(context)
    }

}