package com.nesib.shoppingapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nesib.shoppingapp.model.User

object UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _userData: MutableLiveData<User> = MutableLiveData()
    val userData: LiveData<User>
        get() = _userData

    fun getUserData(forceDataFetch: Boolean = false) {
        if (!forceDataFetch) {
            if (_userData.value != null || auth.currentUser == null) return
        }
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result?.toObject(User::class.java)
                    _userData.value = user
                }
            }
    }

    fun getUser(): FirebaseUser? = auth.currentUser

    fun signIn(email:String,password:String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email,password)
    }

    fun signUp(email:String,password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email,password)
    }
}