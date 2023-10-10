package com.example.samani.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.samani.data.CartProduct
import com.example.samani.databinding.BillingProductsRvItemBinding
import com.example.samani.helper.getProductPrice

class BillingProductsAdapter: RecyclerView.Adapter< BillingProductsAdapter.BillingProductsViewHolder> (){

    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct){
            binding.apply{
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterPercentage = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                   tvProductCartPrice.text =  "Ksh ${String.format("%.2f", priceAfterPercentage)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor?: Color.TRANSPARENT))

            }
        }
    }

        private val diffCallback = object: DiffUtil.ItemCallback<CartProduct>(){
            override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
                return oldItem.product == newItem.product
            }

            override fun areContentsTheSame(oldItem: CartProduct, newItem:CartProduct): Boolean {
                return  oldItem == newItem
            }

        }

        val differ = AsyncListDiffer(this@BillingProductsAdapter,diffCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }
}