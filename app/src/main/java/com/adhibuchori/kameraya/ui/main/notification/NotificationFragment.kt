package com.adhibuchori.kameraya.ui.main.notification

import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentNotificationBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment

class
NotificationFragment :
    BaseFragment<FragmentNotificationBinding, ViewModel>(FragmentNotificationBinding::inflate) {
    override fun initViews() {
        setupToolbar()
    }

    private fun setupToolbar() {
        with(binding.notificationPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_notification)
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }
}