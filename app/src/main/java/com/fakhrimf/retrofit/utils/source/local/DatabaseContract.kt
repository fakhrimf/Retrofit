package com.fakhrimf.retrofit.utils.source.local

import android.net.Uri
import android.provider.BaseColumns
import com.fakhrimf.retrofit.utils.AUTHORITY
import com.fakhrimf.retrofit.utils.CONTENT_SCHEME

internal class DatabaseContract {
    internal class FavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorites"
            const val TITLE = "title"
            const val ID = "_id"
            const val TYPE = "type"
            const val OVERVIEW = "overview"
            const val BACKDROP = "backdrop_path"
            const val POSTER = "poster_path"
            const val RELEASE = "release_date"
            const val VOTE = "vote"
            val CONTENT_URI: Uri = Uri.Builder().apply {
                scheme(CONTENT_SCHEME)
                authority(AUTHORITY)
                appendPath(TABLE_NAME)
            }.build()
        }
    }
}