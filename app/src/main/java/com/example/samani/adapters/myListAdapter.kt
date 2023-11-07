package com.example.samani.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samani.data.MyListProduct
import com.example.samani.databinding.MyListRvItemBinding
import com.example.samani.helper.getProductPrice

class MyListAdapter: RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

    inner class MyListViewHolder(val binding: MyListRvItemBinding): RecyclerView.ViewHolder(binding.root){
       fun bind(myListProduct: MyListProduct){
           binding.apply {
               Glide.with(itemView).load(myListProduct.product.images[0]).into(imgProduct)
               tvName.text = myListProduct.product.name
               if(myListProduct.product.offerPercentage != null) {
                   val priceAfterPercentage = myListProduct.product.offerPercentage.getProductPrice(myListProduct.product.price)
                   tvNewPrice.text = "Ksh ${String.format("%.2f", priceAfterPercentage)}"

                   myListProduct.product.offerPercentage?.let {
                       val productPercentage = (it * 100).toInt()
                       tvOffer.visibility = View.VISIBLE
                      tvOffer.text = "${productPercentage}% off"

                   }
               }else{
                   tvOffer.visibility = View.GONE
                   tvNewPrice.text = "Ksh " +myListProduct.product.price.toString()
               }

           }
       }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<MyListProduct>(){

        override fun areItemsTheSame(oldItem: MyListProduct, newItem: MyListProduct): Boolean {
         return  oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: MyListProduct, newItem: MyListProduct): Boolean {
          return  oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this@MyListAdapter,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        return MyListViewHolder(
            MyListRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
       val myListProduct = differ.currentList[position]
        holder.bind(myListProduct)

        holder.binding.imgProduct.setOnClickListener {
                 onImageClick?.invoke(myListProduct)
        }
        holder.binding.AddToCartButton.setOnClickListener {
               onViewClick?.invoke(myListProduct)
        }
        holder.binding.removeButton.setOnClickListener {
              onRemoveClick?.invoke(myListProduct)
        }
    }

    var onRemoveClick:( (MyListProduct) -> Unit)? = null
    var onViewClick:( (MyListProduct) -> Unit)? = null
    var onImageClick:((MyListProduct) -> Unit)? = null

}