package com.fakhrimf.retrofit.detail

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import java.util.*

class FavoriteDetailVM(application: Application) : AndroidViewModel(application) {
    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favoritesList = ArrayList<FavoriteModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.ID))
            val title =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TITLE))
            val vote =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.VOTE))
            val type =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TYPE))
            val overview =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.OVERVIEW))
            val release =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.RELEASE))
            val poster =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.POSTER))
            val backdrop =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.BACKDROP))
            favoritesList.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true, type))
        }
        return favoritesList
    }

    fun checkFavorite(favoriteModel: FavoriteModel): Boolean {
        FavoritesHelper(getApplication()).open()
        val cursor = FavoritesHelper(getApplication()).queryCall()
        val favoritesList = cursorToArrayList(cursor)
        var check = false
        for (i in 0 until favoritesList.size) {
            if (favoriteModel.id == favoritesList[i].id) {
                check = true
            }
        }
        return check
    }

    fun add(favoriteModel: FavoriteModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val values = ContentValues()
        values.put(DatabaseContract.FavColumns.ID, favoriteModel.id)
        values.put(DatabaseContract.FavColumns.TITLE, favoriteModel.title)
        values.put(DatabaseContract.FavColumns.TYPE, favoriteModel.type)
        values.put(DatabaseContract.FavColumns.BACKDROP, favoriteModel.backDropPath)
        values.put(DatabaseContract.FavColumns.OVERVIEW, favoriteModel.overview)
        values.put(DatabaseContract.FavColumns.POSTER, favoriteModel.posterPath)
        values.put(DatabaseContract.FavColumns.RELEASE, favoriteModel.releaseDate)
        values.put(DatabaseContract.FavColumns.VOTE, favoriteModel.vote)
        val result = favoritesHelper.insert(values)
        val context = getApplication() as Context

        if (result > 0) {
            Toast.makeText(context, "${favoriteModel.title} " + context.getString(R.string.added_to_favorites), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, context.getString(R.string.duplicate_constraint_error), Toast.LENGTH_LONG).show()
        }
    }

    fun delete(favoriteModel: FavoriteModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val context = getApplication() as Context
        val result = favoritesHelper.delete(favoriteModel.id as Int)
        if (result > 0) {
            Toast.makeText(context, "${favoriteModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG).show()
        } else {
            val resultTwo = favoritesHelper.delete(favoriteModel.title + "")
            if (resultTwo > 0) {
                Toast.makeText(getApplication(), "${favoriteModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), context.getString(R.string.error_delete), Toast.LENGTH_LONG).show()
            }
        }
    }
}