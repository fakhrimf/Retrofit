package com.fakhrimf.retrofit

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fakhrimf.retrofit.utils.SectionAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionAdapter = SectionAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionAdapter
        tabLayout.setupWithViewPager(viewPager)
        supportActionBar?.elevation = 0f
        tabLayout.background = ColorDrawable(1)
    }

    private val mHandler = Handler()
    private var back = false
    override fun onBackPressed() {
        if (back) super.onBackPressed()
        else {
            Toast.makeText(applicationContext,getString(R.string.back), Toast.LENGTH_LONG).show()
            back = true
        }
        mHandler.postDelayed({
            back = true
        },2000)
    }
}
