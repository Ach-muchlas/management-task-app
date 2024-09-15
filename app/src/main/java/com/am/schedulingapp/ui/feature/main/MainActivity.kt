package com.am.schedulingapp.ui.feature.main

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.am.schedulingapp.R
import com.am.schedulingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu)
        val menu = popupMenu.menu
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController( menu, navController)
    }
}