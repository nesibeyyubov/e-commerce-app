package com.nesib.shoppingapp.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.adapters.AdAdapter
import com.nesib.shoppingapp.adapters.CategoryAdapter
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.viewmodels.FoodViewModel
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var userViewModel: UserViewModel
    private lateinit var bottomNavBar: BottomNavigationView
    private var userData:User? = null
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (activity as MainActivity).userViewModel!!
        setupUi()
        setupAdRecyclerView()
        setupCategoryRecyclerView()
        getUser()
    }

    private fun showWarningDialog(){
        val view = LayoutInflater.from(context).inflate(R.layout.login_warning,null,false)
        val dialog = AlertDialog.Builder(context).setView(view)
            .create()
        view.findViewById<Button>(R.id.notNowBtn).setOnClickListener{
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.signInBtn).setOnClickListener {
            dialog.dismiss()
            requireActivity().finish()
        }
        dialog.show()
    }

    private fun setupUi(){
        bottomNavBar =
            requireActivity().findViewById(R.id.bottomNavigationView)

        if (!bottomNavBar.isVisible) {
            bottomNavBar.visibility = View.VISIBLE
        }


        basketBtn.setOnClickListener {
            if(!userViewModel.isAuthenticated){
                showWarningDialog()
            }else{
                findNavController().navigate(R.id.action_homeFragment_to_basketFragment)
            }
        }

        seeAllBtn.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_allCategoriesFragment) }

    }

    private fun getUser() {
        bottomNavBar.visibility = View.GONE
        if(!userViewModel.isAuthenticated) loadingFinished()
        userViewModel.getUserData().observe(viewLifecycleOwner) { user ->
            userData = user
            if(user!=null){
                if(user.basket.isNotEmpty()){
                    cartNotification?.visibility = View.VISIBLE
                    cartNotificationText?.text = user.basket.size.toString()
                }
                else{
                    cartNotification.visibility = View.GONE
                }
                loadingFinished()
            }

        }
    }

    private fun loadingFinished(){
        bottomNavBar.visibility = View.VISIBLE
        home_loader.visibility = View.GONE
        toolbar.visibility = View.VISIBLE
        cartNotificationContainer.visibility = View.VISIBLE
    }

    private fun setupAdRecyclerView() {
        val adAdapter = AdAdapter()
        adRecyclerView.apply {
            adapter = adAdapter
            layoutManager = LinearLayoutManager(
                this@HomeFragment.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }
    }

    private fun setupCategoryRecyclerView() {
        val categoryAdapter = CategoryAdapter()
        categoryAdapter.categoryClickListener = { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
            action.category = category
            findNavController().navigate(action)
        }
        categoryRecyclerView.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(
                this@HomeFragment.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }
}