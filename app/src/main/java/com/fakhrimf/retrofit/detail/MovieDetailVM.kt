package com.fakhrimf.retrofit.detail

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import java.util.*

class MovieDetailVM(application: Application) : AndroidViewModel(application) {

    private fun setModel(movieModel: MovieModel): FavoriteModel {
        return FavoriteModel(movieModel.id, movieModel.title, movieModel.overview, movieModel.posterPath, movieModel.backDropPath, movieModel.releaseDate, movieModel.vote)
    }

    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favoritesList = ArrayList<FavoriteModel>()
        cursor.moveToFirst()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(FavColumns.ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.TITLE))
            val vote = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.VOTE))
            val overview = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.OVERVIEW))
            val release = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.RELEASE))
            val poster = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.POSTER))
            val backdrop = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.BACKDROP))
            favoritesList.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true))
        }
        return favoritesList
    }

    fun checkFavorite(movieModel: MovieModel): Boolean {
        FavoritesHelper(getApplication()).open()
        val cursor = FavoritesHelper(getApplication()).queryCall()
        val favoritesList = cursorToArrayList(cursor)
        var check = false
        for (i in 0 until favoritesList.size) {
            if (movieModel.id == favoritesList[i].id) {
                check = true
            }
        }
        return check
    }

    fun add(movieModel: MovieModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val values = ContentValues()
        val favoriteModel = setModel(movieModel)
        values.put(FavColumns.ID, favoriteModel.id)
        values.put(FavColumns.TITLE, favoriteModel.title)
        values.put(FavColumns.BACKDROP, favoriteModel.backDropPath)
        values.put(FavColumns.OVERVIEW, favoriteModel.overview)
        values.put(FavColumns.POSTER, favoriteModel.posterPath)
        values.put(FavColumns.RELEASE, favoriteModel.releaseDate)
        values.put(FavColumns.VOTE, favoriteModel.vote)
        val result = favoritesHelper.insert(values)
        val context = getApplication() as Context

        if (result > 0) {
            movieModel.isFavorite = true
            Toast.makeText(context, "${movieModel.title} " + context.getString(R.string.added_to_favorites), Toast.LENGTH_LONG)
                .show()
        } else {
            movieModel.isFavorite = true
            Toast.makeText(context, context.getString(R.string.duplicate_constraint_error), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun delete(movieModel: MovieModel) {
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val favoriteModel = setModel(movieModel)
        val result = favoritesHelper.delete(favoriteModel.id as Int)
        val context = getApplication() as Context
        if (result > 0) {
            movieModel.isFavorite = false
            Toast.makeText(context, "${movieModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG)
                .show()
        } else {
            val resultTwo = favoritesHelper.delete(favoriteModel.title + "")
            if (resultTwo > 0) {
                Toast.makeText(getApplication(), "${movieModel.title} " + context.getString(R.string.removed_from_favorites), Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(getApplication(), context.getString(R.string.error_delete), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}