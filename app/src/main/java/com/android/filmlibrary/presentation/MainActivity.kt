package com.android.filmlibrary.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.filmlibrary.utils.Constant.COLOR_PURPLE
import com.android.filmlibrary.utils.Constant.COLOR_RED
import com.android.filmlibrary.utils.Constant.MAIL
import com.android.filmlibrary.utils.Constant.NAVIGATE_TO_MESSAGE
import com.android.filmlibrary.utils.Constant.NEW_MAIL
import com.android.filmlibrary.utils.Constant.NEW_MESSAGE
import com.android.filmlibrary.utils.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.data.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.getIntExtra(NEW_MESSAGE, 0) == 1) {
                context?.let {
                    floatingActionButton.supportBackgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(it, COLOR_RED))
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(it, NEW_MAIL))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = SharedPref(this).readSettings()

        setContentView(R.layout.main_activity)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph)


        setupActionBarWithNavController(navController)

        floatingActionButton = findViewById(R.id.fab)
        floatingActionButton.setOnClickListener {
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, MAIL))
            floatingActionButton.supportBackgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, COLOR_PURPLE))
            navHostFragment.navController.navigate(
                NAVIGATE_TO_MESSAGE
            )
        }
    }

    override fun onDestroy() {
        SharedPref(this).saveSettings(settings)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter(NEW_MESSAGE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}