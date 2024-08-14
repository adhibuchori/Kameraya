package com.adhibuchori.kameraya

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adhibuchori.kameraya.databinding.ActivityMainBinding
import com.adhibuchori.kameraya.ui.auth.preference.UserPreferences
import com.adhibuchori.kameraya.ui.auth.preference.UserViewModel
import com.adhibuchori.kameraya.ui.auth.preference.dataStore
import com.adhibuchori.kameraya.utils.factory.UserViewModelFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userViewModel: UserViewModel by viewModels {
        val pref = UserPreferences.getInstance(application.dataStore)
        UserViewModelFactory(pref)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {
        applyThemeFromDataStore()
        applyLanguageFromDataStore()
    }

    private fun applyThemeFromDataStore() {
        userViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun applyLanguageFromDataStore() {
        userViewModel.getLanguageSetting().observe(this) { isLanguageChangeActive: Boolean ->
            val newLocale = if (isLanguageChangeActive) Locale("ja") else Locale.ENGLISH
            val localeList = LocaleListCompat.create(newLocale)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
}