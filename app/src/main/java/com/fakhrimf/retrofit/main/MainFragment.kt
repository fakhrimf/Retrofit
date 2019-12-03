package com.fakhrimf.retrofit.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.MovieDetailActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.*

class MainFragment : Fragment(), MovieUserActionListener {
    private lateinit var type: Type
    private lateinit var mainVM: MainVM
    private lateinit var job: Job

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(TYPE_KEY, type)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        mainVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(MainVM::class.java)
        type = mainVM.getSharedPreferences()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setRecycler(type: Type) {
        mainVM.setSharedPreferences(type)
        if (!mainVM.getIsLoaded()) {
            srl.isRefreshing = true
            rvMovie.apply {
                animate().alpha(TRANSPARENT_ALPHA).setDuration(DURATION).setListener(null)
            }
            val io = Dispatchers.IO
            val main = Dispatchers.Main
            job = GlobalScope.launch(io) {
                //Background Thread, fetching API data from https://themoviedb.org
                val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                mainVM.getPopularMovies(apiInterface)
                mainVM.getLatestMovies(apiInterface)
                delay(2000)

                //Main Thread
                if (activity?.isDestroyed != true) {
                    withContext(main) {
                        if (type == Type.LIST || type == Type.CARD) rvMovie.layoutManager =
                            LinearLayoutManager(context)
                        else rvMovie.layoutManager = GridLayoutManager(context, 2)
                        mainVM.moviesList.value?.let {
                            when (type) {
                                Type.LIST -> rvMovie.adapter =
                                    MovieListAdapter(it, this@MainFragment)
                                Type.CARD -> rvMovie.adapter =
                                    MovieCardAdapter(it, this@MainFragment)
                                else -> rvMovie.adapter = MovieGridAdapter(it, this@MainFragment)
                            }
                        }
                        rvMovie.apply {
                            animate().alpha(OPAQUE_ALPHA).setDuration(DURATION).setListener(null)
                        }
                        srl.isRefreshing = false
                    }
                }
            }
        } else if (mainVM.getIsLoaded()) {
            mainVM.moviesList.value?.let {
                when (type) {
                    Type.LIST -> rvMovie.adapter = MovieListAdapter(it, this@MainFragment)
                    Type.CARD -> rvMovie.adapter = MovieCardAdapter(it, this@MainFragment)
                    else -> rvMovie.adapter = MovieGridAdapter(it, this@MainFragment)
                }
            }
            if (type == Type.LIST || type == Type.CARD) rvMovie.layoutManager =
                LinearLayoutManager(context)
            else rvMovie.layoutManager = GridLayoutManager(context, 2)
        }
        this.type = type
    }

    private fun refresh() {
        GlobalScope.launch(Dispatchers.IO) {
            mainVM.setIsLoaded(false)
            delay(50)
            withContext(Dispatchers.Main) {
                setRecycler(type)
            }
        }
        mainVM.setSharedPreferences(type)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            R.id.card -> {
                type = Type.CARD
                refresh()
                true
            }
            R.id.grid -> {
                type = Type.GRID
                refresh()
                true
            }
            R.id.list -> {
                type = Type.LIST
                refresh()
                true
            }
            else -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
        }
    }

    override fun onPause() {
        if(::job.isInitialized){
            job.cancel("User closed", null)
        }
        super.onPause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState?.getSerializable(TYPE_KEY) == null) {
            setRecycler(type)
        } else {
            setRecycler(savedInstanceState.getSerializable(TYPE_KEY) as Type)
        }
        srl.setOnRefreshListener {
            refresh()
        }
    }

    override fun onClickItem(movieModel: MovieModel) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra(VALUE_KEY, movieModel)
        startActivity(intent)
    }
}
