package com.nesib.shoppingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nesib.shoppingapp.model.User
import com.nesib.shoppingapp.repository.UserRepository

class UserViewModel:ViewModel() {
    private val _isAuthenticated: MutableLiveData<Boolean> = MutableLiveData()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    init {
        _isAuthenticated.value = UserRepository.getUser() != null
    }

    fun getUserData(forceDataFetch:Boolean = false):LiveData<User>{
        UserRepository.getUserData(forceDataFetch)
        return UserRepository.userData
    }
}