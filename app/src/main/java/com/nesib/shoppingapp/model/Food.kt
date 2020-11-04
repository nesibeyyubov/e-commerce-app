package com.nesib.shoppingapp.model

data class Food(
    val name: String? = null,
    var price: Float? = null,
    val restaurant: String? = null,
    val image: String? = null,
    var count:Int = 1
)