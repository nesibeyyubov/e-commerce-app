package com.nesib.shoppingapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import java.util.*

object FoodRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var docRef: DocumentReference

    init {
        auth.currentUser?.let {
            docRef = db.collection("users").document(auth.currentUser!!.uid)
        }
    }

    fun getFoods(category: String): Task<QuerySnapshot> {
        return db.collection("categories")
            .document(category)
            .collection(category)
            .get()
    }

    fun addFoodToBasket(food: Food): Task<Transaction> {
        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef).toObject(User::class.java)
            snapshot?.let {
                val newBasket = snapshot.basket.toMutableList()
                newBasket.add(food)
                UserRepository.userData.value!!.basket = newBasket
                transaction.update(docRef, "basket", newBasket)
            }
        }

    }

    fun deleteFoodFromBasket(food: Food): Task<Void> {
        val newBasket = UserRepository.userData.value!!.basket.toMutableList()
        newBasket.remove(food)
        return db.collection("users")
            .document(auth.currentUser!!.uid)
            .update("basket", newBasket)
    }

    fun incOrDecFoodCount(food: Food, isIncrement: Boolean) {

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef).toObject(User::class.java)
            if (snapshot != null) {
                for (basketItem in snapshot.basket) {
                    if (basketItem.image == food.image) {
                        if (isIncrement) basketItem.count++
                        else basketItem.count--

                        break
                    }
                }
                transaction.update(docRef, "basket", snapshot.basket)
            }

        }
    }

    fun order(basket: List<Food>): Task<Void> {
        val newOrders = UserRepository.userData.value!!.orders.toMutableList()
        basket.forEach { basketItem -> newOrders.add(basketItem) }
        return db.collection("users")
            .document(auth.currentUser!!.uid)
            .update("orders",newOrders)
    }

    fun resetBasket(): Task<Void> {
        return db.collection("users")
            .document(auth.currentUser!!.uid)
            .update("basket", listOf<Food>())
    }


}