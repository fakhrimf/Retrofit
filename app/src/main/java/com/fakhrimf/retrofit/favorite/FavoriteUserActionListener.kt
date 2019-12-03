package com.fakhrimf.retrofit.favorite

import com.fakhrimf.retrofit.model.FavoriteModel

interface FavoriteUserActionListener {
    fun onClickItem(favoriteModel: FavoriteModel)
}