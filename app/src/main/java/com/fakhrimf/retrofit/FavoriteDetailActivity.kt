package com.fakhrimf.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fakhrimf.retrofit.detail.FavoriteDetailFragment
import com.fakhrimf.retrofit.model.FavoriteModel
import com.fakhrimf.retrofit.utils.VALUE_KEY

class FavoriteDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = intent.getParcelableExtra<FavoriteModel>(VALUE_KEY)
        val transaction = supportFragmentManager.beginTransaction()
        val favoriteDetailFragment = FavoriteDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(VALUE_KEY, model)
        favoriteDetailFragment.arguments = bundle
        transaction.replace(R.id.fragmentFavoriteDetail, favoriteDetailFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        setContentView(R.layout.activity_favorite_detail)
    }

    override fun onBackPressed() {
        this.finish()
    }
}
