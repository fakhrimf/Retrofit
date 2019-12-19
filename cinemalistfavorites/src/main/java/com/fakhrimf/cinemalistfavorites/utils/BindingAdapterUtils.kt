package com.fakhrimf.cinemalistfavorites.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import com.fakhrimf.cinemalistfavorites.R
import com.fakhrimf.cinemalistfavorites.model.FavoriteModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapterUtils : BaseObservable() {

    @JvmStatic
    @BindingAdapter("imgRes")
    fun setImgByUrl(imageView: ImageView, resUrl: String?) {
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        if (resUrl != null) {
            Picasso.get().load(baseUrl + resUrl).into(imageView)
        } else {
            Picasso.get().load("https://www.materiaimpar.com/wp-content/uploads/2015/07/import_placeholder.png").into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("type")
    fun setString(textView: TextView, text: String?) {
        val context = textView.context
        if (text == "Movies" || text == "Film") {
            textView.setTextColor(context.getColor(R.color.movies))
            textView.text = context.getString(R.string.movie)
        } else {
            textView.setTextColor(context.getColor(R.color.shows))
            textView.text = context.getString(R.string.show)
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("set_release_date_movie")
    fun setReleaseMovie(textView: TextView, text: String) {
        val context = textView.context
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date? = sdf.parse(text)
        val check = Date() > date
        if (check) textView.text = context.getString(R.string.released_at) + " " + text
        else textView.text = context.getString(R.string.unreleased)
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("set_release_date_show")
    fun setReleaseShow(textView: TextView, text: String) {
        val context = textView.context
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date? = sdf.parse(text)
        val check = Date() > date
        if (check) textView.text = context.getString(R.string.first_aired) + " " + text
        else textView.text = context.getString(R.string.unreleased)
    }

    @SuppressLint("SetText18n", "SetTextI18n")
    @JvmStatic
    @BindingAdapter("set_release_date_favorite")
    fun setReleaseFavorite(textView: TextView, favoriteModel: FavoriteModel) {
        val context = textView.context
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date? = sdf.parse(favoriteModel.releaseDate ?: "2002-04-27")
        val check = Date() > date
        if (check) {
            if (favoriteModel.type == "Movies" || favoriteModel.type == "Film") {
                textView.text =
                    context.getString(R.string.released_at) + " " + favoriteModel.releaseDate
            } else {
                textView.text =
                    context.getString(R.string.first_aired) + " " + favoriteModel.releaseDate
            }
        } else textView.text = context.getString(R.string.unreleased)
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("set_rating")
    fun setRating(textView: TextView, text: String) {
        val context = textView.context
        if (text == "0.0" || text == "0") {
            textView.text = context.getString(R.string.unrated)
        } else textView.text = context.getString(R.string.rating) + " " + text + " / 10"
    }
}