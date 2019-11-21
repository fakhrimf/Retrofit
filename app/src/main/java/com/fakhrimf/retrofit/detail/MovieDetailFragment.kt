package com.fakhrimf.retrofit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.databinding.FragmentMovieDetailBinding
import com.fakhrimf.retrofit.main.MainVM
import com.fakhrimf.retrofit.model.MovieModel

class MovieDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val main = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get(MainVM::class.java)
        val model = arguments?.getParcelable<MovieModel>(main.getParcelKey())
        binding.apply {
            vm = model
        }
        return view
    }
}
