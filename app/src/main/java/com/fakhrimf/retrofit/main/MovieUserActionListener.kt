package com.fakhrimf.retrofit.main

import com.fakhrimf.retrofit.model.MovieModel

interface MovieUserActionListener {
    fun onClickItem(movieModel: MovieModel)
}