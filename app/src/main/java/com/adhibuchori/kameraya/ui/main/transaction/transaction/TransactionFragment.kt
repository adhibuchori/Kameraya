package com.adhibuchori.kameraya.ui.main.transaction.transaction

import androidx.lifecycle.ViewModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentTransactionBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone

class TransactionFragment :
    BaseFragment<FragmentTransactionBinding, ViewModel>(FragmentTransactionBinding::inflate) {
    override fun initViews() {
        setupToolbar()
    }

    private fun setupToolbar() {
        with(binding.transactionPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_transaction)
            ivToolbarArrowBackIcon.gone()
        }
    }
}