package com.am.schedulingapp.ui.feature.auth

import androidx.lifecycle.ViewModel
import com.am.schedulingapp.service.source.AuthRepository

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    fun signUpUser(name: String, email: String, password: String) =
        repository.signUp(name, email, password)
    fun signInUser(email: String, password: String) = repository.signIn(email, password)
    fun signInWithGoogle(idToken: String) = repository.signInWithGoogle(idToken)
    fun signOut() = repository.signOut()
}