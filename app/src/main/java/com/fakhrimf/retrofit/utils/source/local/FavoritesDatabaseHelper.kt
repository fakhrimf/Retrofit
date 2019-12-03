package com.fakhrimf.retrofit.utils.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns

internal class FavoritesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VER) {
    companion object {
        private const val DB_NAME = "db_cinemalistfavorites"
        private const val DB_VER = 1
        private const val DROP = "DROP TABLE IF EXISTS ${FavColumns.TABLE_NAME}"
        private const val CREATE_TABLE = "CREATE TABLE ${FavColumns.TABLE_NAME} " +
                "(${FavColumns.ID} INTEGER PRIMARY KEY, " +
                "${FavColumns.TITLE} TEXT, " +
                "${FavColumns.OVERVIEW} TEXT, " +
                "${FavColumns.BACKDROP} TEXT, " +
                "${FavColumns.POSTER} TEXT, " +
                "${FavColumns.RELEASE} TEXT, " +
                "${FavColumns.VOTE} TEXT, " +
                "${FavColumns.TYPE} TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP)
        onCreate(db)
    }
}