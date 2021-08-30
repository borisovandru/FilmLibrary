package com.android.filmlibrary.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.android.filmlibrary.Constant.COLOR_PURPLE
import com.android.filmlibrary.Constant.COLOR_RED
import com.android.filmlibrary.Constant.MAIL
import com.android.filmlibrary.Constant.NAVIGATE_TO_MESSAGE
import com.android.filmlibrary.Constant.NEW_MAIL
import com.android.filmlibrary.Constant.NEW_MESSAGE
import com.android.filmlibrary.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.sharedpref.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.getIntExtra(NEW_MESSAGE, 0) == 1){
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

        settings = SharedPref(this).loadSettings()

        setContentView(R.layout.main_activity)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

        setupActionBarWithNavController(this, navController)

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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
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