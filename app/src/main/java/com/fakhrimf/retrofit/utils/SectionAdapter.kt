package com.fakhrimf.retrofit.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.favorite.FavoriteFragment
import com.fakhrimf.retrofit.main.MainFragment
import com.fakhrimf.retrofit.show.ShowFragment

class SectionAdapter(private val context: Context, fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    @StringRes
    private val tabTitles = intArrayOf(R.string.movies, R.string.shows, R.string.favorite)

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = MainFragment()
            1 -> fragment = ShowFragment()
            2 -> fragment = FavoriteFragment()
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