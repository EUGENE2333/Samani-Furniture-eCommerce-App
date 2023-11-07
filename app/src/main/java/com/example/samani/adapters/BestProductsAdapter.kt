package com.example.samani.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samani.data.Product
import com.example.samani.databinding.ProductRvItemBinding
import com.example.samani.helper.getProductPrice


class BestProductsAdapter:RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

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

                tvName.text = product.name
            }
            val sharedPreferences = binding.root.context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val productId = product.id

            // Update visibility based on SharedPreferences
            val isImgFavourite2Visible = sharedPreferences.getBoolean(productId, false)
            if (isImgFavourite2Visible) {

                binding.imgFavorite2.visibility = View.VISIBLE
                binding.imgFavorite.visibility = View.GONE

            } else {
                binding.imgFavorite2.visibility = View.GONE
                binding.imgFavorite.visibility = View.VISIBLE
            }

            binding.imgProduct.setOnClickListener {
                onClick?.invoke(product)

            }
            binding.imgFavorite.setOnClickListener {
                onImgFavouriteClick?.invoke(product)

                   binding.imgFavorite.visibility = View.GONE
                    binding.imgFavorite2.visibility = View.VISIBLE
                // Save the new visibility state to SharedPreferences
                val editor = sharedPreferences.edit()
                val newVisibility = !isImgFavourite2Visible
                editor.putBoolean(productId, newVisibility)
                editor.apply()
            }

            binding.imgFavorite2.setOnClickListener {
                onImgFavourite2Click?.invoke(product)
                   binding.imgFavorite.visibility = View.VISIBLE
                    binding.imgFavorite2.visibility = View.GONE


                // Save the new visibility state to SharedPreferences
                val editor = sharedPreferences.edit()
                val newVisibility = !isImgFavourite2Visible
                editor.putBoolean(productId, newVisibility)
                editor.apply()

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

    }

    var onClick: ((Product) -> Unit)? = null
    var onImgFavouriteClick: ((Product) -> Unit)? = null
    var onImgFavourite2Click: ((Product) -> Unit)? = null

}
