package com.fakhrimf.retrofit.detail

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import java.util.*

class ShowDetailVM(application: Application) : AndroidViewModel(application) {
    private fun setModel(showModel: ShowModel): FavoriteModel {
        return FavoriteModel(showModel.id, showModel.title, showModel.overview, showModel.posterPath, showModel.backDropPath, showModel.releaseDate, showModel.vote, true, showModel.type)
    }

    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favoritesList = ArrayList<FavoriteModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.ID))
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

    fun checkFavorite(showModel: ShowModel): Boolean {
        FavoritesHelper(getApplication()).open()
        val cursor = FavoritesHelper(getApplication()).queryCall()
        val favoritesList = cursorToArrayList(cursor)
        var check = false
        for (i in 0 until favoritesList.size) {
            if (showModel.id == favoritesList[i].id) {
                check = true
            }
        }
        return check
    }

    fun add(showModel: ShowModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val values = ContentValues()
        val favoriteModel = setModel(showModel)
        values.put(DatabaseContract.FavColumns.ID, favoriteModel.id)
        values.put(DatabaseContract.FavColumns.TITLE, favoriteModel.title)
        values.put(DatabaseContract.FavColumns.BACKDROP, favoriteModel.backDropPath)
        values.put(DatabaseContract.FavColumns.OVERVIEW, favoriteModel.overview)
        values.put(DatabaseContract.FavColumns.POSTER, favoriteModel.posterPath)
        values.put(DatabaseContract.FavColumns.RELEASE, favoriteModel.releaseDate)
        values.put(DatabaseContract.FavColumns.VOTE, favoriteModel.vote)
        values.put(DatabaseContract.FavColumns.TYPE, favoriteModel.type)
        val result = favoritesHelper.insert(values)
        val context = getApplication() as Context

        if (result > 0) {
            Toast.makeText(context, "${showModel.title} " + context.getString(R.string.added_to_favorites), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, context.getString(R.string.duplicate_constraint_error), Toast.LENGTH_LONG).show()
        }
    }

    fun delete(showModel: ShowModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val favoriteModel = setModel(showModel)
        val result = favoritesHelper.delete(favoriteModel.id as Int)
        val context = getApplication() as Context
        if (result > 0) {
            Toast.makeText(context, "${showModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG).show()
        } else {
            val resultTwo = favoritesHelper.delete(favoriteModel.title + "")
            if (resultTwo > 0) {
                Toast.makeText(getApplication(), "${showModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), context.getString(R.string.error_delete), Toast.LENGTH_LONG).show()
            }
        }
    }
}