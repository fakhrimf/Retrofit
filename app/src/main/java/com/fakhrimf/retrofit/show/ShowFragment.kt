package com.fakhrimf.retrofit.show

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.ShowDetailActivity
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.*
import com.fakhrimf.retrofit.utils.source.remote.ApiClient
import com.fakhrimf.retrofit.utils.source.remote.ApiInterface
import kotlinx.android.synthetic.main.fragment_show.*
import kotlinx.coroutines.*

class ShowFragment : Fragment(), ShowUserActionListener {
    private lateinit var type: Type
    private lateinit var showVM: ShowVM
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        showVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application) //Double bang call is used because AndroidViewModelFactory needed application, not application?
            ).get(ShowVM::class.java)
        type = showVM.getSharedPreferences()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setRecycler(type: Type) {
        showVM.setSharedPreferences(type)
        if (!showVM.getIsLoaded()) {
            srl.isRefreshing = true
            rvShow?.apply {
                animate().alpha(TRANSPARENT_ALPHA).setDuration(DURATION).setListener(null)
            }
            job = GlobalScope.launch(Dispatchers.IO) {
                //Background Thread, fetching API data from https://themoviedb.org
                val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                val rvShow = view?.findViewById<RecyclerView>(R.id.rvShow)
                showVM.getPopularShow(apiInterface)
                showVM.getLatestShow(apiInterface)
                delay(2000)

                //Main Thread
                withContext(Dispatchers.Main) {
                    if (type == Type.LIST || type == Type.CARD) rvShow?.layoutManager =
                        LinearLayoutManager(context)
                    else rvShow?.layoutManager = GridLayoutManager(context, 2)
                    showVM.showList?.let {
                        when (type) {
                            Type.LIST -> rvShow?.adapter = ShowListAdapter(it, this@ShowFragment)
                            Type.CARD -> rvShow?.adapter = ShowCardAdapter(it, this@ShowFragment)
                            else -> rvShow?.adapter = ShowGridAdapter(it, this@ShowFragment)
                        }
                    }
                    rvShow?.apply {
                        animate().alpha(OPAQUE_ALPHA).setDuration(DURATION).setListener(null)
                    }
                    srl?.isRefreshing = false
                }
            }
        } else if (showVM.getIsLoaded()) {
            showVM.showList?.let {
                when (type) {
                    Type.LIST -> rvShow?.adapter = ShowListAdapter(it, this@ShowFragment)
                    Type.CARD -> rvShow?.adapter = ShowCardAdapter(it, this@ShowFragment)
                    else -> rvShow?.adapter = ShowGridAdapter(it, this@ShowFragment)
                }
            }
            if (type == Type.LIST || type == Type.CARD) rvShow?.layoutManager =
                LinearLayoutManager(context)
            else rvShow?.layoutManager = GridLayoutManager(context, 2)
        }
        this.type = type
    }

    private fun refresh() {
        GlobalScope.launch(Dispatchers.IO) {
            showVM.setIsLoaded(false)
            delay(50)
            withContext(Dispatchers.Main) {
                setRecycler(type)
            }
        }
        showVM.setSharedPreferences(type)
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(TYPE_KEY, type)
        super.onSaveInstanceState(outState)
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

    override fun onClickItem(showModel: ShowModel) {
        val intent = Intent(requireContext(), ShowDetailActivity::class.java)
        intent.putExtra(VALUE_KEY, showModel)
        startActivity(intent)
    }

    override fun onPause() {
        if (::job.isInitialized) {
            job.cancel("User closed", null)
        }
        super.onPause()
    }
}
