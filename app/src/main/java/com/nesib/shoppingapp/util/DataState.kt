package com.nesib.shoppingapp.util

import com.nesib.shoppingapp.model.Food

sealed class DataState(val data: List<Food> = listOf(),val message: String = "") {
    class Success(data: List<Food>) : DataState(data = data)
    class Failure(message: String) : DataState(message = message)
    class Loading : DataState()
}