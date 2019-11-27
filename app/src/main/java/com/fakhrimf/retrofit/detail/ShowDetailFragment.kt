package com.fakhrimf.retrofit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fakhrimf.retrofit.databinding.FragmentShowDetailBinding
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.*

class ShowDetailFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentShowDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val model = arguments?.getParcelable<ShowModel>(VALUE_KEY)
        binding.apply {
            vm = model
        }
        return view
    }
}
