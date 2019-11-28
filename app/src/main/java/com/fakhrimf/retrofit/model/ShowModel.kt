package com.fakhrimf.retrofit.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShowModel(
        val id: Int,
        @SerializedName("original_name")
        val title: String?,
        @SerializedName("overview")
        var overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("backdrop_path")
        val backDropPath: String?,
        @SerializedName("first_air_date")
        var releaseDate: String?,
        @SerializedName("vote_average")
        var vote: String?
) : Parcelable