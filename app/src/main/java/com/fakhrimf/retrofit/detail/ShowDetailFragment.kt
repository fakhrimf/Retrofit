package com.fakhrimf.retrofit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.databinding.FragmentShowDetailBinding
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_show_detail.*

class ShowDetailFragment : Fragment(), ShowDetailUserActionListener {
    private lateinit var showDetailVM: ShowDetailVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentShowDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        showDetailVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(ShowDetailVM::class.java)
        val model = arguments?.getParcelable<ShowModel>(VALUE_KEY)
        binding.apply {
            vm = model
            listener = this@ShowDetailFragment
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val modelCheck =
            showDetailVM.checkFavorite(arguments?.getParcelable<ShowModel>(VALUE_KEY) as ShowModel)
        if (modelCheck) {
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
        } else {
            ivStar.setImageResource(R.drawable.ic_star_24)
        }
    }

    private var isStarred = false
    override fun addToFav(model: ShowModel) {
        val modelCheck = showDetailVM.checkFavorite(model)
        isStarred = if (!isStarred && !modelCheck) {
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
            showDetailVM.add(model)
            true
        } else {
            ivStar.setImageResource(R.drawable.ic_star_24)
            showDetailVM.delete(model)
            false
        }
    }
}
