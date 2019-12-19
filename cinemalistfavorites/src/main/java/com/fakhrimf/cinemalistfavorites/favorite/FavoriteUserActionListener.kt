package com.fakhrimf.cinemalistfavorites.favorite

import com.fakhrimf.cinemalistfavorites.model.FavoriteModel

interface FavoriteUserActionListener {
    fun onClickItem(favoriteModel: FavoriteModel)
}