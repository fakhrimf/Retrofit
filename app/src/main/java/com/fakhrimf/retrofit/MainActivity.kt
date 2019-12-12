package com.fakhrimf.retrofit

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f
        bottomNavMain.setupWithNavController(findNavController(R.id.homeFragment))
    }

    private val mHandler = Handler()
    private var back = false
    override fun onBackPressed() {
        if (back) super.onBackPressed()
        else {
            Toast.makeText(applicationContext, getString(R.string.back), Toast.LENGTH_LONG).show()
            back = true
        }
        mHandler.postDelayed({
            back = true
        }, 2000)
    }
}
