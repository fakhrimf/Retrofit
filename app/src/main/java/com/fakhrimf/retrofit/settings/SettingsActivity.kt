package com.fakhrimf.retrofit.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.fakhrimf.retrofit.R
import com.fakhrimf.retrofit.utils.KEY_DAILY
import com.fakhrimf.retrofit.utils.KEY_RELEASE
import com.fakhrimf.retrofit.utils.notification.ReminderReceiver

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> true
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        private lateinit var showDaily: SwitchPreference
        private lateinit var showRelease: SwitchPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            init()
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        private fun init() {
            showDaily = findPreference<SwitchPreference>(KEY_DAILY) as SwitchPreference
            showRelease = findPreference<SwitchPreference>(KEY_RELEASE) as SwitchPreference
            val receiver = ReminderReceiver()
            showDaily.isChecked = receiver.isNotificationActivated(KEY_DAILY, context)
            showRelease.isChecked = receiver.isNotificationActivated(KEY_RELEASE, context)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            val receiver = ReminderReceiver()
            when (key) {
                KEY_DAILY -> {
                    showDaily.isChecked = sharedPreferences?.getBoolean(KEY_DAILY, false) ?: false
                    if (showDaily.isChecked) {
                        receiver.activateNotification(requireContext(), true)
                    } else {
                        receiver.activateNotification(requireContext(), false)
                    }
                }
                KEY_RELEASE -> {
                    showRelease.isChecked = sharedPreferences?.getBoolean(KEY_RELEASE, false)
                            ?: false
                    if (showRelease.isChecked) {
                        receiver.activateReleaseNotification(requireContext(), true)
                    } else {
                        receiver.activateReleaseNotification(requireContext(), false)
                    }
                }
            }
        }
    }
}