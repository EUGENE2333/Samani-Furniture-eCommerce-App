package com.example.samani.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyListProduct(
    val product: Product
): Parcelable {
    constructor(): this(Product())
}

