package com.fakhrimf.retrofit.favorite

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.FavoriteDetailActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.settings.SettingsActivity
import com.fakhrimf.retrofit.utils.DURATION
import com.fakhrimf.retrofit.utils.OPAQUE_ALPHA
import com.fakhrimf.retrofit.utils.TRANSPARENT_ALPHA
import com.fakhrimf.retrofit.utils.VALUE_KEY
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.coroutines.*

class FavoriteFragment : Fragment(), FavoriteUserActionListener {
    private lateinit var favoriteVM: FavoriteVM
    private lateinit var job: Job
    val adapter by lazy {
        FavoriteListAdapter(favoriteVM.favoriteList, this@FavoriteFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        favoriteVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(FavoriteVM::class.java)
    }

    private fun setRecycler() {
        FavoritesHelper(requireContext()).open()
        if (!favoriteVM.getIsLoaded()) {
            tvInfo.visibility = View.GONE
            srl.isRefreshing = true
            rvFavorite?.apply {
                animate().alpha(TRANSPARENT_ALPHA).setDuration(DURATION).setListener(null)
            }
            job = GlobalScope.launch(Dispatchers.IO) {
                val cursor = FavoritesHelper(requireContext()).queryCall()
                favoriteVM.favoriteList = favoriteVM.cursorToArrayList(cursor)
                delay(2000)
                withContext(Dispatchers.Main) {
                    rvFavorite?.adapter = adapter
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
            rvFavorite?.adapter = adapter
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
                setRecycler()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        setRecycler()
        srl.setOnRefreshListener {
            refresh()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
            else -> {
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_favorite, menu)
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_query_hint_favorites)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                if (newText?.isEmpty() == true) adapter.resetList()
                return false
            }
        })
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            val bottomNavMain = activity?.findViewById<View>(R.id.bottomNavMain)
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                setItemVisibility(menu, false)
                bottomNavMain?.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                setItemVisibility(menu, true)
                bottomNavMain?.visibility = View.VISIBLE
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setItemVisibility(menu: Menu, visible: Boolean) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item != menu.findItem(R.id.search)) item.isVisible = visible
            activity?.invalidateOptionsMenu()
        }
    }

    override fun onClickItem(favoriteModel: FavoriteModel) {
        val intent = Intent(requireContext(), FavoriteDetailActivity::class.java)
        intent.putExtra(VALUE_KEY, favoriteModel)
        startActivity(intent)
    }

    override fun onPause() {
        if (::job.isInitialized) {
            job.cancel("User closed", null)
        }
        super.onPause()
    }
}
