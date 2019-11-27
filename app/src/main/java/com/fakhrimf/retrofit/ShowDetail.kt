package com.fakhrimf.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fakhrimf.retrofit.detail.ShowDetailFragment
import com.fakhrimf.retrofit.model.ShowModel
import com.fakhrimf.retrofit.utils.VALUE_KEY

class ShowDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)
        val model = intent.getParcelableExtra<ShowModel>(VALUE_KEY)
        val transaction = supportFragmentManager.beginTransaction()
        val showFragment = ShowDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(VALUE_KEY, model)
        showFragment.arguments = bundle
        transaction.replace(R.id.fragmentShowDetail, showFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        this.finish()
    }
}
