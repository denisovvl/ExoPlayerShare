package com.example.sharedexoplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharedexoplayer.databinding.ItemBinding

class MyAdapter(val onClick: (String, view: View) -> Unit) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    val items: ArrayList<Pair<String, String>> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binder = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val vh = MyHolder(binder)
        binder.root.setOnClickListener {
            onClick(vh.item, binder.imageView)
        }
        return vh
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setup(items: List<String>) {
        this.items.clear()
        this.items.addAll(items.map { Pair(it, it.hashCode().toString().take(10)) })
        notifyDataSetChanged()
    }

    class MyHolder(val binder: ItemBinding) : RecyclerView.ViewHolder(binder.root) {

        var item: String = ""

        fun onBind(item: Pair<String, String>) {
            this.item = item.first
            ViewCompat.setTransitionName(binder.imageView, item.second)
            Glide
                .with(binder.root)
                .load(item.first)
                .centerCrop()
                .into(binder.imageView)
        }
    }
}