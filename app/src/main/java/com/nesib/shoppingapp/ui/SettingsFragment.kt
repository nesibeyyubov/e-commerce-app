package com.nesib.shoppingapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.WelcomeActivity
import com.nesib.shoppingapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject
import kotlin.math.sign

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var userViewModel: UserViewModel

    @Inject
    lateinit var auth:FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (activity as MainActivity).userViewModel!!
        setupUi()
    }

    private fun setupUi() {
        if (userViewModel.isAuthenticated) {
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