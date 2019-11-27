package com.fakhrimf.retrofit.detail

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import java.util.ArrayList

class MovieDetailVM(application: Application): AndroidViewModel(application) {

    private fun setModel(movieModel: MovieModel): FavoriteModel{
        return FavoriteModel(movieModel.id, movieModel.title, movieModel.overview, movieModel.posterPath, movieModel.backDropPath, movieModel.releaseDate, movieModel.vote)
    }

    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favlist = ArrayList<FavoriteModel>()
        cursor.moveToFirst()
        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TITLE))
            val vote = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.VOTE))
            val overview = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.OVERVIEW))
            val release = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.RELEASE))
            val poster = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.POSTER))
            val backdrop = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.BACKDROP))
            favlist.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true))
        }
        return favlist
    }

    fun checkFavorite(movieModel: MovieModel): Boolean {
        FavoritesHelper(getApplication()).open()
        val cursor = FavoritesHelper(getApplication()).queryCall()
        val favlist = cursorToArrayList(cursor)
        var check = false
        for (i in 0 until favlist.size){
            if (movieModel.id == favlist[i].id){
                check = true
            }
        }
        return check
    }

    fun add(movieModel: MovieModel){
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

        if(result > 0){
            movieModel.isFavorite = true
            Toast.makeText(getApplication(), result.toInt().toString(), Toast.LENGTH_LONG).show()
        } else {
            movieModel.isFavorite = true
            Toast.makeText(getApplication(), "Data already added", Toast.LENGTH_LONG).show()
        }
    }

    fun delete(movieModel: MovieModel){
        val favoritesHelper = FavoritesHelper.getInstance(getApplication())
        favoritesHelper.open()
        val favoriteModel = setModel(movieModel)
        val result = favoritesHelper.delete(favoriteModel.id as Int)
        if (result > 0){
            movieModel.isFavorite = false
            Toast.makeText(getApplication(), "Sukses dihapus menggunakan ID", Toast.LENGTH_LONG).show()
        } else {
            val resultTwo = favoritesHelper.delete(favoriteModel.title+"")
            if (resultTwo > 0){
                Toast.makeText(getApplication(), "Sukses dihapus menggunakan nama", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), favoriteModel.id.toString()+" | "+result.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}