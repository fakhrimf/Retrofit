package com.fakhrimf.retrofit.model

data class ShowResponse(
    var page:Int,
    val results : ArrayList<ShowModel>,
    val totalResult : Int,
    val totalPage : Int
)