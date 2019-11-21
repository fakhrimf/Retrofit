package com.fakhrimf.retrofit.show

import com.fakhrimf.retrofit.model.ShowModel

interface ShowUserActionListener {
    fun onClickItem(showModel: ShowModel)
}