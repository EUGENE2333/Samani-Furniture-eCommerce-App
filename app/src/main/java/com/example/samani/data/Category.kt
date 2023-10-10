package com.example.samani.data

sealed class Category(val category: String){
    object Chair: Category("Chair")
    object Sofa: Category("Sofa")
    object Cupboard: Category("Cupboard")
    object Table: Category("Table")
    object Bed: Category("Bed")
    object Ottoman: Category("Ottoman")
    object Accessory: Category("Accessory")
    object MoreFurniture: Category("More")

}


