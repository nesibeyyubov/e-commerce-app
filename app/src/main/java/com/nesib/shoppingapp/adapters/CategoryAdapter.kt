package com.nesib.shoppingapp.adapters

import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.model.Category
import com.nesib.shoppingapp.util.Constants

class CategoryAdapter(val inAllCategories: Boolean = false) :
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    lateinit var categoryClickListener: (Category) -> Unit
    private var categoryList = listOf(
        Category(Constants.CATEGORY_FAST_FOOD, R.drawable.fastfood),
        Category(Constants.CATEGORY_SOUPS, R.drawable.soups),
        Category(Constants.CATEGORY_SALADS, R.drawable.salads),
        Category(Constants.CATEGORY_DRINKS, R.drawable.drinks),
        Category(Constants.CATEGORY_VEGETABLES, R.drawable.vegetables),
        Category(Constants.CATEGORY_SWEETS, R.drawable.sweets),
        Category(Constants.CATEGORY_CAKES, R.drawable.cakes),
        Category(Constants.CATEGORY_ICE_CREAMS, R.drawable.ice_creams)
    )

    inner class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryImage = view.findViewById<RoundedImageView>(R.id.categoryImage)
        val categoryName = view.findViewById<TextView>(R.id.categoryName)
        val overlay = view.findViewById<View>(R.id.overlay)

        init {
            overlay.setOnClickListener {
                categoryClickListener(categoryList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view =
            if (!inAllCategories) LayoutInflater.from(parent.context)
                .inflate(R.layout.category_item, parent, false)
            else LayoutInflater.from(parent.context)
                .inflate(R.layout.category_item_expanded, parent, false)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = categoryList[position]
        holder.apply {
            categoryImage.setImageResource(category.imageUrl)
            categoryName.text = category.name
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}