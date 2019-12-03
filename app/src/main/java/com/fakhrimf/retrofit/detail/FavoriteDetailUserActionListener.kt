package com.fakhrimf.retrofit.detail

import com.fakhrimf.retrofit.model.FavoriteModel

interface FavoriteDetailUserActionListener {
    fun removeFromFav(favoriteModel: FavoriteModel)
}