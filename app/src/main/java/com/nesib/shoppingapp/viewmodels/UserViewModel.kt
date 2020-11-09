package com.nesib.shoppingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.repository.UserRepository

class UserViewModel : ViewModel() {
    private var _isAuthenticated: Boolean = false
    val isAuthenticated: Boolean
        get() = _isAuthenticated

    init {
        _isAuthenticated = UserRepository.getUser() != null
    }

    fun getUserData(forceDataFetch: Boolean = false): LiveData<User> {
        UserRepository.getUserData(forceDataFetch)
        return UserRepository.userData
    }

    fun signIn(email:String,password:String): Task<AuthResult> {
        return UserRepository.signIn(email,password)
    }

    fun signUp(email:String,password:String): Task<AuthResult> {
        return UserRepository.signUp(email,password)
    }
}