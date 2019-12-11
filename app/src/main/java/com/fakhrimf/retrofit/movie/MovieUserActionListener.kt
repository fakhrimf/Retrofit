package com.fakhrimf.retrofit.movie

import com.fakhrimf.retrofit.model.MovieModel

interface MovieUserActionListener {
    fun onClickItem(movieModel: MovieModel)
}