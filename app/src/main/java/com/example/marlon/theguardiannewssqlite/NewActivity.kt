package com.example.marlon.theguardiannewssqlite

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.marlon.theguardiannews.R

class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myTheme = intent.extras.getString(KEY_THEME)
        if (myTheme == DAY) {
            setTheme(R.style.AppTheme)
        } else {
            setTheme(R.style.Theme_AppCompat_NoActionBar)
        }
        setContentView(R.layout.activity_new)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        //Enable action bar
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        // Open a new fragment
        val newFragment = NewFragment()
        // Send data to the fragment
        newFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().replace(R.id.container, newFragment).commit()
    }
}
