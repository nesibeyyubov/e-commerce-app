package com.nesib.shoppingapp.adapters

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
import com.nesib.shoppingapp.model.Food
import kotlinx.android.synthetic.main.cart_item.*
import java.text.DecimalFormat

class BasketAdapter : RecyclerView.Adapter<BasketAdapter.BasketItemHolder>() {
    var basketItemList: List<Food> = ArrayList()
    lateinit var onItemDeleteListener: (Food,Int) -> Unit
    lateinit var onIncButtonClickListener: (Food) -> Unit
    lateinit var onDecButtonClickListener: (Food) -> Unit
    inner class BasketItemHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val foodImage = view.findViewById<RoundedImageView>(R.id.foodImage)
        val foodName = view.findViewById<TextView>(R.id.foodName)
        val foodPrice = view.findViewById<TextView>(R.id.foodPrice)
        val foodCount = view.findViewById<TextView>(R.id.foodCount)
        val incButton = view.findViewById<Button>(R.id.incButton)
        val decButton = view.findViewById<Button>(R.id.decButton)
        val deleteBtn = view.findViewById<RelativeLayout>(R.id.deleteBtn)
        val deleteBtnProgressBar = view.findViewById<ProgressBar>(R.id.deleteBtnProgressBar)
        val deleteBtnText = view.findViewById<TextView>(R.id.deleteBtnText)
        init {
            incButton.setOnClickListener(this)
            decButton.setOnClickListener(this)
            deleteBtn.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.incButton -> onIncButtonClickListener(basketItemList[adapterPosition])
                R.id.decButton -> onDecButtonClickListener(basketItemList[adapterPosition])
                R.id.deleteBtn -> {
                    deleteBtnProgressBar.visibility = View.VISIBLE
                    deleteBtnText.visibility = View.GONE
                    onItemDeleteListener(basketItemList[adapterPosition],adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return BasketItemHolder(view)
    }

    private fun Float.format(digits:Int): String {
        return "%.${digits}f".format(this)
    }

    override fun onBindViewHolder(holder: BasketItemHolder, position: Int) {
        val food = basketItemList[position]
        holder.apply {
            foodName.text = food.name

            foodPrice.text = food.price?.times(food.count)?.format(2) + " AZN"
            foodCount.text = food.count.toString()
            Glide.with(holder.itemView.context)
                .load(food.image)
                .placeholder(R.drawable.food_placeholder)
                .into(foodImage)
        }
    }

    override fun getItemCount(): Int {
        return basketItemList.size
    }
}