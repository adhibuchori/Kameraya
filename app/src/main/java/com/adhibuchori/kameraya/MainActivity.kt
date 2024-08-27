package com.adhibuchori.kameraya

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adhibuchori.kameraya.databinding.ActivityMainBinding
import com.adhibuchori.kameraya.ui.main.profile.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val profileViewModel: ProfileViewModel by viewModel()

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        arrayOf()
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
        if (arePermissionGranted().not()) {
            requestPermissions()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val deniedPermission = permission.filterValues { !it }.keys
        if (deniedPermission.isNotEmpty()) {
            Toast.makeText(this, "Permission denied: $deniedPermission", Toast.LENGTH_SHORT)
        }
    }

    private fun arePermissionGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(requiredPermissions)
    }

    private fun applyThemeFromDataStore() {
        profileViewModel.theme.observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive == true) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun applyLanguageFromDataStore() {
        profileViewModel.language.observe(this) { isLanguageChangeActive ->
            val newLocale = if (isLanguageChangeActive == true) Locale("ja") else Locale.ENGLISH
            val localeList = LocaleListCompat.create(newLocale)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
}