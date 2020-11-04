package com.nesib.shoppingapp.adapters

import android.net.Uri
import android.text.Layout
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

class FoodAdapter(var userData: User) : RecyclerView.Adapter<FoodAdapter.FoodHolder>() {
    var foodList = ArrayList<Food>()
    lateinit var onAddButtonClickListener: (Food, LinearLayout, TextView, ProgressBar, ImageView, ImageView) -> Unit
    lateinit var onIncButtonClickListener: (Food) -> Unit
    lateinit var onDecButtonClickListener: (Food) -> Unit

    inner class FoodHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val foodImage = view.findViewById<RoundedImageView>(R.id.foodImage)
        val foodPrice = view.findViewById<TextView>(R.id.foodPrice)
        val foodName = view.findViewById<TextView>(R.id.productName)
        val foodCount = view.findViewById<TextView>(R.id.foodCount)
        val addToCartButton = view.findViewById<LinearLayout>(R.id.addToCartButton)
        val incButton = view.findViewById<Button>(R.id.increaseButton)
        val decButton = view.findViewById<Button>(R.id.decreaseButton)

        val addToCartButtonText = view.findViewById<TextView>(R.id.addToCartButtonText)
        val addToCartButtonProgressBar =
            view.findViewById<ProgressBar>(R.id.addToCartButtonProgressBar)
        val addToCartButtonDoneIcon = view.findViewById<ImageView>(R.id.addToCartButtonDoneIcon)
        val addToCartButtonBasketIcon = view.findViewById<ImageView>(R.id.addToCartButtonBasketIcon)

        init {
            addToCartButton.setOnClickListener(this)
            incButton.setOnClickListener(this)
            decButton.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val food = foodList[adapterPosition]
            when (view?.id) {
                R.id.addToCartButton -> {
                    onAddButtonClickListener(
                        food,
                        addToCartButton,
                        addToCartButtonText,
                        addToCartButtonProgressBar,
                        addToCartButtonDoneIcon,
                        addToCartButtonBasketIcon
                    )
                }
                R.id.increaseButton -> onIncButtonClickListener(food)
                R.id.decreaseButton -> onDecButtonClickListener(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return FoodHolder(view)
    }

    private fun List<Food>.containsFood(food: Food): Boolean {
        for (data in this) {
            if (data.image == food.image) {
                return true
            }
        }
        return false
    }

    private fun Float.format(digits:Int):String{
        return "%.${digits}f".format(this)
    }


    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        val food = foodList[position]
        holder.apply {
            Glide.with(holder.itemView.context)
                .load(food.image)
                .placeholder(R.drawable.food_placeholder)
                .into(foodImage)

            foodName.text = food.name
            foodPrice.text = "${food.price?.format(2)} AZN"
            foodCount.text = food.count.toString()

            if (userData.basket.containsFood(food)) {
                addToCartButton.isEnabled = false
                addToCartButtonText.text = "Added to cart"
                addToCartButtonBasketIcon.visibility = View.GONE
                addToCartButtonDoneIcon.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}