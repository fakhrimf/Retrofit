package com.fakhrimf.retrofit.utils.source.local

import android.provider.BaseColumns

internal class DatabaseContract {
    internal class FavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorites"
            const val TITLE = "title"
            const val ID = "_id"
            const val OVERVIEW = "overview"
            const val BACKDROP = "backdrop_path"
            const val POSTER = "poster_path"
            const val RELEASE = "release_date"
            const val VOTE = "vote"
        }
    }
}