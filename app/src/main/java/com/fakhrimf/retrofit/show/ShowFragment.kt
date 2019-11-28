package com.fakhrimf.retrofit.show

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.AboutActivity
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.ShowDetailActivity
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.TYPE_KEY
import com.fakhrimf.retrofit.utils.Type
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_main.srl
import kotlinx.android.synthetic.main.fragment_show.*

class ShowFragment : Fragment(), ShowUserActionListener {
    private lateinit var type: Type
    private lateinit var showVM: ShowVM

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        showVM = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(activity!!.application) //Double bang call is used because AndroidViewModelFactory needed application, not application?
        ).get(ShowVM::class.java)
        type = showVM.getSharedPreferences()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setRecycler(type: Type) {
        showVM.setRecycler(rvShow, this, type, srl)
        this.type = type
    }

    private fun refresh() {
        showVM.onRefresh(rvShow, this, type, srl)
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
}
