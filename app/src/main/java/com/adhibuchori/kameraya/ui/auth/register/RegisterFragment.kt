package com.adhibuchori.kameraya.ui.auth.register

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentRegisterBinding
import com.adhibuchori.kameraya.databinding.ItemDialogBoxBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.getImageUri
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, ViewModel>(FragmentRegisterBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()
    private var currentImageUri: Uri? = null

    override fun initViews() {
        setupNavigation()
    }

    private fun setupNavigation() {
        with(binding) {
            ivRegisterPageArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            cvRegisterPageBackgroundAddPhotoIcon.setOnClickListener { setupAlertDialogListener() }
        }
        setupLoginNavigation()
        setupRegisterListener()
    }

    private fun setupRegisterListener() {
        with(binding) {
            btnRegisterPageRegister.setOnClickListener {
                val userImageFile = currentImageUri?.let { uriToFile(it) }
                val userName = tietRegisterPageUsername.text.toString()
                val email = tietRegisterPageEmail.text.toString()
                val password = tietRegisterPagePassword.text.toString()

                if (userImageFile != null) {
                    when {
                        userName.isEmpty() -> {
                            tilRegisterPageEmail.error = "Please enter username first"
                        }

                        email.isEmpty() -> {
                            tilRegisterPageEmail.error = "Please enter email first"
                        }

                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            tilRegisterPageEmail.error = "Invalid email format"
                        }

                        password.isEmpty() -> {
                            tilRegisterPagePassword.error = "Please enter password first"
                        }

                        password.length < 8 -> {
                            tilRegisterPagePassword.error =
                                "Password must be at least 8 characters long"
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
                    is Resource.Loading -> {
                        pbRegisterPageProgressBar.visible()
                    }

                    is Resource.Success -> {
                        pbRegisterPageProgressBar.gone()
                        Navigation.findNavController(requireView()).navigate(
                            R.id.action_registerFragment_to_mainFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_graphs, true).build()
                        )
                    }

                    is Resource.Error -> {
                        pbRegisterPageProgressBar.gone()
                        Snackbar.make(root, result.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> {
                        Log.e("RegisterFragment", "Unexpected result type: $result")
                    }
                }
            }
        }
    }

    private fun setupLoginNavigation() {
        val loginNow = "Login now"
        val formattedString = getString(R.string.no_account, loginNow)

        val spannableStringBuilder = SpannableStringBuilder(formattedString)

        val termsStart = formattedString.indexOf(loginNow)
        val termsEnd = termsStart + loginNow.length

        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {

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

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#E67E9E")
                ds.isUnderlineText = false
            }
        }

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableStringBuilder.setSpan(
            boldSpan,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            termsClickable,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        with(binding.tvRegisterPageHaveAccount) {
            text = spannableStringBuilder
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
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
            tvItemDialogBoxQuestion.text = getString(R.string.dialog_select_profile_question)
            ivItemDialogBoxImage.setImageResource(R.drawable.iv_dialog_insert_profile_image)
            ivItemDialogCloseIcon.setOnClickListener { dialog.dismiss() }

            btnItemDialogBoxNegative.text = getString(R.string.btn_camera)
            btnItemDialogBoxPositive.text = getString(R.string.btn_gallery)

            btnItemDialogBoxNegative.setOnClickListener {
                startCamera()
                dialog.dismiss()
            }
            btnItemDialogBoxPositive.setOnClickListener {
                startGallery()
                dialog.dismiss()
            }
        }

        dialog.show()
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
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")
            binding.civRegisterPageProfilePicturePlaceholder.setImageURI(currentImageUri)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val myFile = createTempFile(context?.cacheDir, "temp_image", ".jpg")
        val inputStream = contentResolver.openInputStream(uri) ?: return myFile
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
}