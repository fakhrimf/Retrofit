package com.fakhrimf.retrofit.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fakhrimf.retrofit.MainActivity
import com.fakhrimf.retrofit.R

class SplashFragment : Fragment() {
    private val mHandler = Handler()
    private val runnable = Runnable {
        startActivity(Intent(activity,MainActivity::class.java))
        activity!!.finish()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mHandler.postDelayed(runnable, 1000)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(runnable)
        super.onDestroy()
    }
}
