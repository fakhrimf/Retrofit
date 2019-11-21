package com.fakhrimf.retrofit.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.BR
import com.fakhrimf.retrofit.databinding.MovieItemGridBinding
import com.fakhrimf.retrofit.model.MovieModel

class MovieGridAdapter(
    private val item: ArrayList<MovieModel>,
    private val movieUserActionListener: MovieUserActionListener
) : RecyclerView.Adapter<MovieGridAdapter.Holder>()/*, StaffUserActionListener*/ {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val inflater = LayoutInflater.from(p0.context)
        val binding = MovieItemGridBinding.inflate(inflater, p0, false)
        return Holder(binding.apply {
            binding.listener = movieUserActionListener
        })
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        p0.bind(item[p1])
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: MovieItemGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieModel) {
            binding.setVariable(BR.vm, item)
            binding.executePendingBindings()
        }
    }
}