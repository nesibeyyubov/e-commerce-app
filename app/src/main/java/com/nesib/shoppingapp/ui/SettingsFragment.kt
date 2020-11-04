package com.nesib.shoppingapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.WelcomeActivity
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlin.math.sign

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var userViewModel: UserViewModel
    private val auth = FirebaseAuth.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setupUi()
    }

    private fun setupUi() {
        if (userViewModel.isAuthenticated.value!!) {
            val user = userViewModel.getUserData().value!!
            accountContainer.visibility = View.VISIBLE
            userName.text = user.name
            userMail.text = user.email

            logoutButton.setOnClickListener{
                auth.signOut()
                val intent = Intent(activity,WelcomeActivity::class.java)
                intent.putExtra("directToAuth",true)
                startActivity(intent)
                requireActivity().finish()
            }
        } else {
            accountContainer.visibility = View.GONE
            noAccountContainer.visibility = View.VISIBLE
            loginButton.setOnClickListener{
                requireActivity().finish()
            }
        }
    }

}