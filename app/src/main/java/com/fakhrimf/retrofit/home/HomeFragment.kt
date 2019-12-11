package com.fakhrimf.retrofit.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.utils.SectionAdapter
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sectionAdapter = SectionAdapter(requireContext(), childFragmentManager)
        val viewPager = view?.findViewById(R.id.viewPager) as ViewPager
        viewPager.adapter = sectionAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
