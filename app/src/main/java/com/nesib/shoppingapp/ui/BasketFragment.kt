package com.nesib.shoppingapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.adapters.BasketAdapter
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.viewmodels.FoodViewModel
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketFragment : Fragment(R.layout.fragment_basket) {
    private lateinit var userViewModel: UserViewModel
    private lateinit var foodViewModel: FoodViewModel
    private lateinit var basketAdapter: BasketAdapter
    private lateinit var loadingDialog: AlertDialog

    private lateinit var orderBtnProgressBar: ProgressBar
    private lateinit var orderBtnText: TextView
    private lateinit var basketList: MutableList<Food>

    private lateinit var userData: User
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        initializeViewModels()
        setupBasketRecyclerView()
        subscribeObservers()
    }

    private fun setupUi() {
        val bottomNavBar =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNavBar.isVisible) {
            bottomNavBar.visibility = View.GONE
        }
        createOrderLoadingDialog()
        checkoutButton.setOnClickListener { showCheckoutDialog() }
        backButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    private fun showCheckoutDialog() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_dialog, null, false)
        bottomSheet.setContentView(bottomSheetView)
        val textTotal = bottomSheetView.findViewById<TextView>(R.id.textTotal)
        val textSubTotal = bottomSheetView.findViewById<TextView>(R.id.textSubTotal)
        val textTax = bottomSheetView.findViewById<TextView>(R.id.textTax)
        val orderBtn = bottomSheetView.findViewById<LinearLayout>(R.id.orderBtn)
        orderBtnProgressBar = bottomSheetView.findViewById(R.id.orderBtnProgressBar)
        orderBtnText = bottomSheetView.findViewById(R.id.orderBtnText)
        var subTotalAmount = 0f
        basketAdapter.basketItemList.forEach { food ->
            subTotalAmount += (food.price!! * food.count)
        }
        val taxAmount = (subTotalAmount * 5) / 100
        textSubTotal.text = subTotalAmount.format(2) + " AZN"
        textTax.text = taxAmount.format(2) + " AZN"
        textTotal.text = (taxAmount + subTotalAmount).format(2) + " AZN"
        bottomSheet.show()
        orderBtn.setOnClickListener { order(bottomSheet, orderBtn) }
    }

    private fun order(bottomSheet: BottomSheetDialog, orderBtn: LinearLayout) {
        toggleOrderBtnLoader(loading = true)
        orderBtn.isEnabled = false
        foodViewModel.order(basketAdapter.basketItemList).addOnCompleteListener {
            if (it.isSuccessful) {
                foodViewModel.resetBasket()
                    .addOnCompleteListener {
                        toggleOrderBtnLoader(loading = false)
                        bottomSheet.dismiss()
                        loadingDialog.show()
                        Handler().postDelayed({
                            loadingDialog.dismiss()
                            userViewModel.getUserData().value!!.basket = ArrayList()
                            findNavController().popBackStack()
                        }, 3200)
                    }

            } else {
                orderBtn.isEnabled = true
                toggleOrderBtnLoader(false)
                Toast.makeText(context, "An error happened,please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun toggleOrderBtnLoader(loading: Boolean) {
        orderBtnProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        orderBtnText.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun createOrderLoadingDialog() {
        loadingDialog = AlertDialog.Builder(context)
            .setView(R.layout.order_done_box)
            .setCancelable(false)
            .create()
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun initializeViewModels() {
        userViewModel = (activity as MainActivity).userViewModel!!
        foodViewModel = (activity as MainActivity).foodViewModel!!
    }

    private fun incOrDecFoodCount(isIncrement: Boolean, selectedFood: Food) {
        var foodIndex = 0
        for ((index, food) in basketList.withIndex()) {
            if (food.image == selectedFood.image) {
                if (isIncrement) {
                    food.count++
                } else {
                    if (food.count >= 2) {
                        food.count--
                    }
                }
                foodViewModel.incOrDecFoodCount(food, isIncrement)
                foodIndex = index
                break
            }
        }
        basketAdapter.basketItemList = basketList
        basketAdapter.notifyItemChanged(foodIndex)
    }

    private fun setupBasketRecyclerView() {
        basketAdapter = BasketAdapter()
        basketAdapter.onIncButtonClickListener = { food -> incOrDecFoodCount(true, food) }
        basketAdapter.onDecButtonClickListener = { food -> incOrDecFoodCount(false, food) }
        basketRecyclerView.itemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(
                viewHolder: RecyclerView.ViewHolder,
                payloads: MutableList<Any>
            ): Boolean {
                return true
            }
        }
        basketAdapter.onItemDeleteListener = { foodToDelete, index ->
            foodViewModel.deleteFoodFromBasket(foodToDelete)
                .addOnCompleteListener {
                    basketList.remove(foodToDelete)
                    basketAdapter.basketItemList = basketList
                    basketAdapter.notifyItemRemoved(index)
                    updateBasketEmpty()
                }
        }

        basketRecyclerView.apply {
            adapter = basketAdapter
            layoutManager = LinearLayoutManager(this@BasketFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun subscribeObservers(){
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.getUserData(forceDataFetch = false).observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                this.userData = userData
                basketList = userData.basket as MutableList<Food>
                basketAdapter.basketItemList = userData.basket
                if (basketList.size != 0) {
                    basketAdapter.notifyDataSetChanged()
                } else {
                    updateBasketEmpty()
                }
            }
        }
    }

    private fun updateBasketEmpty() {
        checkoutButton.isEnabled = false
        checkoutButton.setBackgroundResource(R.color.colorPrimaryLight)
        noFoodsAdded.visibility = View.VISIBLE
    }

}