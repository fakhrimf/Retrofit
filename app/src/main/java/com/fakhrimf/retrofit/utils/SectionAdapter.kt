package com.fakhrimf.retrofit.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.movie.MovieFragment
import com.fakhrimf.retrofit.show.ShowFragment

class SectionAdapter(private val context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    @StringRes
    private val tabTitles = intArrayOf(R.string.movies, R.string.shows)

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = MovieFragment()
            1 -> fragment = ShowFragment()
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }

    override fun getCount(): Int {
        return tabTitles.size
    }
}