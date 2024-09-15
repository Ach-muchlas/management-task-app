package com.am.schedulingapp.service.source

import androidx.lifecycle.liveData
import com.am.schedulingapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {
    fun signUp(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val userProfile = hashMapOf("name" to name)
                firebaseFirestore.collection("users").document(it.uid).set(userProfile).await()
                emit(Result.success(User(it.uid, it.email ?: "", name)))
            }
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Sign up failed"))
        }
    }


    fun signIn(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val document = firebaseFirestore.collection("users").document(it.uid).get().await()
                val name = document.getString("name") ?: ""
                emit(Result.success(User(it.uid, it.email ?: "", name)))
            }
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Sign in failed"))
        }
    }

    fun signInWithGoogle(idToken: String) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            emit(Result.success(authResult.user))
        } catch (exception: Exception) {
            emit(Result.error(null, exception.message ?: "Sign in with gmail failed"))
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}