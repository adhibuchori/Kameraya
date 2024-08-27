package com.adhibuchori.kameraya.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentSplashBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment :
    BaseFragment<FragmentSplashBinding, ViewModel>(FragmentSplashBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()

    override fun initViews() {
        setupSplashScreen()
    }

    private fun setupSplashScreen() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()

            val navController = view?.let { Navigation.findNavController(it) }

            authViewModel.run {
                onBoardingShown.observe(viewLifecycleOwner) { isOnBoardingShown ->
                    loginStatus.observe(viewLifecycleOwner) { loginStatus ->
                        if (loginStatus?.isNotEmpty() == true) {
                            navController?.navigate(
                                R.id.action_splashFragment_to_mainFragment,
                                null,
                                navOptions
                            )
                        } else {
                            if (isOnBoardingShown == true) {
                                navController?.navigate(
                                    R.id.action_splashFragment_to_loginFragment,
                                    null,
                                    navOptions
                                )
                            } else {
                                navController?.navigate(
                                    R.id.action_splashFragment_to_onBoardingFragment,
                                    null,
                                    navOptions
                                )
                                saveOnBoardingShown(true)
                            }
                        }
                    }
                }
            }
        }
    }
}