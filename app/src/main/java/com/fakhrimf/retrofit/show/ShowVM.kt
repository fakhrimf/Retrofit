package com.fakhrimf.retrofit.show

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.model.ShowResponse
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ShowVM(application: Application) : AndroidViewModel(application) {
    var showList: ArrayList<ShowModel>? = null
    private val context = getApplication() as Context
    private var isLoaded = false

    override fun onCleared() {
        super.onCleared()
        Toast.makeText(getApplication(), "Viewmodel show cleared", Toast.LENGTH_LONG).show()
    }

    fun setRecycler(recyclerView: RecyclerView, listener: ShowUserActionListener, type: Type, srl: SwipeRefreshLayout) {
        if (!isLoaded) {
            srl.isRefreshing = true
            recyclerView.apply {
                animate()
                    .alpha(TRANSPARENT_ALPHA)
                    .setDuration(DURATION)
                    .setListener(null)
            }
            GlobalScope.launch(Dispatchers.IO) {
                //Background Thread
                val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                getPopularShow(apiInterface, recyclerView, listener, type)
                getLatestShow(apiInterface)
                delay(2000)

                //Main Thread
                withContext(Dispatchers.Main) {
                    if (type == Type.LIST || type == Type.CARD) recyclerView.layoutManager =
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
            if (type == Type.LIST || type == Type.CARD) recyclerView.layoutManager =
                LinearLayoutManager(getApplication())
            else recyclerView.layoutManager = GridLayoutManager(getApplication(), 2)
            showList?.let {
                when (type) {
                    Type.LIST -> recyclerView.adapter =
                        ShowListAdapter(it, listener)
                    Type.CARD -> recyclerView.adapter =
                        ShowCardAdapter(it, listener)
                    else -> recyclerView.adapter = ShowGridAdapter(it, listener)
                }
            }
        }
    }

    fun onRefresh(recyclerView: RecyclerView, listener: ShowUserActionListener, type: Type, srl: SwipeRefreshLayout) {
        isLoaded = false
        setRecycler(recyclerView, listener, type, srl)
    }

    private fun getPopularShow(apiInterface: ApiInterface, recyclerView: RecyclerView, listener: ShowUserActionListener, type: Type) {
        val apiKey = API_KEY
        val currentLocale = context.resources.configuration.locales.get(0)
        var locale = currentLocale.toString().split("_")[1].toLowerCase(Locale.ENGLISH)
        if (locale != LOCALE_ID) {
            locale = LOCALE_EN
        }
        val call: Call<ShowResponse> = apiInterface.getPopularShow(apiKey, locale)
        call.enqueue(object : Callback<ShowResponse> {
            override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                Toast.makeText(
                    context,
                    context.getString(R.string.error),
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG_ERROR, SHOW_POPULAR_FAIL)
            }

            override fun onResponse(call: Call<ShowResponse>, response: Response<ShowResponse>) {
                showList = response.body()?.results

                showList?.let {
                    for (i in 0 until it.size) {
                        it[i].type = context.getString(R.string.shows)
                    }

                    when (type) {
                        Type.CARD -> recyclerView.adapter =
                            ShowCardAdapter(it, listener)
                        Type.LIST -> recyclerView.adapter =
                            ShowListAdapter(it, listener)
                        else -> recyclerView.adapter = ShowGridAdapter(it, listener)
                    }
                }

                isLoaded = true
            }
        })
    }

    private fun getLatestShow(apiInterface: ApiInterface): ShowModel? {
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

    fun setSharedPreferences(type: Type) {
        val prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE).edit()
        when (type) {
            Type.CARD -> {
                prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_CARD)
            }
            Type.GRID -> {
                prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_GRID)
            }
            else -> {
                prefs.putString(PREFERENCE_SHOW_TYPE_KEY, VALUE_LIST)
            }
        }
        prefs.apply()
    }
}