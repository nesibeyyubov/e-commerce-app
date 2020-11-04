package com.nesib.shoppingapp.adapters

import android.net.Uri
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.model.Category
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.util.Constants

class OrderAdapter() : RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
    var orderList = ArrayList<Food>()

    inner class OrderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodImage = view.findViewById<RoundedImageView>(R.id.foodImage)
        val foodPrice = view.findViewById<TextView>(R.id.foodPrice)
        val foodName = view.findViewById<TextView>(R.id.foodName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderHolder(view)
    }


    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val food = orderList[position]
        holder.apply {
            Glide.with(holder.itemView.context)
                .load(food.image)
                .placeholder(R.drawable.food_placeholder)
                .into(foodImage)
            foodName.text = food.name
            foodPrice.text = "${food.price?.format(2)} AZN"
        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}