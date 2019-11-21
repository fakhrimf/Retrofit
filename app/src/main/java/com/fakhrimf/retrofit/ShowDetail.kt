package com.fakhrimf.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fakhrimf.retrofit.detail.ShowDetailFragment
import com.fakhrimf.retrofit.main.MainVM
import com.fakhrimf.retrofit.model.ShowModel

class ShowDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)
        val main = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(MainVM::class.java)
        val model = intent.getParcelableExtra<ShowModel>(main.getParcelKey())
        val transaction = supportFragmentManager.beginTransaction()
        val showFragment = ShowDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(main.getParcelKey(), model)
        showFragment.arguments = bundle
        transaction.replace(R.id.fragmentShowDetail, showFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        this.finish()
    }
}
