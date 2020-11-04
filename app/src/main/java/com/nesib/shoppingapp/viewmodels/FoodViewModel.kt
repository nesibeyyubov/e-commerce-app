package com.nesib.shoppingapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.gson.Gson
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.repository.FoodRepository
import com.nesib.shoppingapp.repository.UserRepository
import com.nesib.shoppingapp.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.reflect.typeOf

class FoodViewModel : ViewModel() {
    private val _foodList: MutableLiveData<DataState> = MutableLiveData()
    val foodList: LiveData<DataState>
        get() = _foodList


    fun getFoods(category: String) {
        _foodList.value = DataState.Loading()
        val response = FoodRepository.getFoods(category)
        response.addOnCompleteListener {
            if (it.isSuccessful) {
                var foodList = ArrayList<Food>()
                val documents = it.result?.documents
                documents?.let { foods ->
                    for (food in foods) {
                        val foodObject = food.toObject(Food::class.java)
                        foodList.add(foodObject!!)
                    }
                }
                _foodList.value = DataState.Success(foodList)
            } else {
                _foodList.value = DataState.Failure(it.exception?.message!!)
            }
        }
    }

    fun addFoodToBasket(food: Food): Task<Transaction> {
        return FoodRepository.addFoodToBasket(food)
    }

    fun deleteFoodFromBasket(food: Food): Task<Void> {
        return FoodRepository.deleteFoodFromBasket(food)
    }

    fun incOrDecFoodCount(food: Food, isIncrement: Boolean) {
        FoodRepository.incOrDecFoodCount(food, isIncrement)
    }

    fun order(basket: List<Food>): Task<Void> {
        return FoodRepository.order(basket)
    }

    fun resetBasket():Task<Void>{
        return FoodRepository.resetBasket()
    }


}