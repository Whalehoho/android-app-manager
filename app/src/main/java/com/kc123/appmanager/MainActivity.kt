package com.kc123.appmanager

import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kc123.appmanager.R
import androidx.navigation.fragment.NavHostFragment


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    true
                }
                R.id.folderFragment -> {
                    navController.popBackStack(R.id.folderFragment, false)
                    true
                }
                R.id.personalFragment -> {
                    navController.popBackStack(R.id.personalFragment, false)
                    true
                }
                else -> false
            }
        }
    }
}

