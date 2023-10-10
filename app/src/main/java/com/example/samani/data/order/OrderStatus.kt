package com.example.samani.data.order

sealed class OrderStatus(val status:String){
    object Ordered: OrderStatus("Ordered")
    object Cancelled: OrderStatus("Cancelled")
    object Confirmed: OrderStatus("Confirmed")
    object Shipping: OrderStatus("Shipping")
    object Shipped: OrderStatus("Shipped")
    object Delivered: OrderStatus("Delivered")
    object Returned: OrderStatus("Returned")
}

fun getOrderStatus(status: String):OrderStatus {

    return when(status){
        "Ordered" -> {OrderStatus.Ordered}
        "Cancelled" -> {OrderStatus.Cancelled}
        "Confirmed" -> {OrderStatus.Confirmed}
        "Shipping" -> {OrderStatus.Shipping}
        "Shipped" -> {OrderStatus.Shipped}
        "Delivered" -> {OrderStatus.Delivered}
        else -> {OrderStatus.Returned}
    }

}
