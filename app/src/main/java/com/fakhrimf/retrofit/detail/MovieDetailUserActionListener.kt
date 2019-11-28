package com.fakhrimf.retrofit.detail

import com.fakhrimf.retrofit.model.MovieModel

interface MovieDetailUserActionListener {
    fun addToFav(model: MovieModel)
}