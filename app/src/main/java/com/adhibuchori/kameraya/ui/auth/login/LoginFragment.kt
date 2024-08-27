package com.adhibuchori.kameraya.ui.auth.login

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentLoginBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.configureWithSpannable
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.setBoldStyleSpan
import com.adhibuchori.kameraya.utils.extension.updateTextAppearance
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, ViewModel>(FragmentLoginBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()

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
        setupLoginListener()
        setupRegisterNavigation()
    }

    private fun setupLoginListener() {
        with(binding) {
            btnLoginPageLogin.setOnClickListener {
                buttonClickEvent(BUTTON_LOGIN)

                val email = tietLoginPageEmail.text.toString()
                val password = tietLoginPagePassword.text.toString()

                when {
                    email.isEmpty() -> {
                        tilLoginPageEmail.error = getString(R.string.error_email_required)
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        tilLoginPageEmail.error = getString(R.string.error_invalid_email)
                    }

                    password.isEmpty() -> {
                        tilLoginPagePassword.error = getString(R.string.error_password_required)
                    }

                    password.length < 8 -> {
                        tilLoginPagePassword.error = getString(R.string.error_password_length)
                    }

                    else -> {
                        apply {
                            tietLoginPageEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            tietLoginPagePassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                        }
                        authViewModel.login(email, password)
                        setupLoginObserver()
                    }
                }
            }
        }
    }

    private fun setupLoginObserver() {
        with(binding) {
            authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        pbLoginPageProgressBar.visible()
                    }

                    is Resource.Success -> {
                        pbLoginPageProgressBar.gone()
                        view?.let { view ->
                            Navigation.findNavController(view).navigate(
                                R.id.action_loginFragment_to_mainFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.nav_graphs, true).build()
                            )
                        }
                    }

                    is Resource.HttpError -> {
                        binding.pbLoginPageProgressBar.gone()
                        Snackbar.make(
                            binding.root,
                            "HTTP Error: ${result.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.NetworkError -> {
                        binding.pbLoginPageProgressBar.gone()
                        Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        pbLoginPageProgressBar.gone()
                        Snackbar.make(root, result.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> {
                        Log.e("LoginFragment", "Unexpected result type: $result")
                    }
                }
            }
        }
    }

    private fun setupRegisterNavigation() {
        val registerNow = getString(R.string.register_now)
        val formattedString = getString(R.string.no_account, registerNow)
        val spannableStringBuilder = SpannableStringBuilder(formattedString)

        val termsStart = formattedString.indexOf(registerNow)
        val termsEnd = termsStart + registerNow.length

        setRegisterClickableSpan(spannableStringBuilder, termsStart, termsEnd)
        spannableStringBuilder.setBoldStyleSpan(termsStart, termsEnd)
        binding.tvLoginPageNoAccount.configureWithSpannable(spannableStringBuilder)
    }

    private fun setRegisterClickableSpan(
        spannableStringBuilder: SpannableStringBuilder,
        termsStart: Int,
        termsEnd: Int,
    ) {
        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                buttonClickEvent(REGISTER_NAVIGATION)
                navigateToRegister(widget)
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

    private fun navigateToRegister(widget: View) {
        val navController = Navigation.findNavController(widget)
        navController.navigate(R.id.action_loginFragment_to_registerFragment)
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
        const val SCREEN_NAME = "Login"
        const val EVENT_CATEGORY = "Login Fragment"

        const val BUTTON_LOGIN = "Button Login"
        const val REGISTER_NAVIGATION = "Register Navigation"
    }
}