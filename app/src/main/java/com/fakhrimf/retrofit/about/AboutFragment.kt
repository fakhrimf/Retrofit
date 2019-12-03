package com.fakhrimf.retrofit.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fakhrimf.retrofit.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fun intent(uri: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        ivGmail.setOnClickListener {
            intent("mailto:fakhrimf23@gmail.com")
        }

        ivInstagram.setOnClickListener {
            intent("https://www.instagram.com/fakhrimf23/")
        }

        ivLinked.setOnClickListener {
            intent("https://www.linkedin.com/in/fakhrimf23/")
        }
    }
}
