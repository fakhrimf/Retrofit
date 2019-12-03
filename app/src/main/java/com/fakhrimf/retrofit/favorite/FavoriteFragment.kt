package com.fakhrimf.retrofit.favorite

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.FavoriteDetailActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_favorite.*

// FIXME: 02/12/2019 Viewmodel recreates on rotate, but not on viewpager offscreen
class FavoriteFragment : Fragment(), FavoriteUserActionListener {
    private lateinit var favoriteVM: FavoriteVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(FavoriteVM::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        favoriteVM.setRecycler(rvFavorite, srl, tvInfo, this)
        srl.setOnRefreshListener {
            favoriteVM.refresh(rvFavorite, srl, tvInfo, this)
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

    override fun onClickItem(favoriteModel: FavoriteModel) {
        val intent = Intent(requireContext(), FavoriteDetailActivity::class.java)
        intent.putExtra(VALUE_KEY, favoriteModel)
        startActivity(intent)
    }
}
