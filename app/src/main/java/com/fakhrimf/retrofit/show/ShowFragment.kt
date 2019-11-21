package com.fakhrimf.retrofit.show

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.ShowDetail
import com.fakhrimf.retrofit.main.MainVM
import com.fakhrimf.retrofit.model.ShowModel
import kotlinx.android.synthetic.main.fragment_main.srl
import kotlinx.android.synthetic.main.fragment_show.*

class ShowFragment : Fragment(), ShowUserActionListener {
    private var type = VALUE_LIST
    private lateinit var showVM: ShowVM
    private lateinit var mainVM: MainVM

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        showVM = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(activity!!.application)
        ).get(ShowVM::class.java)
        mainVM = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(activity!!.application)
        ).get(MainVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setRecycler(type: String) {
        showVM.setRecycler(rvShow, this, type, srl)
        this.type = type
    }

    private fun refresh() {
        showVM.onRefresh(rvShow, this, type, srl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            R.id.card -> {
                type = VALUE_CARD
                refresh()
                true
            }
            R.id.grid -> {
                type = VALUE_GRID
                refresh()
                true
            }
            R.id.list -> {
                type = VALUE_LIST
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
        outState.putString(TYPE_KEY, type)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState?.getString(TYPE_KEY) == null) {
            setRecycler(type)
        } else {
            setRecycler(savedInstanceState.getString(TYPE_KEY) as String)
        }
        srl.setOnRefreshListener {
            refresh()
        }
    }

    override fun onClickItem(showModel: ShowModel) {
        val intent = Intent(requireContext(), ShowDetail::class.java)
        intent.putExtra(mainVM.getParcelKey(), showModel)
        startActivity(intent)
    }

    companion object {
        private const val TYPE_KEY = "type"
        private const val VALUE_CARD = "card"
        private const val VALUE_LIST = "list"
        private const val VALUE_GRID = "grird"
    }
}
