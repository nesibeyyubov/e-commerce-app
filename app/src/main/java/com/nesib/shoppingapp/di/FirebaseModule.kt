package com.nesib.shoppingapp.di
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object FirebaseModule {
    @Provides
    fun provideAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideDatabase() = FirebaseFirestore.getInstance()
}