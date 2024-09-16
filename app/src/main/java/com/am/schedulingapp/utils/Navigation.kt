package com.am.schedulingapp.utils

import android.os.Bundle
import androidx.navigation.NavController
import com.am.schedulingapp.R

object Navigation {
    fun navigateToFragment(
        destination: Destination,
        navController: NavController,
        args: Bundle? = null
    ) {
        navController.let {
            when (destination) {
                Destination.TASK_TO_ADD_OR_UPDATE_TASK -> it.navigate(
                    R.id.action_navigation_task_to_addOrUpdateTaskFragment,
                    args
                )
            }
        }
    }
}

enum class Destination {
    TASK_TO_ADD_OR_UPDATE_TASK
}