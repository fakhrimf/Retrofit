package com.fakhrimf.retrofit.movie

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.model.MovieResponse
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MovieVM(application: Application) : AndroidViewModel(application) {
    val context = getApplication() as Context
    private var isLoaded = false /*Check whether the recyclerview is loaded or not*/
    val moviesList: MutableLiveData<ArrayList<MovieModel>> by lazy {
        MutableLiveData<ArrayList<MovieModel>>()
    }
    val type: MutableLiveData<Type> by lazy {
        MutableLiveData<Type>()
    }

    fun getIsLoaded(): Boolean {
        return isLoaded
    }

    fun setIsLoaded(isLoaded: Boolean) {
        this.isLoaded = isLoaded
    }

    fun getPopularMovies(apiInterface: ApiInterface) {
        val apiKey = API_KEY
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val call: Call<MovieResponse> = apiInterface.getPopularMovie(apiKey, locale)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show()
                Log.d(TAG_ERROR, MOVIE_POPULAR_FAIL)
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                moviesList.value = response.body()?.results
                moviesList.value?.let {
                    val context = getApplication() as Context
                    for (i in 0 until it.size) {
                        it[i].type = context.getString(R.string.movies)
                    }
                }
                isLoaded = true
            }
        })
    }

    fun getLatestMovies(apiInterface: ApiInterface): MovieModel? {
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
        return if (firstRun()) {
            Type.LIST
        } else {
            when (prefs.getString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_LIST)) {
                VALUE_CARD -> Type.CARD
                VALUE_GRID -> Type.GRID
                else -> Type.LIST
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
            Type.CARD -> prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_CARD)
            Type.GRID -> prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_GRID)
            else -> prefs.putString(PREFERENCE_MOVIE_TYPE_KEY, VALUE_LIST)
        }
        prefs.apply()
    }
}