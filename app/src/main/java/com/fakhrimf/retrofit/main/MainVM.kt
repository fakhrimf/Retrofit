package com.fakhrimf.retrofit.main

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.model.MovieResponse
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.local.DatabaseContract
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainVM(application: Application) : AndroidViewModel(application) {
    //    lateinit var moviesList: ArrayList<MovieModel>
    val context = getApplication() as Context
    private var isLoaded = false /*Check whether the recyclerview is loaded or not*/
    val moviesList: MutableLiveData<ArrayList<MovieModel>> by lazy {
        MutableLiveData<ArrayList<MovieModel>>()
    }
    val type: MutableLiveData<Type> by lazy {
        MutableLiveData<Type>()
    }

    fun setRecycler(recyclerView: RecyclerView, listener: MovieUserActionListener, type: Type, srl: SwipeRefreshLayout) {
        if (!isLoaded) {
            srl.isRefreshing = true
            recyclerView.apply {
                animate()
                    .alpha(TRANSPARENT_ALPHA)
                    .setDuration(DURATION)
                    .setListener(null)
            }
            GlobalScope.launch(Dispatchers.IO) {
                //Background Thread, fetching API data from https://themoviedb.org
                val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                getPopularMovies(apiInterface, recyclerView, listener, type)
                getLatestMovies(apiInterface)
                delay(2000)

                //Main Thread
                withContext(Dispatchers.Main) {
                    this@MainVM.type.value = type
                    if (type == Type.LIST || type == Type.CARD) recyclerView.layoutManager =
                        LinearLayoutManager(context)
                    else recyclerView.layoutManager = GridLayoutManager(context, 2)
                    recyclerView.apply {
                        animate()
                            .alpha(OPAQUE_ALPHA)
                            .setDuration(DURATION)
                            .setListener(null)
                    }
                    srl.isRefreshing = false
                }
            }
        } else if (isLoaded) {
            this@MainVM.type.value = type
            if (type == Type.LIST || type == Type.CARD) recyclerView.layoutManager =
                LinearLayoutManager(context)
            else recyclerView.layoutManager = GridLayoutManager(context, 2)
            when (type) {
                Type.LIST -> recyclerView.adapter =
                    MovieListAdapter(moviesList.value!!, listener)
                Type.CARD -> recyclerView.adapter =
                    MovieCardAdapter(moviesList.value!!, listener)
                else -> recyclerView.adapter = MovieGridAdapter(moviesList.value!!, listener)
            }
        }
    }

    private fun setFavorites() {
        FavoritesHelper(getApplication()).open()
        val cursor = FavoritesHelper(getApplication()).queryCall()
        val favoritesList = cursorToArrayList(cursor)
        moviesList.value?.let {
            for (i in 0 until it.size) {
                for (o in 0 until favoritesList.size) {
                    if (it[i].title == favoritesList[o].title) {
                        it[i].isFavorite = true
                    }
                }
            }
        }
    }

    fun onRefresh(recyclerView: RecyclerView, listener: MovieUserActionListener, type: Type, srl: SwipeRefreshLayout) {
        srl.isRefreshing = true
        recyclerView.apply {
            animate()
                .alpha(TRANSPARENT_ALPHA)
                .setDuration(DURATION)
                .setListener(null)
            GlobalScope.launch(Dispatchers.IO) {
                //Background Thread
                val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                getPopularMovies(apiInterface, recyclerView, listener, type)
                getLatestMovies(apiInterface)
                delay(2000)

                //Main Thread
                withContext(Dispatchers.Main) {
                    this@MainVM.type.value = type
                    if (type == Type.LIST || type == Type.CARD) recyclerView.layoutManager =
                        LinearLayoutManager(context)
                    else recyclerView.layoutManager = GridLayoutManager(context, 2)
                    recyclerView.apply {
                        animate()
                            .alpha(OPAQUE_ALPHA)
                            .setDuration(DURATION)
                            .setListener(null)
                    }
                    srl.isRefreshing = false
                }
            }
        }
    }

    private fun getPopularMovies(apiInterface: ApiInterface, recyclerView: RecyclerView, listener: MovieUserActionListener, type: Type) {
        val apiKey = API_KEY
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val call: Call<MovieResponse> = apiInterface.getPopularMovie(apiKey, locale)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(
                    context,
                    context.getString(R.string.error),
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG_ERROR, MOVIE_POPULAR_FAIL)
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                moviesList.value = response.body()?.results
                if (moviesList.value == null) {
                    Log.d(TAG_ERROR, "onResponse: Response is null")
                    throw Exception("onResponse: Response is null, please retry")
                }
                moviesList.value?.let {
                    setFavorites()
                    for (i in 0 until it.size) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val date: Date? = sdf.parse(it[i].releaseDate ?: "2002-04-27")
                        val check = Date() > date
                        if (check) it[i].releaseDate =
                            context.getString(R.string.released_at) + " " + it[i].releaseDate
                        else it[i].releaseDate = context.getString(R.string.unreleased)
                        if (it[i].overview == "") it[i].overview =
                            context.getString(R.string.unavailable)
                        if (it[i].vote == "0.0" || it[i].vote == "0") it[i].vote =
                            context.getString(R.string.unrated)
                        else it[i].vote =
                            context.getString(R.string.rating) + " " + it[i].vote + " / 10"
                        if (it[i].isFavorite == null) {
                            it[i].isFavorite = false
                        }
                    }

                    when (type) {
                        Type.LIST -> recyclerView.adapter =
                            MovieListAdapter(it, listener)
                        Type.CARD -> recyclerView.adapter =
                            MovieCardAdapter(it, listener)
                        else -> recyclerView.adapter = MovieGridAdapter(it, listener)
                    }
                }
                isLoaded = true
            }
        })
    }

    fun verifyInternet(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetwork
        return networkInfo != null
    }

    private fun getLatestMovies(apiInterface: ApiInterface): MovieModel? {
        val apiKey = API_KEY
        val movie: MovieModel? = null
        val call: Call<MovieModel> = apiInterface.getLatestMovie(apiKey, "en")
        call.enqueue(object : Callback<MovieModel> {
            override fun onFailure(call: Call<MovieModel>, t: Throwable) {
                Log.d(TAG_ERROR, MOVIE_LATEST_FAIL)
            }

            override fun onResponse(call: Call<MovieModel>, response: Response<MovieModel>) {
//                val title = response.body()!!.title
//                val path = response.body()!!.posterPath
//                val overview = response.body()!!.overview
            }

        })
        return movie
    }

    fun getSharedPreferences(): Type {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE)
        println("TYPE = " + prefs.getString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_LIST))
        return if (firstRun()) {
            Type.LIST
        } else {
            when (prefs.getString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_LIST)) {
                VALUE_CARD -> {
                    Type.CARD
                }
                VALUE_GRID -> {
                    Type.GRID
                }
                else -> {
                    Type.LIST
                }
            }
        }
    }

    private fun firstRun(): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE)
        return prefs.getBoolean(PREFERENCE_FIRST_RUN_KEY, true)
    }

    fun setSharedPreferences(type: Type) {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE).edit()
        when (type) {
            Type.CARD -> {
                prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_CARD)
            }
            Type.GRID -> {
                prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_GRID)
            }
            else -> {
                prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_LIST)
            }
        }
        prefs.apply()
    }


    private fun cursorToArrayList(cursor: Cursor): ArrayList<FavoriteModel> {
        val favoritesList = ArrayList<FavoriteModel>()
        cursor.moveToFirst()
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
            favoritesList.add(FavoriteModel(id, title, overview, poster, backdrop, release, vote, true))
        }
        return favoritesList
    }
}