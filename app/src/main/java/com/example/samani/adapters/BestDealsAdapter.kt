package com.example.samani.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samani.data.Product
import com.example.samani.databinding.BestDealsRvItemBinding
import com.example.samani.helper.getProductPrice

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>(){

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SuspiciousIndentation")
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                tvDealProductName.text = product.name
                    product.offerPercentage?.let {
                        val productPercentage = (it * 100).toInt()
                        tvOfferPercentage.text = "${productPercentage}% off deal"
                    }
                        val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                        tvNewPrice.text = "Ksh ${String.format("%.2f", priceAfterOffer)}"
                        tvOldPrice.text = "Ksh ${product.price}"
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

    val differ = AsyncListDiffer(this@BestDealsAdapter,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick:( (Product) -> Unit)? = null
}