package com.adhibuchori.kameraya.ui.auth.register

import android.app.AlertDialog
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentRegisterBinding
import com.adhibuchori.kameraya.databinding.ItemDialogBoxBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.configureWithSpannable
import com.adhibuchori.kameraya.utils.extension.createAlertDialog
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.setBoldStyleSpan
import com.adhibuchori.kameraya.utils.extension.setDialogMargins
import com.adhibuchori.kameraya.utils.extension.setDialogSize
import com.adhibuchori.kameraya.utils.extension.updateTextAppearance
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.getImageUri
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, ViewModel>(FragmentRegisterBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()
    private var currentImageUri: Uri? = null

    override fun initViews() {
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        authViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupNavigation() {
        with(binding) {
            ivRegisterPageArrowBackIcon.setOnClickListener {
                buttonClickEvent(ARROW_BACK_ICON)
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            cvRegisterPageBackgroundAddPhotoIcon.setOnClickListener {
                buttonClickEvent(BUTTON_ADD_PROFILE_PICTURE)
                setupAlertDialogListener()
            }
        }
        setupLoginNavigation()
        setupRegisterListener()
    }

    private fun setupRegisterListener() {
        with(binding) {
            btnRegisterPageRegister.setOnClickListener {
                buttonClickEvent(BUTTON_REGISTER)

                val userImageFile = currentImageUri?.let { uriToFile(it) }
                val userName = tietRegisterPageUsername.text.toString()
                val email = tietRegisterPageEmail.text.toString()
                val password = tietRegisterPagePassword.text.toString()

                if (userImageFile != null) {
                    when {
                        userName.isEmpty() -> {
                            tilRegisterPageUsername.error =
                                getString(R.string.error_username_required)
                        }

                        email.isEmpty() -> {
                            tilRegisterPageEmail.error = getString(R.string.error_email_required)
                        }

                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            tilRegisterPageEmail.error = getString(R.string.error_invalid_email)
                        }

                        password.isEmpty() -> {
                            tilRegisterPagePassword.error =
                                getString(R.string.error_password_required)
                        }

                        password.length < 8 -> {
                            tilRegisterPagePassword.error =
                                getString(R.string.error_password_length)
                        }

                        else -> {
                            apply {
                                tietRegisterPageUsername.onEditorAction(EditorInfo.IME_ACTION_DONE)
                                tietRegisterPageEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
                                tietRegisterPagePassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            }
                            authViewModel.register(email, password, userName, userImageFile)
                            setupRegisterObserver()
                        }
                    }
                }
            }
        }
    }

    private fun setupRegisterObserver() {
        with(binding) {
            authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> pbRegisterPageProgressBar.visible()

                    is Resource.Success -> {
                        pbRegisterPageProgressBar.gone()
                        view?.let { view ->
                            Navigation.findNavController(view).navigate(
                                R.id.action_registerFragment_to_mainFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.nav_graphs, true).build()
                            )
                        }
                    }

                    is Resource.HttpError -> {
                        binding.pbRegisterPageProgressBar.gone()
                        Snackbar.make(
                            binding.root,
                            "HTTP Error: ${result.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.NetworkError -> {
                        binding.pbRegisterPageProgressBar.gone()
                        Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        pbRegisterPageProgressBar.gone()
                        Snackbar.make(root, result.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> Log.e("RegisterFragment", "Unexpected result type: $result")
                }
            }
        }
    }

    private fun setupLoginNavigation() {
        val loginNow = getString(R.string.login_now)
        val formattedString = getString(R.string.no_account, loginNow)
        val spannableStringBuilder = SpannableStringBuilder(formattedString)

        val termsStart = formattedString.indexOf(loginNow)
        val termsEnd = termsStart + loginNow.length

        setLoginClickableSpan(spannableStringBuilder, termsStart, termsEnd)
        spannableStringBuilder.setBoldStyleSpan(termsStart, termsEnd)
        binding.tvRegisterPageHaveAccount.configureWithSpannable(spannableStringBuilder)
    }

    private fun setLoginClickableSpan(
        spannableStringBuilder: SpannableStringBuilder,
        termsStart: Int,
        termsEnd: Int,
    ) {
        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                buttonClickEvent(LOGIN_NAVIGATION)
                navigateToLogin(widget)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                context?.let { ds.updateTextAppearance(it) }
            }
        }

        spannableStringBuilder.setSpan(
            termsClickable,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun navigateToLogin(widget: View) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graphs, true)
            .build()

        val navController = Navigation.findNavController(widget)
        navController.navigate(
            R.id.action_registerFragment_to_loginFragment,
            null,
            navOptions
        )
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
            tvItemDialogBoxQuestion.text = getString(R.string.dialog_select_profile_question)
            ivItemDialogBoxImage.setImageResource(R.drawable.iv_dialog_insert_profile_image)
            btnItemDialogBoxNegative.text = getString(R.string.btn_camera)
            btnItemDialogBoxPositive.text = getString(R.string.btn_gallery)

            ivItemDialogCloseIcon.setOnClickListener {
                buttonClickEvent(CLOSE_ICON)
                dialog.dismiss()
            }
            btnItemDialogBoxNegative.setOnClickListener {
                buttonClickEvent(BUTTON_CAMERA)
                startCamera()
                dialog.dismiss()
            }
            btnItemDialogBoxPositive.setOnClickListener {
                buttonClickEvent(BUTTON_GALLERY)
                startGallery()
                dialog.dismiss()
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.civRegisterPageProfilePicturePlaceholder.setImageURI(currentImageUri)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = context?.contentResolver
        val myFile = createTempFile(context?.cacheDir, "temp_image", ".jpg")
        val inputStream = contentResolver?.openInputStream(uri) ?: return myFile
        val outputStream = myFile.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return myFile
    }

    @Suppress("SameParameterValue")
    private fun createTempFile(directory: File?, prefix: String, suffix: String): File {
        return File.createTempFile(prefix, suffix, directory).apply {
            createNewFile()
            deleteOnExit()
        }
    }

    private fun buttonClickEvent(buttonName: String) {
        authViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val SCREEN_NAME = "Register"
        const val EVENT_CATEGORY = "Register Fragment"

        const val BUTTON_REGISTER = "Button Register"
        const val BUTTON_CAMERA = "Button Camera"
        const val BUTTON_GALLERY = "Button Gallery"
        const val BUTTON_ADD_PROFILE_PICTURE = "Button Add Profile Picture"
        const val ARROW_BACK_ICON = "Arrow Back Icon"
        const val CLOSE_ICON = "Close Icon"
        const val LOGIN_NAVIGATION = "Login Navigation"
    }
}