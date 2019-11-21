package com.fakhrimf.retrofit.main

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fakhrimf.retrofit.BuildConfig
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.model.MovieResponse
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainVM(application: Application) : AndroidViewModel(application) {
    lateinit var moviesList: ArrayList<MovieModel>
    private var isLoaded = false /*Check whether the recyclerview is loaded or not*/

    fun setRecycler(recyclerView: RecyclerView, listener: MovieUserActionListener, type: String, srl: SwipeRefreshLayout) {
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
                    if (type == VALUE_LIST || type == VALUE_CARD) recyclerView.layoutManager =
                        LinearLayoutManager(getApplication())
                    else recyclerView.layoutManager = GridLayoutManager(getApplication(), 2)
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
            if (type == VALUE_LIST || type == VALUE_CARD) recyclerView.layoutManager =
                LinearLayoutManager(getApplication())
            else recyclerView.layoutManager = GridLayoutManager(getApplication(), 2)
            when (type) {
                VALUE_LIST -> recyclerView.adapter =
                    MovieListAdapter(moviesList, listener)
                VALUE_CARD -> recyclerView.adapter =
                    MovieCardAdapter(moviesList, listener)
                else -> recyclerView.adapter = MovieGridAdapter(moviesList, listener)
            }
        }
    }

    fun onRefresh(recyclerView: RecyclerView, listener: MovieUserActionListener, type: String, srl: SwipeRefreshLayout) {
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
                    if (type == VALUE_LIST || type == VALUE_CARD) recyclerView.layoutManager =
                        LinearLayoutManager(getApplication())
                    else recyclerView.layoutManager = GridLayoutManager(getApplication(), 2)
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

    private fun getPopularMovies(apiInterface: ApiInterface, recyclerView: RecyclerView, listener: MovieUserActionListener, type: String) {
        val apiKey = BuildConfig.API_KEY
        val context = getApplication() as Context
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val call: Call<MovieResponse> = apiInterface.getPopularMovie(apiKey, locale)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(
                    getApplication(),
                    context.getString(R.string.error),
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG_ERROR, MOVIE_POPULAR_FAIL)
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                moviesList = response.body()!!.results
                for (i in 0 until moviesList.size) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date: Date = sdf.parse(moviesList[i].releaseDate)
                    val check = Date() > date
                    if (check) moviesList[i].releaseDate =
                        context.getString(R.string.released_at) + " " + moviesList[i].releaseDate
                    else moviesList[i].releaseDate = context.getString(R.string.unreleased)
                    if (moviesList[i].overview == "") moviesList[i].overview =
                        context.getString(R.string.unavailable)
                    if (moviesList[i].vote == "0.0" || moviesList[i].vote == "0") moviesList[i].vote =
                        context.getString(R.string.unrated)
                    else moviesList[i].vote =
                        context.getString(R.string.rating) + " " + moviesList[i].vote + " / 10"
                }

                when (type) {
                    VALUE_LIST -> recyclerView.adapter =
                        MovieListAdapter(moviesList, listener)
                    VALUE_CARD -> recyclerView.adapter =
                        MovieCardAdapter(moviesList, listener)
                    else -> recyclerView.adapter = MovieGridAdapter(moviesList, listener)
                }

                isLoaded = true
            }
        })
    }

    fun verifyInternet(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getLatestMovies(apiInterface: ApiInterface): MovieModel? {
        val apiKey = BuildConfig.API_KEY
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

    fun getParcelKey(): String {
        return VALUE_KEY
    }

    companion object {
        private const val LOCALE_ID = "id"
        private const val LOCALE_EN = "en"
        private const val MOVIE_LATEST_FAIL = "Failed to fetch Latest Movies"
        private const val MOVIE_POPULAR_FAIL = "Failed to fetch Popular Movies"
        private const val TAG_ERROR = "ERR"
        private const val VALUE_CARD = "card"
        private const val VALUE_LIST = "list"
        private const val VALUE_KEY = "model"
        private const val DURATION: Long = 250
        private const val TRANSPARENT_ALPHA = 0.0F
        private const val OPAQUE_ALPHA = 1.0F
    }
}