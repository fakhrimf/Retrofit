package com.fakhrimf.retrofit.widgets

import android.content.Intent
import android.widget.RemoteViewsService

class FavoriteWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return FavoriteWidgetFactory(this.applicationContext)
    }
}