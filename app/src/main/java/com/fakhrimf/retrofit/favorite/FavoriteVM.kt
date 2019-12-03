package com.fakhrimf.retrofit.favorite

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper

class FavoriteVM(application: Application) : AndroidViewModel(application) {
    private var isLoaded = false /*Check whether the recyclerview is loaded or not*/
    lateinit var favoriteList: ArrayList<FavoriteModel>

    fun getIsLoaded(): Boolean {
        return isLoaded
    }

    fun setIsLoaded(isLoaded: Boolean) {
        this.isLoaded = isLoaded
    }

    fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favoritesList = ArrayList<FavoriteModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(FavColumns.ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.TITLE))
            val vote = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.VOTE))
            val overview = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.OVERVIEW))
            val release = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.RELEASE))
            val poster = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.POSTER))
            val backdrop = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.BACKDROP))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.TYPE))
            favoritesList.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true, type))
        }
        return favoritesList
    }

    override fun onCleared() {
        super.onCleared()
        FavoritesHelper(getApplication()).close()
    }
}