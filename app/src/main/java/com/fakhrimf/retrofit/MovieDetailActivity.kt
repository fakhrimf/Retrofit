package com.fakhrimf.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fakhrimf.retrofit.detail.MovieDetailFragment
import com.fakhrimf.retrofit.model.MovieModel
import com.fakhrimf.retrofit.utils.VALUE_KEY

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = intent.getParcelableExtra<MovieModel>(VALUE_KEY)
        val transaction = supportFragmentManager.beginTransaction()
        val movieFragment = MovieDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(VALUE_KEY, model)
        movieFragment.arguments = bundle
        transaction.replace(R.id.fragmentMovieDetail, movieFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        setContentView(R.layout.activity_movie_detail)
    }

    override fun onBackPressed() {
        this.finish()
    }
}
