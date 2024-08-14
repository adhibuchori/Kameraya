package com.adhibuchori.kameraya.ui.main

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.adhibuchori.kameraya.databinding.FragmentMainBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding, ViewModel>(FragmentMainBinding::inflate) {
    override fun initViews() {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navController =
            childFragmentManager.findFragmentById(binding.fcvMainPageChildContainer.id)
                ?.findNavController()
        val bottomNavigationView = binding.bnvMainPageNavigation

        navController?.let {
            bottomNavigationView.setupWithNavController(it)
        }
    }
}