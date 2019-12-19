package com.fakhrimf.cinemalistfavorites.favorite

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fakhrimf.cinemalistfavorites.R
import com.fakhrimf.cinemalistfavorites.model.FavoriteModel
import com.fakhrimf.cinemalistfavorites.utils.DURATION
import com.fakhrimf.cinemalistfavorites.utils.OPAQUE_ALPHA
import com.fakhrimf.cinemalistfavorites.utils.TRANSPARENT_ALPHA
import com.fakhrimf.cinemalistfavorites.utils.source.local.DatabaseContract.FavColumns.Companion.CONTENT_URI
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.coroutines.*

class FavoriteFragment : Fragment(), FavoriteUserActionListener {
    private lateinit var favoriteVM: FavoriteVM
    private lateinit var job: Job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        favoriteVM = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(FavoriteVM::class.java)
    }

    @SuppressLint("Recycle")
    private fun setRecycler() {
        if (!favoriteVM.getIsLoaded()) {
            tvInfo.visibility = View.GONE
            srl.isRefreshing = true
            rvFavorite?.apply {
                animate().alpha(TRANSPARENT_ALPHA).setDuration(DURATION).setListener(null)
            }
            job = GlobalScope.launch(Dispatchers.IO) {
                val cursor = activity?.contentResolver?.query(CONTENT_URI,null,null,null,null)
                favoriteVM.favoriteList = favoriteVM.cursorToArrayList(cursor!!)
                delay(2000)
                withContext(Dispatchers.Main) {
                    rvFavorite?.adapter = FavoriteListAdapter(favoriteVM.favoriteList, this@FavoriteFragment)
                    rvFavorite?.layoutManager = LinearLayoutManager(requireContext())
                    favoriteVM.setIsLoaded(true)
                    rvFavorite?.apply {
                        animate().alpha(OPAQUE_ALPHA).setDuration(DURATION).setListener(null)
                    }
                    if (favoriteVM.favoriteList.isEmpty()) {
                        tvInfo.visibility = View.VISIBLE
                    }
                    srl?.isRefreshing = false
                }
            }
        } else {
            rvFavorite?.layoutManager = LinearLayoutManager(requireContext())
            rvFavorite?.adapter = FavoriteListAdapter(favoriteVM.favoriteList, this@FavoriteFragment)
            if (favoriteVM.favoriteList.isEmpty()) {
                tvInfo.visibility = View.VISIBLE
            }
        }
    }

    private fun refresh() {
        GlobalScope.launch(Dispatchers.IO) {
            favoriteVM.setIsLoaded(false)
            delay(50)
            withContext(Dispatchers.Main) {
                val handlerThread = HandlerThread("DataObserver")
                handlerThread.start()
                val handler = Handler(handlerThread.looper)
                val observe = object : ContentObserver(handler) {
                    override fun onChange(selfChange: Boolean) {
                        setRecycler()
                    }
                }
                activity?.contentResolver?.registerContentObserver(CONTENT_URI, true, observe)
                setRecycler()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val observe = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                setRecycler()
            }
        }
        activity?.contentResolver?.registerContentObserver(CONTENT_URI, true, observe)
        setRecycler()
        srl.setOnRefreshListener {
            refresh()
        }
    }

    override fun onClickItem(favoriteModel: FavoriteModel) {
        Toast.makeText(context, favoriteModel.title, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        if (::job.isInitialized) {
            job.cancel("User closed", null)
        }
        super.onPause()
    }
}
