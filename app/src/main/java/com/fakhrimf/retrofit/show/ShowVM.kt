package com.fakhrimf.retrofit.show

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.model.ShowResponse
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ShowVM(application: Application) : AndroidViewModel(application) {
    var showList: ArrayList<ShowModel>? = null
    private val context = getApplication() as Context
    private var isLoaded = false

    fun getIsLoaded(): Boolean {
        return isLoaded
    }

    fun setIsLoaded(isLoaded: Boolean) {
        this.isLoaded = isLoaded
    }

    fun getPopularShow(apiInterface: ApiInterface) {
        val apiKey = API_KEY
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val call: Call<ShowResponse> = apiInterface.getPopularShow(apiKey, locale)
        call.enqueue(object : Callback<ShowResponse> {
            override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show()
                Log.d(TAG_ERROR, SHOW_POPULAR_FAIL)
            }

            override fun onResponse(call: Call<ShowResponse>, response: Response<ShowResponse>) {
                showList = response.body()?.results
                showList?.let {
                    for (i in 0 until it.size) {
                        it[i].type = context.getString(R.string.shows)
                    }
                }
                isLoaded = true
            }
        })
    }

    fun getLatestShow(apiInterface: ApiInterface): ShowModel? {
        val apiKey = API_KEY
        val showModel: ShowModel? = null
        val call: Call<ShowModel> = apiInterface.getLatestShow(apiKey, "en")
        call.enqueue(object : Callback<ShowModel> {
            override fun onFailure(call: Call<ShowModel>, t: Throwable) {
                Log.d(TAG_ERROR, SHOW_LATEST_FAIL)
            }

            override fun onResponse(call: Call<ShowModel>, response: Response<ShowModel>) {
                //                val title = response.body()!!.title
                //                val path = response.body()!!.posterPath
                //                val overview = response.body()!!.overview
            }

        })
        return showModel
    }

    private fun firstRun(): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREFERENCE_FIRST_RUN_KEY, true)
    }

    private fun endFirstRun() {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE).edit()
        prefs.putBoolean(PREFERENCE_FIRST_RUN_KEY, false).apply()
    }

    fun getSharedPreferences(): Type {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        return if (firstRun()) {
            endFirstRun()
            Type.LIST
        } else {
            return when (prefs.getString(PREFERENCE_SHOW_TYPE_KEY, VALUE_LIST)) {
                VALUE_CARD -> Type.CARD
                VALUE_GRID -> Type.GRID
                else -> Type.LIST
            }
        }
    }

    fun setSharedPreferences(type: Type) {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE).edit()
        when (type) {
            Type.CARD -> prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_CARD)
            Type.GRID -> prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_GRID)
            else -> prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_LIST)
        }
        prefs.apply()
    }
}