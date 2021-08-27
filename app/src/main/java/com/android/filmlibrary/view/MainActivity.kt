package com.android.filmlibrary.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.sharedpref.SharedPref

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

        setupActionBarWithNavController(this, navController)

        (this.application as GlobalVariables).settings = SharedPref(this).loadSettings()
    }

    override fun onDestroy() {
        SharedPref(this).saveSettings((this.application as GlobalVariables).settings)
        super.onDestroy()
    }
}