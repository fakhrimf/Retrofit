package com.fakhrimf.cinemalistfavorites.model

import android.os.Parcelable
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
        var isFavorite: Boolean = false,
        var type: String?
):Parcelable