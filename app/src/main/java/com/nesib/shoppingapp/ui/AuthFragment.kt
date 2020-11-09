package com.nesib.shoppingapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nesib.shoppingapp.MainActivity
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.WelcomeActivity
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth), View.OnClickListener {
    private var isRegistering = false

    @Inject
    lateinit var db: FirebaseFirestore

    private lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (activity as WelcomeActivity).userViewModel!!
        initialize()
    }

    private fun initialize() {
        authButton.setOnClickListener(this)
        toggleLoginBtn.setOnClickListener(this)
        toggleSignupBtn.setOnClickListener(this)
        exitAuthButton.setOnClickListener(this)
    }

    private fun toggleButtonStyle(clickedButton: TextView, unClickedButton: TextView) {
        clickedButton.background =
            ResourcesCompat.getDrawable(resources, R.drawable.auth_toggle_active, null)
        clickedButton.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        unClickedButton.background = null
        unClickedButton.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorTextLight,
                null
            )
        )
        if (errorText.visibility == View.VISIBLE) {
            errorText.visibility = View.GONE
        }
    }

    private fun toggleProgressBar(loading: Boolean) {
        authButtonProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        authButtonText.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun openMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.toggleLoginBtn -> {
                toggleButtonStyle(toggleLoginBtn, toggleSignupBtn)
                if (nameInputContainer.isVisible) {
                    nameInputContainer.visibility = View.GONE
                    authButtonText.text = "Login"
                    emailInput.text.clear()
                    passwordInput.text.clear()
                }
                isRegistering = false
            }
            R.id.toggleSignupBtn -> {
                toggleButtonStyle(toggleSignupBtn, toggleLoginBtn)
                if (!nameInputContainer.isVisible) {
                    nameInputContainer.visibility = View.VISIBLE
                    authButtonText.text = "Signup"
                    emailInput.text.clear()
                    passwordInput.text.clear()
                }
                isRegistering = true
            }
            R.id.authButton -> {
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val name = nameInput.text.toString().trim()
                if (email.isBlank() || password.isBlank()) return
                if (!isRegistering) {
                    toggleProgressBar(true)
                    authButton.isEnabled = false
                    userViewModel.signIn(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                errorText.visibility = View.GONE
                                openMainActivity()
                            } else {
                                errorText.visibility = View.VISIBLE
                                toggleProgressBar(false)
                                authButton.isEnabled = true
                            }
                        }
                } else {
                    toggleProgressBar(true)
                    authButton.isEnabled = false
                    userViewModel.signUp(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                errorText.visibility = View.GONE
                                val user = User(it.result!!.user!!.uid, name, email)
                                db.collection("users")
                                    .document(user.uid!!)
                                    .set(user)
                                    .addOnCompleteListener {
                                        openMainActivity()
                                    }
                            } else {
                                authButton.isEnabled = true
                                toggleProgressBar(false)
                                errorText.text = "Something went wrong,please try again"
                            }
                        }
                }
            }
            R.id.exitAuthButton -> {
                findNavController().popBackStack()
            }
        }
    }
}