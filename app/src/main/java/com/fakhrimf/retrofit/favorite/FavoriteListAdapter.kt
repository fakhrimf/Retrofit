package com.fakhrimf.retrofit.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.BR
import com.fakhrimf.retrofit.databinding.FavoriteItemListBinding
import com.fakhrimf.retrofit.model.FavoriteModel

class FavoriteListAdapter(private val item: ArrayList<FavoriteModel>, private val favoriteUserActionListener: FavoriteUserActionListener) : RecyclerView.Adapter<FavoriteListAdapter.Holder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val inflater = LayoutInflater.from(p0.context)
        val binding = FavoriteItemListBinding.inflate(inflater, p0, false)
        return Holder(binding.apply {
            listener = favoriteUserActionListener
        })
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        p0.bind(item[p1])
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(private val binding: FavoriteItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteModel) {
            binding.setVariable(BR.vm, item)
            binding.executePendingBindings()
        }
    }
}