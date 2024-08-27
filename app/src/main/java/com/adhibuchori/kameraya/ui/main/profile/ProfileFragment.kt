package com.adhibuchori.kameraya.ui.main.profile

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentProfileBinding
import com.adhibuchori.kameraya.databinding.ItemDialogBoxBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.createAlertDialog
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.setDialogMargins
import com.adhibuchori.kameraya.utils.extension.setDialogSize
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ViewModel>(FragmentProfileBinding::inflate) {

    private val profileViewModel: ProfileViewModel by viewModel()

    private val thumbColorStateList by lazy {
        ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            context?.let {
                intArrayOf(
                    ContextCompat.getColor(it, R.color.color_switch_thumb_active),
                    ContextCompat.getColor(it, R.color.color_switch_thumb_inactive)
                )
            }
        )
    }

    override fun initViews() {
        observeUserData()
        setupToolbar()
        setupNavigation()
        setupSwitchListener()
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupNavigation() {
        binding.btnProfilePageLogout.setOnClickListener {
            buttonClickEvent(BUTTON_LOGOUT)
            setupAlertDialogListener()
        }
    }

    private fun setupAlertDialogListener() {
        val dialogBinding = ItemDialogBoxBinding.inflate(layoutInflater)
        context?.createAlertDialog(dialogBinding)?.let { dialog ->
            dialog.show()
            dialogBinding.setDialogMargins()
            dialog.setDialogSize()
            setupDialogUI(dialogBinding, dialog)
        }
    }

    private fun setupDialogUI(dialogBinding: ItemDialogBoxBinding, dialog: AlertDialog) {
        with(dialogBinding) {
            tvItemDialogBoxQuestion.text = getString(R.string.dialog_logout_question)
            ivItemDialogBoxImage.setImageResource(R.drawable.iv_dialog_logout)
            btnItemDialogBoxNegative.text = getString(R.string.btn_cancel)
            btnItemDialogBoxPositive.text = getString(R.string.btn_logout)

            ivItemDialogCloseIcon.setOnClickListener {
                buttonClickEvent(CLOSE_ICON)
                dialog.dismiss()
            }

            btnItemDialogBoxNegative.setOnClickListener {
                buttonClickEvent(BUTTON_CANCEL_LOGOUT)
                dialog.dismiss()
            }

            btnItemDialogBoxPositive.setOnClickListener {
                buttonClickEvent(BUTTON_PROCEED_LOGOUT)
                setupLogoutObserver()
                dialog.dismiss()
            }
        }
    }


    private fun observeUserData() {
        binding.run {
            profileViewModel.run {
                userName.observe(viewLifecycleOwner) { userName ->
                    tvProfilePageUsernameData.text = userName
                }
                email.observe(viewLifecycleOwner) { email ->
                    tvProfilePageEmailData.text = email
                }
                userImage.observe(viewLifecycleOwner) { userImage ->
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

            profileViewModel.logout.observe(viewLifecycleOwner) { result ->
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
                        Snackbar.make(
                            binding.root,
                            "Failed to Logout: ${result.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.HttpError -> {
                        pbProfilePageProgressBar.gone()
                        Snackbar.make(
                            binding.root,
                            "HTTP Error: ${result.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.NetworkError -> {
                        pbProfilePageProgressBar.gone()
                        Snackbar.make(
                            binding.root,
                            "Network Error",
                            Snackbar.LENGTH_SHORT
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
        sProfilePageTheme.let {
            it.thumbTintList = thumbColorStateList
            profileViewModel.theme.observe(viewLifecycleOwner) { isDarkModeActive ->
                it.isChecked = isDarkModeActive ?: false
            }
            it.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                profileViewModel.saveThemeSetting(isChecked)
            }
        }
    }

    private fun setupLanguageSetting() = with(binding) {
        sProfilePageLanguage.let {
            it.thumbTintList = thumbColorStateList
            profileViewModel.language.observe(viewLifecycleOwner) { isLanguageChangeActive ->
                it.isChecked = isLanguageChangeActive ?: false
            }
            it.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                profileViewModel.saveLanguageSetting(isChecked)
            }
        }
    }

    private fun buttonClickEvent(buttonName: String) {
        profileViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private fun setupToolbar() {
        with(binding.profilePageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_profile)
            ivToolbarArrowBackIcon.gone()
        }
    }

    private companion object {
        const val SCREEN_NAME = "Notification"
        const val EVENT_CATEGORY = "Profile Fragment"

        const val BUTTON_LOGOUT = "Logout Button Logout"
        const val BUTTON_PROCEED_LOGOUT = "Button Proceed Logout"
        const val BUTTON_CANCEL_LOGOUT = "Button Cancel Logout"
        const val CLOSE_ICON = "Close Icon"
    }
}