package com.fakhrimf.retrofit.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.BR
import com.fakhrimf.retrofit.databinding.ShowItemListBinding
import com.fakhrimf.retrofit.model.ShowModel

class ShowListAdapter(
    private val item: ArrayList<ShowModel>,
    private val movieUserActionListener: ShowUserActionListener
) : RecyclerView.Adapter<ShowListAdapter.Holder>()/*, StaffUserActionListener*/ {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        val inflater = LayoutInflater.from(p0.context)
        val binding = ShowItemListBinding.inflate(inflater, p0, false)
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

    inner class Holder(private val binding: ShowItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShowModel) {
            binding.setVariable(BR.vm, item)
            binding.executePendingBindings()
        }
    }
}