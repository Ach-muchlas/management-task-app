package com.am.schedulingapp.service.di

import com.am.schedulingapp.service.source.AuthRepository
import com.am.schedulingapp.ui.feature.auth.AuthViewModel
import com.am.schedulingapp.ui.feature.task.TaskRepository
import com.am.schedulingapp.ui.feature.task.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModule {
    val apiModule = module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        single { FirebaseStorage.getInstance() }
        single { AuthRepository(get(), get()) }
        single { TaskRepository(get(), get()) }
    }

    val uiModule
        get() = module {
            viewModel { AuthViewModel(get()) }
            viewModel { TaskViewModel(get()) }
        }
}