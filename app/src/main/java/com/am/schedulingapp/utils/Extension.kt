package com.am.schedulingapp.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object Extension {
    fun Activity.goToActivity(
        targetActivity: Class<out Activity>,
        withFinish: Boolean = false,
        bundle: Bundle? = null
    ) {
        val intent = Intent(this, targetActivity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (withFinish) finish()
    }
}