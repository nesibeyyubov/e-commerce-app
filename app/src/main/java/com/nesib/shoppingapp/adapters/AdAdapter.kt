package com.nesib.shoppingapp.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.nesib.shoppingapp.R

class AdAdapter : RecyclerView.Adapter<AdAdapter.AdHolder>() {
    inner class AdHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adImage = view.findViewById<RoundedImageView>(R.id.adImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ad_item, parent, false)
        return AdHolder(view)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        when (position) {
            0 -> holder.adImage.setImageResource(R.drawable.ad_one)
            1 -> holder.adImage.setImageResource(R.drawable.ad_two)
            2 -> holder.adImage.setImageResource(R.drawable.ad_three)
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}