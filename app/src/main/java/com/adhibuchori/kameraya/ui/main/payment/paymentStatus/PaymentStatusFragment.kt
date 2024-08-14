package com.adhibuchori.kameraya.ui.main.payment.paymentStatus

import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentPaymentStatusBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone

class PaymentStatusFragment :
    BaseFragment<FragmentPaymentStatusBinding, ViewModel>(FragmentPaymentStatusBinding::inflate) {
    override fun initViews() {
        setupNavigation()
        setupToolbar()
    }

    private fun setupNavigation() {
        with(binding) {
            btnPaymentStatusPageDone.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_paymentStatusFragment_to_mainFragment)
            }
        }
    }

    private fun setupToolbar() {
        with(binding.paymentStatusToolbar) {
            tvToolbarTitle.text = getString(R.string.payment_status_navigation)
            ivToolbarArrowBackIcon.gone()
        }
    }
}