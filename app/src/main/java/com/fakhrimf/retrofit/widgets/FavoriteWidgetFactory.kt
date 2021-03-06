package com.fakhrimf.retrofit.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.WIDGET_INTENT_KEY
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import java.net.URL
import java.net.UnknownHostException

class FavoriteWidgetFactory(private val context: Context) : RemoteViewsFactory {
    private var bitmapImage: Bitmap? = null

    override fun onCreate() {}

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun onDataSetChanged() {}

    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews? {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item)
        val widgetItems = cursorToArrayList()
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        val network = NetworkToBitmap(context)
        bitmapImage = network.getBitmap(baseUrl + widgetItems[position].posterPath)
        Log.d("REMOTECHECK", "getViewAt: $bitmapImage, ${widgetItems[position].posterPath}")
        remoteViews.setImageViewBitmap(R.id.widgetImageItem, bitmapImage)
        val fillIntent = Intent()
        fillIntent.putExtra(WIDGET_INTENT_KEY, bitmapImage)
        remoteViews.setOnClickFillInIntent(R.id.widgetImageItem, fillIntent)
        return remoteViews
    }

    override fun getCount(): Int = cursorToArrayList().size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {}

    @SuppressLint("Recycle")
    private fun cursorToArrayList(): ArrayList<FavoriteModel> {
        val helper = FavoritesHelper(context)
        helper.open()
        val cursor = helper.queryCall()
        val favoritesList = ArrayList<FavoriteModel>()
        while (cursor.moveToNext()) {
            val id =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.ID))
            val title =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TITLE))
            val vote =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.VOTE))
            val overview =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.OVERVIEW))
            val release =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.RELEASE))
            val poster =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.POSTER))
            val backdrop =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.BACKDROP))
            val type =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TYPE))
            favoritesList.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true, type))
        }
        return favoritesList
    }

    class NetworkToBitmap(private val context: Context) : AsyncTask<URL, Nothing, Bitmap>() {
        private fun isConnected(context: Context): Boolean {
            var result = false
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            connManager?.run {
                connManager.getNetworkCapabilities(connManager.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
            return result
        }

        override fun doInBackground(vararg params: URL?): Bitmap {
            val bitmap: Bitmap?
            val networkURL = params[0]
            bitmap = if (isConnected(context)) {
                try {
                    BitmapFactory.decodeStream(
                        networkURL?.openConnection()?.getInputStream()
                    )
                } catch (e: UnknownHostException) {
                    BitmapFactory.decodeResource(context.resources, R.drawable.no_internet_dino)
                }
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.no_internet_dino)
            }
            return bitmap
        }

        fun getBitmap(url: String): Bitmap {
            return doInBackground(URL(url))
        }
    }
}