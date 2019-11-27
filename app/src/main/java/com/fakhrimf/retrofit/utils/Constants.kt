package com.fakhrimf.retrofit.utils

import com.fakhrimf.retrofit.BuildConfig

const val LOCALE_ID = "id"
const val LOCALE_EN = "en"
const val MOVIE_LATEST_FAIL = "Failed to fetch Latest Movies"
const val MOVIE_POPULAR_FAIL = "Failed to fetch Popular Movies"
const val SHOW_LATEST_FAIL = "Failed to fetch Latest Shows"
const val SHOW_POPULAR_FAIL = "Failed to fetch Popular Shows"
const val TAG_ERROR = "ERR"
const val DURATION: Long = 250
const val TRANSPARENT_ALPHA = 0.0F
const val OPAQUE_ALPHA = 1.0F
const val VALUE_KEY = "model"
const val TYPE_KEY = "type"
const val API_KEY = BuildConfig.API_KEY
const val PREFERENCE_MOVIE_TYPE_KEY = "type_movie"
const val PREFERENCE_SHOW_TYPE_KEY = "type_show"
const val PREFERENCE_KEY = "type_preference"
const val VALUE_LIST = "list"
const val VALUE_CARD = "card"
const val VALUE_GRID = "grid"

enum class Type {
    LIST, CARD, GRID
}