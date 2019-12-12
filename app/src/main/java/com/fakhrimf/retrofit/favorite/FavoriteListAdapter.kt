package com.fakhrimf.retrofit.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.fakhrimf.retrofit.BR
import com.fakhrimf.retrofit.databinding.FavoriteItemListBinding
import com.fakhrimf.retrofit.model.FavoriteModel
import java.util.*
import kotlin.collections.ArrayList

class FavoriteListAdapter(private val item: ArrayList<FavoriteModel>, private val favoriteUserActionListener: FavoriteUserActionListener) : RecyclerView.Adapter<FavoriteListAdapter.Holder>(), Filterable {
    private val filterList = ArrayList<FavoriteModel>(item)

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

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<FavoriteModel>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(filterList)
            } else {

                val filterPattern = constraint
                    .toString()
                    .toLowerCase(Locale.getDefault())
                    .trim()

                for (it in filterList) {
                    if (it.title?.toLowerCase(Locale.getDefault())?.contains(filterPattern) == true) {
                        filteredList.add(it)
                    }
                }
            }
            val result = FilterResults()
            result.values = filteredList
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filterList.clear()
            @Suppress("UNCHECKED_CAST")
            filterList.addAll(results?.values as ArrayList<FavoriteModel>)
            notifyDataSetChanged()
        }

    }
}