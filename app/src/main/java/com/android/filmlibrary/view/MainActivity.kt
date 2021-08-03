package com.android.filmlibrary.view

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.MainActivityBinding
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    private lateinit var listFragment: List<Fragment>
    private lateinit var binder: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = MainActivityBinding.inflate(layoutInflater)
        setContentView(binder.root)

        listFragment = listOf(HomeFragment(), FavoritesFragment(), RatingsFragment())
        if (savedInstanceState == null) {
            showFragment(listFragment[0])
            setColorBackgroundButton(binder.buttonHome)
        }
        initView()

    }

    private fun initView() {
        setSupportActionBar(binder.toolbar)
        clickNavigateButtons()
    }

    private fun clickNavigateButtons() {
        binder.buttonHome.setOnClickListener {
            showFragment(listFragment[0])
            setColorBackgroundButton(binder.buttonHome)
        }
        binder.buttonFavorite.setOnClickListener {
            showFragment(listFragment[1])
            setColorBackgroundButton(binder.buttonFavorite)
        }
        binder.buttonRatings.setOnClickListener {
            showFragment(listFragment[2])
            setColorBackgroundButton(binder.buttonRatings)
        }


    }

    private fun setColorBackgroundButton(button: MaterialButton) {
        binder.buttonHome.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        binder.buttonFavorite.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)
        binder.buttonRatings.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)
        button.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.buttons_background_color)
    }


    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val search = menu?.findItem(R.id.action_search)
        (search?.actionView as SearchView).also {
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return true
                }
            })
        }
        return true
    }


}

