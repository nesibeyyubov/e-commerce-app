package com.nesib.shoppingapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.adapters.OrderAdapter
import com.nesib.shoppingapp.model.Food
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_basket.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_orders.backButton
import java.sql.Array

class OrdersFragment : Fragment(R.layout.fragment_orders) {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userData: User
    private var orderAdapter = OrderAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (activity as MainActivity).userViewModel!!
        setupUi()
        setupOrdersRecyclerView()
        subscribeObservers()
    }

    private fun setupUi(){
        backButton.setOnClickListener { findNavController().popBackStack() }
        setupYourAccount.setOnClickListener { requireActivity().finish() }
    }

    private fun showWarningDialog(){
        val view = LayoutInflater.from(context).inflate(R.layout.login_warning,null,false)
        val dialog = AlertDialog.Builder(context).setView(view)
            .create()
        view.findViewById<Button>(R.id.notNowBtn).setOnClickListener{
            orderRecyclerView.visibility = View.GONE
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.signInBtn).setOnClickListener {
            dialog.dismiss()
            requireActivity().finish()
        }
        dialog.show()
    }


    private fun subscribeObservers() {
        if(!userViewModel.isAuthenticated) {
            showWarningDialog()
            noUserContainer.visibility = View.VISIBLE
            return
        }
        if(orderRecyclerView.visibility == View.GONE){
            orderRecyclerView.visibility = View.VISIBLE
        }
        var firstRender = true
        userViewModel = (activity as MainActivity).userViewModel!!
        userViewModel.getUserData(true).observe(viewLifecycleOwner) { user ->
            if(firstRender){
                orderProgressBar.visibility = View.VISIBLE
                firstRender = false
            }
            else if (user != null) {
                orderProgressBar.visibility = View.GONE
                this.userData = user
                val orders = user.orders.toMutableList()
                if(orders.size == 0) {
                    noFoodsOrdered.visibility = View.VISIBLE
                    orderRecyclerView.visibility = View.GONE
                }else{
                    val filteredOrders = flattenOrderList(orders)
                    orderAdapter.orderList = filteredOrders
                    orderAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun flattenOrderList(orders:List<Food>):ArrayList<Food>{
        val filteredOrders = mutableListOf<Food>()
        for (i in 0 until orders.size) {
            var isAlreadyAdded = false
            for (j in 0 until filteredOrders.size) {
                if (filteredOrders[j].image == orders[i].image) {
                    filteredOrders[j].count += orders[i].count
                    isAlreadyAdded = true
                    break
                }
            }
            if (!isAlreadyAdded) {
                filteredOrders.add(orders[i])
            }

        }
        return filteredOrders as ArrayList<Food>
    }

    private fun setupOrdersRecyclerView() {
        orderRecyclerView.apply {
            adapter = orderAdapter
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
        }
    }

}