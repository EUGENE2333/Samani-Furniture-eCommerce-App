package com.example.samani.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samani.data.MyListProduct
import com.example.samani.data.Product
import com.example.samani.databinding.ProductRvItemBinding
import com.example.samani.helper.getProductPrice


class BestProductsAdapter:RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {
    var isProductAdded = false

    inner class BestProductsViewHolder(val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                if (product.offerPercentage != null) {
                    val offferInpercent = (product.offerPercentage * 100).toInt()
                    val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                    tvNewPrice.text = "Ksh ${String.format("%.2f", priceAfterOffer)}"
                    tvPrice.text = "Ksh ${product.price}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvOfferPercentage.text = "-${offferInpercent}%"
                    tvNoOfferPrice.visibility = View.GONE
                } else {
                    tvNoOfferPrice.text = "Ksh ${product.price}"
                    tvNewPrice.visibility = View.GONE
                    tvPrice.visibility = View.GONE
                    tvOfferPercentage.visibility = View.GONE
                }
                if(isProductAdded){
                    binding.apply {
                        imgFavorite.visibility = View.GONE
                        imgFavorite2.visibility = View.VISIBLE

                    }
                }else{
                        binding.apply {
                            imgFavorite2.visibility = View.GONE
                            imgFavorite.visibility = View.VISIBLE
                        }
                    }

                tvName.text = product.name
            }

        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this@BestProductsAdapter, diffCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestProductsAdapter.BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )

        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.binding.imgProduct.setOnClickListener {
            onClick?.invoke(product)
        }
        holder.binding.imgFavorite.setOnClickListener {
            onImgFavouriteClick?.invoke(product)
                 isProductAdded =! isProductAdded

                holder.binding.imgFavorite.visibility = View.GONE
                holder.binding.imgFavorite2.visibility = View.VISIBLE
        }

        holder.binding.imgFavorite2.setOnClickListener {
            onImgFavouriteClick?.invoke(product)
            isProductAdded =! isProductAdded
                holder.binding.imgFavorite.visibility = View.VISIBLE
                holder.binding.imgFavorite2.visibility = View.GONE

        }
    }

    var onClick: ((Product) -> Unit)? = null
    var onImgFavouriteClick: ((Product) -> Unit)? = null

}
