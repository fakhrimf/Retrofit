package com.fakhrimf.retrofit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.databinding.FragmentMovieDetailBinding
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.VALUE_KEY
import kotlinx.android.synthetic.main.fragment_movie_detail.*

class MovieDetailFragment : Fragment(), MovieDetailUserActionListener {
    private lateinit var movieDetailVM: MovieDetailVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val model = arguments?.getParcelable<MovieModel>(VALUE_KEY)
        movieDetailVM =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(MovieDetailVM::class.java)
        binding.apply {
            vm = model
            listener = this@MovieDetailFragment
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val modelCheck =
            movieDetailVM.checkFavorite(arguments?.getParcelable<MovieModel>(VALUE_KEY) as MovieModel)
        if (modelCheck) {
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
        } else {
            ivStar.setImageResource(R.drawable.ic_star_24)
        }
    }

    private var isStarred = false
    override fun addToFav(model: MovieModel) {
        val modelCheck = movieDetailVM.checkFavorite(model)
        isStarred = if (!isStarred && !modelCheck) {
            ivStar.setImageResource(R.drawable.ic_star_24_yellow)
            model.isFavorite = true
            movieDetailVM.add(model)
            true
        } else {
            ivStar.setImageResource(R.drawable.ic_star_24)
            model.isFavorite = false
            movieDetailVM.delete(model)
            false
        }
    }
}
