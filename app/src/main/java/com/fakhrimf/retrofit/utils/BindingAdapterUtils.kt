package com.fakhrimf.retrofit.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapterUtils {

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
}