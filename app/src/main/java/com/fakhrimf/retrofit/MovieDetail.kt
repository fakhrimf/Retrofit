package com.fakhrimf.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.detail.MovieDetailFragment
import com.fakhrimf.retrofit.main.MainVM
import com.fakhrimf.retrofit.model.MovieModel

class MovieDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val main = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(MainVM::class.java)
        val model = intent.getParcelableExtra<MovieModel>(main.getParcelKey())
        val transaction = supportFragmentManager.beginTransaction()
        val movieFragment = MovieDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(main.getParcelKey(), model)
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
