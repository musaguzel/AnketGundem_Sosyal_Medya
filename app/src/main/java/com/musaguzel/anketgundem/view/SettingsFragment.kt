package com.musaguzel.anketgundem.view

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.musaguzel.anketgundem.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == "darkmode") {
                    val checkDarkMode = sharedPreferences.getBoolean(key, false)
                    if (checkDarkMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }

            }

    }


}