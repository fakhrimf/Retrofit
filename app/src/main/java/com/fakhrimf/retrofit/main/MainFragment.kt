package com.fakhrimf.retrofit.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.MovieDetailActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.TYPE_KEY
import com.fakhrimf.retrofit.utils.Type
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MovieUserActionListener {
    private lateinit var type: Type
    private lateinit var mainVM: MainVM

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
        mainVM.setRecycler(rvMovie, this, type, srl)
        mainVM.setSharedPreferences(type)
        this.type = type
    }

    private fun refresh() {
        mainVM.onRefresh(rvMovie, this, type, srl)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState?.getSerializable(TYPE_KEY) == null) {
            setRecycler(type)
        } else {
            setRecycler(savedInstanceState.getSerializable(TYPE_KEY) as Type)
        }
//        mainVM.setList(requireContext(), lvMovie, this)
        if (!mainVM.verifyInternet(activity as Activity)) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.attention), Toast.LENGTH_LONG).show()
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
