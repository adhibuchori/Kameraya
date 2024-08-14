package com.adhibuchori.kameraya.ui.auth.login

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentLoginBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, ViewModel>(FragmentLoginBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()

    override fun initViews() {
        setupNavigation()
    }

    private fun setupNavigation() {
        setupLoginListener()
        setupRegisterNavigation()
    }

    private fun setupLoginListener() {
        with(binding) {
            btnLoginPageLogin.setOnClickListener {
                val email = tietLoginPageEmail.text.toString()
                val password = tietLoginPagePassword.text.toString()

                when {
                    email.isEmpty() -> {
                        tilLoginPageEmail.error = "Please enter email first"
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        tilLoginPageEmail.error = "Invalid email format"
                    }

                    password.isEmpty() -> {
                        tilLoginPagePassword.error = "Please enter password first"
                    }

                    password.length < 8 -> {
                        tilLoginPagePassword.error =
                            "Password must be at least 8 characters long"
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
                        Navigation.findNavController(requireView()).navigate(
                            R.id.action_loginFragment_to_mainFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_graphs, true).build()
                        )
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
        val registerNow = "Register now"
        val formattedString = getString(R.string.no_account, registerNow)

        val spannableStringBuilder = SpannableStringBuilder(formattedString)

        val termsStart = formattedString.indexOf(registerNow)
        val termsEnd = termsStart + registerNow.length

        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val navController = Navigation.findNavController(widget)
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
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

        with(binding.tvLoginPageNoAccount) {
            text = spannableStringBuilder
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}