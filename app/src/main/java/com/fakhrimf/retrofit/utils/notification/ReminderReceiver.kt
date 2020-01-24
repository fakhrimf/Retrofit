package com.fakhrimf.retrofit.utils.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fakhrimf.retrofit.MainActivity
import com.fakhrimf.retrofit.MovieDetailActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.model.MovieResponse
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    var list: ArrayList<MovieModel>? = null

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.getStringExtra(RECEIVER_INTENT_KEY)) {
            KEY_DAILY -> {
                val notificationManager = createChannel(context)
                val uniqueId = ((Date().time / 1000L) % Integer.MAX_VALUE).toInt()
                notificationManager.notify(uniqueId, notifyReminder(context))
            }
            KEY_RELEASE -> {
                notifyReleaseDate(context)
            }
        }
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_TIME_CHANGED) {
            activateNotification(context, true)
            activateReleaseNotification(context, true)
        }
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

    private fun createReleaseChannel(context: Context?): NotificationManager {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID_RELEASE, CHANNEL_NAME_RELEASE, NotificationManager.IMPORTANCE_HIGH)
        channel.apply {
            enableLights(true)
            lightColor = context.getColor(R.color.white)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 10, 5, 15)
            description = CHANNEL_DESC_RELEASE
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

    private fun notifyReleaseDate(context: Context) {
        GlobalScope.launch(Dispatchers.Default) {
            getMovie(context)
            delay(5000)
            if (list != null) if (list!!.isNotEmpty()) {
                val intentApp = Intent(context, MovieDetailActivity::class.java)
                intentApp.putExtra(VALUE_KEY, list!![0])
                val pendingIntentApp = PendingIntent.getActivity(context, NOTIFICATION_RELEASE_REQUEST_CODE, intentApp, Intent.FILL_IN_ACTION)
                val notifyBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
                    setContentTitle(context.getString(R.string.new_release))
                    setContentText(context.getString(R.string.has_released, list!!.size))
                    setSmallIcon(R.drawable.ic_star_24)
                    setStyle(NotificationCompat.InboxStyle().run {
                        if (list!!.size > 5) {
                            for (i in 0 until 5) {
                                addLine("${list!![i].title}")
                            }
                            setSummaryText(context.getString(R.string.and_more, list!!.size - 5))
                        } else for (i in 0 until list!!.size) {
                            addLine("${list!![i].title}")
                        }
                        addLine(context.getString(R.string.check_now))
                    })
                    setContentIntent(pendingIntentApp)
                    setAutoCancel(true)
                }

                val notificationManager = createReleaseChannel(context)
                val uniqueId = ((Date().time / 1000L) % Integer.MAX_VALUE).toInt()
                notificationManager.notify(uniqueId, notifyBuilder.build())
            }
        }
    }

    private fun getMovie(context: Context) {
        val apiKey = API_KEY
        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val call: Call<MovieResponse> = apiInterface.getDiscoverMovie(apiKey, locale, date, date)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                list = response.body()?.results
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("ERR", "onFailure: $t")
            }
        })
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, Intent(context, ReminderReceiver::class.java).putExtra(RECEIVER_INTENT_KEY, KEY_DAILY), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getReleasePendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(context, NOTIFICATION_RELEASE_REQUEST_CODE, Intent(context, ReminderReceiver::class.java).putExtra(RECEIVER_INTENT_KEY, KEY_RELEASE), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun activateReleaseNotification(context: Context, activate: Boolean) {
        val intentApp = Intent(context, MainActivity::class.java)
        intentApp.putExtra(RECEIVER_INTENT_KEY, KEY_RELEASE)
        val pendingIntentApp = getReleasePendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.run {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, HOUR_EIGHT.toInt())
            set(Calendar.MINUTE, MINUTE_ZERO.toInt())
        }
        if (activate) {
            alarmManager.apply {
                setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntentApp)
                setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentApp)
            }
            createReleaseChannel(context)
//            notifyReleaseDate(context)
        } else {
            alarmManager.cancel(pendingIntentApp)
        }
    }

    fun activateNotification(context: Context, activate: Boolean) {
        val intentApp = Intent(context, MainActivity::class.java)
        intentApp.putExtra(RECEIVER_INTENT_KEY, KEY_DAILY)
        val pendingIntentApp = getPendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.run {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, HOUR_NINE.toInt())
            set(Calendar.MINUTE, MINUTE_ZERO.toInt())
        }
        if (activate) {
            alarmManager.apply {
                setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntentApp)
                setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentApp)
            }
            createChannel(context)
        } else {
            alarmManager.cancel(pendingIntentApp)
        }
    }

    fun isNotificationActivated(type: String, context: Context?): Boolean {
        val intentApp = Intent(context, ReminderReceiver::class.java)
        return when (type) {
            KEY_DAILY -> (PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, intentApp, PendingIntent.FLAG_NO_CREATE)) != null
            else -> (PendingIntent.getBroadcast(context, NOTIFICATION_RELEASE_REQUEST_CODE, intentApp, PendingIntent.FLAG_NO_CREATE)) != null
        }
    }
}