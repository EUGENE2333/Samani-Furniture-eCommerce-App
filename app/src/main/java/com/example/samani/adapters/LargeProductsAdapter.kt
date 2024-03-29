package com.example.samani.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samani.data.Product
import com.example.samani.databinding.LargeproductRvItemBinding
import com.example.samani.helper.getProductPrice


class LargeProductsAdapter:RecyclerView.Adapter<LargeProductsAdapter.LargeProductsViewHolder>() {

    inner class LargeProductsViewHolder(private val binding: LargeproductRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                if(product.offerPercentage != null) {
                    val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                    tvNewPrice.text = "Ksh ${String.format("%.2f", priceAfterOffer)}"
                    tvPrice.text = "Ksh ${product.price}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else{
                    tvPrice.text = "Ksh ${product.price}"
                    tvNewPrice.visibility = View.GONE
                }


                    tvName.text = product.name
            }

        }

    }

    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this@LargeProductsAdapter,diffCallback)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeProductsAdapter.LargeProductsViewHolder {
            return LargeProductsViewHolder(
                LargeproductRvItemBinding.inflate(
                    LayoutInflater.from(parent.context)
                )

            )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    override fun onBindViewHolder(holder:LargeProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }

    }

    var onClick:( (Product) -> Unit)? = null
}