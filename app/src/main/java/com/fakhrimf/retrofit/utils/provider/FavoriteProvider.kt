package com.fakhrimf.retrofit.utils.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.fakhrimf.retrofit.utils.AUTHORITY
import com.fakhrimf.retrofit.utils.FAVORITE_PROVIDER
import com.fakhrimf.retrofit.utils.FAVORITE_PROVIDER_ID
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper

class FavoriteProvider : ContentProvider() {

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return when(FAVORITE_PROVIDER_ID){
            mUriMatcher.match(uri) -> favoritesHelper.delete(uri.lastPathSegment.toString())
            else -> 0
        }
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val insert:Long = when (FAVORITE_PROVIDER) {
            mUriMatcher.match(uri) -> favoritesHelper.insert(values)
            else -> 0
        }
        return Uri.parse("$CONTENT_URI/$insert")
    }

    override fun onCreate(): Boolean {
        favoritesHelper = FavoritesHelper.getInstance(context!!)
        favoritesHelper.open()
        Log.d("PARO", "onCreate: favoriteProvider created?")
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        Log.d("PARO", "query: called")
        return favoritesHelper.queryCall()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int = 0

    companion object {
        private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favoritesHelper: FavoritesHelper
        init {
            mUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE_PROVIDER)
            mUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAVORITE_PROVIDER_ID)
        }
    }
}
