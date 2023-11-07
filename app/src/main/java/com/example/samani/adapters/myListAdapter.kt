package com.example.samani.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.samani.data.MyListProduct
import com.example.samani.databinding.MyListRvItemBinding

class MyListAdapter: RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

    inner class MyListViewHolder(val binding: MyListRvItemBinding): RecyclerView.ViewHolder(binding.root){


    }

    private val diffCallback = object : DiffUtil.ItemCallback<MyListProduct>(){

        override fun areItemsTheSame(oldItem: MyListProduct, newItem: MyListProduct): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: MyListProduct, newItem: MyListProduct): Boolean {
            TODO("Not yet implemented")
        }

    }
    val differ = AsyncListDiffer(this@MyListAdapter,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}