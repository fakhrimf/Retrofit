package com.fakhrimf.retrofit.utils.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns
import java.sql.SQLException

//On Progress
class FavoritesHelper(context: Context) {
    companion object {
        private const val DB_TABLE = FavColumns.TABLE_NAME
        private lateinit var dbHelper: FavoritesDatabaseHelper
        private var INSTANCE: FavoritesHelper? = null
        private lateinit var db: SQLiteDatabase
        fun getInstance(context: Context): FavoritesHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = FavoritesHelper(context)
                    }
                }
            }
            return INSTANCE as FavoritesHelper
        }
    }

    init {
        dbHelper = FavoritesDatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        db = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
        if (db.isOpen) {
            db.close()
        }
    }

    fun queryCall(): Cursor {
        return db.query(
            DB_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${FavColumns.ID} ASC"
        )
    }

    @Throws(SQLException::class)
    fun insert(values: ContentValues?): Long {
        return db.insert(DB_TABLE, null, values)
    }

    fun delete(id: Int): Int {
        return db.delete(DB_TABLE, "${FavColumns.ID} = '$id'", null)
    }

    fun delete(name: String): Int {
        return db.delete(DB_TABLE, "${FavColumns.TITLE} = '$name'", null)
    }
}