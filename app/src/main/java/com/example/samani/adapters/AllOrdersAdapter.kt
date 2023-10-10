package com.example.samani.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.samani.R
import com.example.samani.data.order.Order
import com.example.samani.data.order.OrderStatus
import com.example.samani.data.order.getOrderStatus
import com.example.samani.databinding.OrderItemBinding

class AllOrdersAdapter: RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(order:Order){
                binding.apply {
                    tvOrderId.text = order.orderId.toString()
                    tvOrderDate.text = order.date
                    val resources = itemView.resources

                    val colorDrawable = when(getOrderStatus(order.orderStatus)){
                        is OrderStatus.Ordered -> {
                            ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                        }
                        is OrderStatus.Cancelled -> {
                            ColorDrawable(resources.getColor(R.color.g_red))
                        }
                        is OrderStatus.Confirmed -> {
                            ColorDrawable(resources.getColor(R.color.green))
                        }
                        is OrderStatus.Shipping -> {
                            ColorDrawable(resources.getColor(R.color.green))
                        }
                        is OrderStatus.Shipped-> {
                            ColorDrawable(resources.getColor(R.color.green))
                        }
                        is OrderStatus.Delivered-> {
                            ColorDrawable(resources.getColor(R.color.purple_500))
                        }
                        is OrderStatus.Returned -> {
                            ColorDrawable(resources.getColor(R.color.g_red))
                        }
                    }
                    imageOrderState.setImageDrawable(colorDrawable)
                }

            }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
         return OrdersViewHolder(
             OrderItemBinding.inflate(
                 LayoutInflater.from(parent.context),parent,false
             )
         )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }
    var onClick: ((Order) -> Unit)? = null
}

