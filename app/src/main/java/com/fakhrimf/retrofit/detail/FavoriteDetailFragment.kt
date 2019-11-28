package com.fakhrimf.retrofit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.databinding.FragmentFavoriteDetailBinding
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_favorite_detail.*

class FavoriteDetailFragment : Fragment(), FavoriteDetailUserActionListener {
    private lateinit var favoriteDetailVM: FavoriteDetailVM

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentFavoriteDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val model = arguments?.getParcelable<FavoriteModel>(VALUE_KEY)
        favoriteDetailVM = ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(FavoriteDetailVM::class.java)
        binding.apply {
            vm = model
            listener = this@FavoriteDetailFragment
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val modelCheck = favoriteDetailVM.checkFavorite(arguments?.getParcelable<FavoriteModel>(VALUE_KEY) as FavoriteModel)
        if(modelCheck){
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
        } else {
            ivStar.setImageResource(R.drawable.ic_star_24)
        }
    }

    private var isStarred = true
    override fun removeFromFav(favoriteModel: FavoriteModel) {
        val modelCheck = favoriteDetailVM.checkFavorite(favoriteModel)
        if (isStarred && modelCheck){
            favoriteDetailVM.delete(favoriteModel)
            isStarred = false
            ivStar.setImageResource(R.drawable.ic_star_24)
        } else {
            favoriteDetailVM.add(favoriteModel)
            isStarred = true
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
        }
    }
}
