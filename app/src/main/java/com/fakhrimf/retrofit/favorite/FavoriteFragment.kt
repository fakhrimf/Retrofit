package com.fakhrimf.retrofit.favorite

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.AboutActivity

import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.utils.Type
import com.fakhrimf.retrofit.utils.source.local.FavoritesHelper
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val favoriteVM = ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(FavoriteVM::class.java)
        favoriteVM.setRecycler(rvFavorite,srl,tvInfo)
        setHasOptionsMenu(true)
        srl.setOnRefreshListener {
            favoriteVM.refresh(rvFavorite,srl)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            R.id.card -> {

                true
            }
            R.id.grid -> {

                true
            }
            R.id.list -> {

                true
            }
            else -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        FavoritesHelper(requireContext()).close()
    }
}
