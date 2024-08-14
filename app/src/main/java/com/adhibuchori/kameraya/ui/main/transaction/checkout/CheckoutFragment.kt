package com.adhibuchori.kameraya.ui.main.transaction.checkout

import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentCheckoutBinding
import com.adhibuchori.kameraya.databinding.ItemBottomSheetPaymentMethodBinding
import com.adhibuchori.kameraya.ui.main.payment.paymentMethod.adapter.ListPaymentMethodAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class CheckoutFragment :
    BaseFragment<FragmentCheckoutBinding, ViewModel>(FragmentCheckoutBinding::inflate) {

    private lateinit var virtualBankAdapter: ListPaymentMethodAdapter
    private lateinit var transferBankAdapter: ListPaymentMethodAdapter
    private lateinit var instantPaymentAdapter: ListPaymentMethodAdapter

    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var bottomSheetBinding: ItemBottomSheetPaymentMethodBinding

    override fun initViews() {
        setupToolbar()
        setupNavigation()
    }

    private fun setupNavigation() {
        with(binding) {
            btnCheckoutPageBuy.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_checkoutFragment_to_paymentStatusFragment)
                // TODO: Add payment functionality.
            }
            cvCheckoutPageChoosePaymentMethodContainer.setOnClickListener { showPaymentMethodBottomSheet() }
        }
    }

    private fun showPaymentMethodBottomSheet() {
        initPaymentMethodBottomSheet()

        bottomSheetDialog?.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.ivBottomSheetCloseIcon.setOnClickListener { bottomSheetDialog?.dismiss() }

        setupPaymentMethodsRecyclerView(bottomSheetBinding)

        bottomSheetDialog?.show()
    }

    private fun initPaymentMethodBottomSheet() {
        virtualBankAdapter = ListPaymentMethodAdapter()
        transferBankAdapter = ListPaymentMethodAdapter()
        instantPaymentAdapter = ListPaymentMethodAdapter()

        bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        bottomSheetBinding = ItemBottomSheetPaymentMethodBinding.inflate(layoutInflater)
    }

    private fun setupPaymentMethodsRecyclerView(bottomSheetBinding: ItemBottomSheetPaymentMethodBinding) {
        with(bottomSheetBinding) {
            rvBottomSheetVirtualAccount.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = virtualBankAdapter
            }
            rvBottomSheetTransferBank.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = transferBankAdapter
            }
            rvBottomSheetInstantPayment.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = instantPaymentAdapter
            }
        }
    }

    private fun setupToolbar() {
        with(binding.checkoutPageToolbar) {
            tvToolbarTitle.text = getString(R.string.payment_status_navigation)
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }
}