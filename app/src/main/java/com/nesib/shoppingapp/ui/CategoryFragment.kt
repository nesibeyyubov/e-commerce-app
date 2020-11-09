package com.nesib.shoppingapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.adapters.FoodAdapter
import com.nesib.shoppingapp.model.Category
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.util.Constants
import com.nesib.shoppingapp.util.DataState
import com.nesib.shoppingapp.viewmodels.FoodViewModel
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.food_item.*
import kotlinx.android.synthetic.main.fragment_basket.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.backButton

class CategoryFragment : Fragment(R.layout.fragment_category) {
    private val TAG = "mytag"
    private lateinit var userData: User
    private lateinit var foodViewModel: FoodViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var selectedCategory: Category
    private lateinit var foodList: ArrayList<Food>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModels()
        setupUi()
        getUser()
        subscribeObservers()
        foodViewModel.getFoods(selectedCategory.name.toLowerCase())
    }

    private fun showWarningDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.login_warning, null, false)
        val dialog = AlertDialog.Builder(context).setView(view)
            .create()
        view.findViewById<Button>(R.id.notNowBtn).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.signInBtn).setOnClickListener {
            dialog.dismiss()
            requireActivity().finish()
        }
        dialog.show()
    }

    private fun getUser() {
        if (!userViewModel.isAuthenticated) {
            userData = User()
            setupFoodRecyclerView()
            return
        }
        userViewModel.getUserData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                this.userData = user
                if (user.basket.isNotEmpty()) {
                    cartNotification.visibility = View.VISIBLE
                    cartNotificationText.text = user.basket.size.toString()
                } else {
                    cartNotification.visibility = View.GONE
                }
                setupFoodRecyclerView()
            }
        }
    }

    private fun initializeViewModels() {
        userViewModel = (activity as MainActivity).userViewModel!!
        foodViewModel = (activity as MainActivity).foodViewModel!!
    }

    private fun setupUi() {
        selectedCategory = CategoryFragmentArgs.fromBundle(requireArguments()).category!!
        categoryName.text = selectedCategory.name
        categoryImage.setImageResource(selectedCategory.imageUrl)

        basketButton.setOnClickListener {
            if (!userViewModel.isAuthenticated) {
                showWarningDialog()
            } else {
                findNavController().navigate(R.id.action_categoryFragment_to_basketFragment)
            }
        }
        backButton.setOnClickListener { findNavController().popBackStack() }

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNav.visibility == View.GONE) {
            bottomNav.visibility = View.VISIBLE
        }


    }

    private fun incOrDecFoodCount(isIncrement: Boolean, selectedFood: Food) {
        var foodIndex = 0
        for ((index, food) in foodList.withIndex()) {
            if (food.image == selectedFood.image) {
                if (isIncrement) food.count++
                else {
                    if (food.count >= 2) {
                        food.count--
                    }
                }
                foodIndex = index
                break
            }
        }
        foodAdapter.foodList = foodList
        foodAdapter.notifyItemChanged(foodIndex)
    }

    private fun setupFoodRecyclerView() {
        foodAdapter = FoodAdapter(userData)

        foodRecyclerView.itemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(
                viewHolder: RecyclerView.ViewHolder,
                payloads: MutableList<Any>
            ): Boolean {
                return true
            }
        }
        foodAdapter.onAddButtonClickListener =
            { food, button, buttonText, progressBar, doneIcon, basketIcon ->
                if (userViewModel.isAuthenticated) {
                    progressBar.visibility = View.VISIBLE
                    basketIcon.visibility = View.GONE
                    foodViewModel.addFoodToBasket(food).addOnCompleteListener {
                        progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            basketIcon.visibility = View.GONE
                            doneIcon.visibility = View.VISIBLE
                            button.isEnabled = false
                            buttonText.text = "Added to cart"
                            if (cartNotification.visibility == View.GONE) {
                                cartNotification.visibility = View.VISIBLE
                            }
                            cartNotificationText.text =
                                (cartNotificationText.text.toString().toInt() + 1).toString()
                        } else {
                            basketIcon.visibility = View.VISIBLE
                        }
                    }
                } else {
                    showWarningDialog()
                }
            }
        foodAdapter.onDecButtonClickListener =
            { selectedFood -> incOrDecFoodCount(false, selectedFood) }
        foodAdapter.onIncButtonClickListener =
            { selectedFood -> incOrDecFoodCount(true, selectedFood) }

        foodRecyclerView.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@CategoryFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun toggleLoading(loading: Boolean) {
        foodLoaderContainer.visibility = if (loading) View.VISIBLE else View.GONE
        foodRecyclerView.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun subscribeObservers() {
        foodViewModel.foodList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    toggleLoading(loading = false)
                    foodError.visibility = View.GONE
                    this.foodList = it.data as ArrayList<Food>
                    foodAdapter.foodList = it.data
                    if (foodList.size == 0) {
                        noFoodFoundContainer.visibility = View.VISIBLE
                        foodRecyclerView.visibility = View.GONE
                    } else {
                        foodAdapter.notifyDataSetChanged()
                    }
                }
                is DataState.Loading -> {
                    toggleLoading(true)
                }
                is DataState.Failure -> {
                    toggleLoading(false)
                    foodError.visibility = View.VISIBLE
                }
            }
        }
    }

}