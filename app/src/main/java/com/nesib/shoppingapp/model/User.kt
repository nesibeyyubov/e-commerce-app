package com.nesib.shoppingapp.model

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    var basket: List<Food> = listOf(),
    val orders: List<Food> = listOf()
)