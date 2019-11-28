package com.fakhrimf.retrofit.utils

import android.widget.ImageView
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.main.MainFragment
import com.fakhrimf.retrofit.main.MovieCardAdapter
import com.fakhrimf.retrofit.main.MovieGridAdapter
import com.fakhrimf.retrofit.main.MovieListAdapter
import com.fakhrimf.retrofit.model.MovieModel
import com.squareup.picasso.Picasso

object BindingAdapterUtils: BaseObservable() {

    @JvmStatic
    @BindingAdapter("imgRes")
    fun setImgByUrl(imageView: ImageView, resUrl: String?) {
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        if (resUrl != null) {
            Picasso.get().load(baseUrl + resUrl).into(imageView)
        } else {
            Picasso.get()
                .load("https://www.materiaimpar.com/wp-content/uploads/2015/07/import_placeholder.png")
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["bind:adapter","bind:type"], requireAll = false)
    fun setAdapter(recyclerView: RecyclerView, adapter: MutableLiveData<ArrayList<MovieModel>>, type: MutableLiveData<Type>){
        var type1: Type? = null
        type.value?.let {
            if (it== Type.LIST || it == Type.CARD) recyclerView.layoutManager =
                LinearLayoutManager(recyclerView.context)
            else recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)
            type1 = it
        }
        adapter.value?.let {
            when (type1) {
                Type.LIST -> recyclerView.adapter =
                    MovieListAdapter(it, MainFragment())
                Type.CARD -> recyclerView.adapter =
                    MovieCardAdapter(it, MainFragment())
                else -> recyclerView.adapter = MovieGridAdapter(it, MainFragment())
            }
        }
    }
}