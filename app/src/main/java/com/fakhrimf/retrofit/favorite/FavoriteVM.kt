package com.fakhrimf.retrofit.favorite

import android.app.Application
import android.database.Cursor
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract.FavColumns
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import kotlinx.coroutines.*

class FavoriteVM(application: Application) : AndroidViewModel(application) {
    private lateinit var favoriteList: ArrayList<FavoriteModel>
    private var isLoaded = false /*Check whether the recyclerview is loaded or not*/

    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel>{
        val favoritesList = ArrayList<FavoriteModel>()
        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(FavColumns.ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.TITLE))
            val vote = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.VOTE))
            val overview = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.OVERVIEW))
            val release = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.RELEASE))
            val poster = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.POSTER))
            val backdrop = cursor.getString(cursor.getColumnIndexOrThrow(FavColumns.BACKDROP))
            favoritesList.add(FavoriteModel(id,title,overview,poster,backdrop,release,vote,true))
        }
        return favoritesList
    }

    fun setRecycler(recyclerView: RecyclerView, srl: SwipeRefreshLayout, info:TextView, listener: FavoriteUserActionListener) {
        favoriteList = ArrayList()
        FavoritesHelper(getApplication()).open()
        if (!isLoaded) {
            info.visibility= View.GONE
            srl.isRefreshing = true
            recyclerView.apply {
                animate()
                    .alpha(TRANSPARENT_ALPHA)
                    .setDuration(DURATION)
                    .setListener(null)
            }
            GlobalScope.launch(Dispatchers.IO) {
                val cursor = FavoritesHelper(getApplication()).queryCall()
                favoriteList = cursorToArrayList(cursor)
                delay(2000)
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = FavoriteListAdapter(favoriteList, listener)
                    recyclerView.layoutManager = LinearLayoutManager(getApplication())
                    recyclerView.apply {
                        animate()
                            .alpha(OPAQUE_ALPHA)
                            .setDuration(DURATION)
                            .setListener(null)
                    }
                    if (favoriteList.isEmpty()){
                        info.visibility = View.VISIBLE
                    }
                    srl.isRefreshing = false
                }
            }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(getApplication())
            recyclerView.adapter = FavoriteListAdapter(favoriteList, listener)
            Log.d("CHECK", "setRecycler: "+FavoriteListAdapter(favoriteList,listener).itemCount)
            if (favoriteList.isEmpty()){
                info.visibility = View.VISIBLE
            }
        }
    }

    fun refresh(recyclerView: RecyclerView, srl: SwipeRefreshLayout, info: TextView, listener: FavoriteUserActionListener) {
        isLoaded = false
        setRecycler(recyclerView,srl,info,listener)
    }

    companion object {
        private const val DURATION: Long = 250
        private const val TRANSPARENT_ALPHA = 0.0F
        private const val OPAQUE_ALPHA = 1.0F
    }
}