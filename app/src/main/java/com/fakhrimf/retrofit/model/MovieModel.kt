package com.fakhrimf.retrofit.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieModel(
        var id: Int?,
        @SerializedName("original_title")
        val title: String?,
        @SerializedName("overview")
        var overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("backdrop_path")
        val backDropPath: String?,
        @SerializedName("release_date")
        var releaseDate: String?,
        @SerializedName("vote_average")
        var vote: String?,
        var isFavorite: Boolean?
) : Parcelable