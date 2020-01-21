package com.fakhrimf.retrofit.utils.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fakhrimf.retrofit.MainActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.utils.*
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = createChannel(context)
        val uniqueId = ((Date().time / 1000L) % Integer.MAX_VALUE).toInt()
        notificationManager.notify(uniqueId, context?.let { notifyReminder(it) })
    }

    private fun createChannel(context: Context?): NotificationManager {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.apply {
            enableLights(true)
            lightColor = context.getColor(R.color.white)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 10, 5, 15)
            description = CHANNEL_DESC
        }
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }

    private fun notifyReminder(context: Context): Notification {
        val intentApp = Intent(context, MainActivity::class.java)
        val pendingIntentApp = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intentApp, Intent.FILL_IN_ACTION)
        val notifyBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.good_morning))
            setContentText(context.getString(R.string.notification_text))
            setSmallIcon(R.drawable.ic_popular)
            setContentIntent(pendingIntentApp)
            setAutoCancel(true)
        }
        return notifyBuilder.build()
    }

    private fun getPendingIntent(context: Context, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun activateNotification(context: Context, activate: Boolean) {
        val intentApp = Intent(context, MainActivity::class.java)
        val pendingIntentApp = getPendingIntent(context, intentApp)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.run {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, HOUR_SEVEN.toInt())
        }
        if (activate) {
            alarmManager.apply {
                setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentApp)
            }
            val isAlarmManagerUp = (PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, intentApp, PendingIntent.FLAG_NO_CREATE)) != null
            Log.i("TIMEINFO", "activateNotification: ${calendar.get(Calendar.HOUR_OF_DAY)}, $isAlarmManagerUp")
            createChannel(context)
        } else {
            alarmManager.cancel(pendingIntentApp)
        }
    }
}