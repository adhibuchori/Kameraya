package com.adhibuchori.kameraya.ui.main.profile

import android.app.AlertDialog
import android.util.Log
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import com.adhibuchori.kameraya.ui.auth.preference.dataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentProfileBinding
import com.adhibuchori.kameraya.databinding.ItemDialogBoxBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.ui.auth.preference.UserPreferences
import com.adhibuchori.kameraya.ui.auth.preference.UserViewModel
import com.adhibuchori.kameraya.ui.auth.preference.dataStore
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.factory.UserViewModelFactory
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ViewModel>(FragmentProfileBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModels {
        val pref = UserPreferences.getInstance(requireActivity().application.dataStore)
        UserViewModelFactory(pref)
    }

    override fun initViews() {
        observeUserData()
        setupToolbar()
        setupNavigation()
        setupSwitchListener()
    }

    private fun setupNavigation() {
        binding.btnProfilePageLogout.setOnClickListener { setupAlertDialogListener() }
    }

    private fun setupAlertDialogListener() {
        val dialogBinding = ItemDialogBoxBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        val layoutParams = dialogBinding.root.layoutParams as ViewGroup.MarginLayoutParams
        val horizontalMarginInDp = 45
        val horizontalMarginInPx = (horizontalMarginInDp * resources.displayMetrics.density).toInt()
        layoutParams.marginStart = horizontalMarginInPx
        layoutParams.marginEnd = horizontalMarginInPx
        dialogBinding.root.layoutParams = layoutParams

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        with(dialogBinding) {
            tvItemDialogBoxQuestion.text = getString(R.string.dialog_logout_question)
            ivItemDialogBoxImage.setImageResource(R.drawable.iv_dialog_logout)
            ivItemDialogCloseIcon.setOnClickListener { dialog.dismiss() }

            btnItemDialogBoxNegative.text = getString(R.string.btn_cancel)
            btnItemDialogBoxPositive.text = getString(R.string.btn_logout)

            btnItemDialogBoxNegative.setOnClickListener {
                dialog.dismiss()
            }
            btnItemDialogBoxPositive.setOnClickListener {
                setupLogoutObserver()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun observeUserData() {
        binding.run {
            authViewModel.run {
                userName.observe(viewLifecycleOwner) { userName ->
                    tvProfilePageUsernameData.text = userName
                }
                email.observe(viewLifecycleOwner) { email ->
                    tvProfilePageEmailData.text = email
                }
                userImage.observe(viewLifecycleOwner) { userImage ->
                    Log.d("userImage at HomeFragment: ", userImage.toString())
                    Glide.with(this@ProfileFragment)
                        .load(userImage)
                        .into(ivProfilePageProfilePicture)
                }
            }
        }
    }

    private fun setupLogoutObserver() {
        with(binding) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graphs, true)
                .build()

            authViewModel.logout.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        pbProfilePageProgressBar.visible()
                    }

                    is Resource.Success -> {
                        pbProfilePageProgressBar.gone()
                        Navigation.findNavController(requireActivity(), R.id.fcv_container)
                            .navigate(
                                R.id.action_mainFragment_to_loginFragment,
                                null,
                                navOptions
                            )
                    }

                    is Resource.Error -> {
                        pbProfilePageProgressBar.gone()
                        Toast.makeText(
                            requireActivity(),
                            "Failed to Logout",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.HttpError -> {
                        pbProfilePageProgressBar.gone()
                        Toast.makeText(
                            requireActivity(),
                            "HTTP Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.NetworkError -> {
                        pbProfilePageProgressBar.gone()
                        Toast.makeText(
                            requireActivity(),
                            "Network Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupSwitchListener() {
        setupThemeSetting()
        setupLanguageSetting()
    }

    private fun setupThemeSetting() = with(binding) {
        userViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            sProfilePageTheme.isChecked = isDarkModeActive
        }

        sProfilePageTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            userViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupLanguageSetting() = with(binding) {
        userViewModel.getLanguageSetting()
            .observe(viewLifecycleOwner) { isLanguageChangeActive ->
                sProfilePageLanguage.isChecked = isLanguageChangeActive
            }

        sProfilePageLanguage.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            userViewModel.saveLanguageSetting(isChecked)
        }
    }

    private fun setupToolbar() {
        with(binding.profilePageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_profile)
            ivToolbarArrowBackIcon.gone()
        }
    }
}