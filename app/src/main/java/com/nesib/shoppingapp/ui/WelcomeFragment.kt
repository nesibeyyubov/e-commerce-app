package com.nesib.shoppingapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.WelcomeActivity
import com.nesib.shoppingapp.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    private var flag = false
    private lateinit var userViewModel: UserViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (activity as WelcomeActivity).userViewModel!!
        if (userViewModel.isAuthenticated) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
        getStartedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
        }
        skipButton.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
        }
        val directToAuth = requireActivity().intent.getBooleanExtra("directToAuth", false)
        if (directToAuth && !flag) {
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
            flag = true
        }

    }


}