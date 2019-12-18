package com.fakhrimf.retrofit.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.fakhrimf.retrofit.FavoriteDetailActivity
import com.fakhrimf.retrofit.MainActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.*

class FavoriteStacks : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null) if (intent.action == OPEN_DETAIL_ACTION) {
            val intentDetail = Intent(context, MainActivity::class.java)
            intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intentDetail)
        } else if (intent.action == WIDGET_REFRESH) {
            val updateIntent = Intent(context, FavoriteWidgetService::class.java)
            updateIntent.data = updateIntent.toUri(Intent.URI_INTENT_SCHEME).toUri()
            val views = RemoteViews(context.packageName, R.layout.favorite_stacks)
            views.setRemoteAdapter(R.id.stackViewWidget, updateIntent)
            views.setEmptyView(R.id.stackViewWidget, R.id.tvEmptyView)
        }
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val intent = Intent(context, FavoriteWidgetService::class.java)
    intent.apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
    }

    val views = RemoteViews(context.packageName, R.layout.favorite_stacks)
    views.apply {
        setRemoteAdapter(R.id.stackViewWidget, intent)
        setEmptyView(R.id.stackViewWidget, R.id.tvEmptyView)
    }

    val detailIntent = Intent(context, FavoriteStacks::class.java)
    detailIntent.apply {
        action = OPEN_DETAIL_ACTION
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
    }
    val detailPendingIntent = PendingIntent.getBroadcast(context, PENDING_INTENT_KEY, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setPendingIntentTemplate(R.id.stackViewWidget, detailPendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}