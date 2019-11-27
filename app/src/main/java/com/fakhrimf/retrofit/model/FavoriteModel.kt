package com.fakhrimf.retrofit.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavoriteModel(
        var id:Int?,
        var title:String?,
        var overview:String?,
        var posterPath:String?,
        var backDropPath:String?,
        var releaseDate:String?,
        var vote:String?,
        var isFavorite: Boolean = false
):Parcelable