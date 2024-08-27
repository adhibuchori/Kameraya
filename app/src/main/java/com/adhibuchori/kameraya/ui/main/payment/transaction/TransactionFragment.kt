package com.adhibuchori.kameraya.ui.main.payment.transaction

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.transaction.TransactionModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentTransactionBinding
import com.adhibuchori.kameraya.ui.main.MainFragmentDirections
import com.adhibuchori.kameraya.ui.main.payment.transaction.adapter.ListItemTransactionAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionFragment :
    BaseFragment<FragmentTransactionBinding, ViewModel>(FragmentTransactionBinding::inflate) {

    private val transactionViewModel: TransactionViewModel by viewModel()
    private var transactions: List<TransactionModel> = emptyList()

    private val listAdapter: ListItemTransactionAdapter by lazy {
        ListItemTransactionAdapter().apply {
            activity?.let { activity ->
                setOnItemClickListener { transactionItem ->
                    selectItemEvent(transactionItem)
                    val fulfillmentModelData =
                        transactionViewModel.mapTransactionToFulfillmentModelData(transactionItem)
                    val action = MainFragmentDirections.actionMainFragmentToPaymentStatusFragment(
                        fulfillmentModelData
                    )
                    Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                }
                setOnReviewListener { transactionItem ->
                    buttonClickEvent()
                    val fulfillmentModelData =
                        transactionViewModel.mapTransactionToFulfillmentModelData(transactionItem)
                    val action = MainFragmentDirections.actionMainFragmentToPaymentStatusFragment(
                        fulfillmentModelData
                    )
                    Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                }
            }
        }
    }

    override fun initViews() {
        setupToolbar()
        observeTransactionData()
    }

    override fun onResume() {
        super.onResume()
        transactionViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupToolbar() {
        with(binding.transactionPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_transaction)
            ivToolbarArrowBackIcon.gone()
        }
    }

    private fun observeTransactionData() {
        transactionViewModel.fetchTransactions()
        transactionViewModel.transactions.observe(viewLifecycleOwner) { resource ->
            binding.run {
                when (resource) {
                    is Resource.Loading -> pbTransactionPageProgressBar.visible()

                    is Resource.Success -> {
                        resource.data.let { transactionList ->
                            transactions = transactionList
                            if (transactionList.isEmpty()) showEmptyState() else showTransactionData()
                        }
                    }

                    is Resource.HttpError -> showHttpErrorState(resource.message)
                    is Resource.NetworkError -> showNetworkErrorState()
                    is Resource.Error -> showErrorState(resource.message)
                    else -> Log.e("TransactionFragment", "Unexpected result type: $resource")
                }
            }
        }
    }

    private fun showTransactionData() = with(binding) {
        pbTransactionPageProgressBar.gone()
        setupRecyclerView()
    }

    private fun showEmptyState() = with(binding) {
        pbTransactionPageProgressBar.gone()
        transactionPageState.root.visible()
        setupRecyclerView()
    }

    private fun showErrorState(message: String?) = with(binding) {
        pbTransactionPageProgressBar.gone()
        transactionPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_error_state)
            tvStateTitle.text = getString(R.string.error_state_title)
            tvStateDescription.text = getString(R.string.error_state_description)
        }
        transactionPageState.root.visible()
        Snackbar.make(root, "Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun showNetworkErrorState() = with(binding) {
        pbTransactionPageProgressBar.gone()
        transactionPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_network_error_state)
            tvStateTitle.text = getString(R.string.network_error_state_title)
            tvStateDescription.text = getString(R.string.network_error_state_description)
        }
        transactionPageState.root.visible()
        Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun showHttpErrorState(message: String?) = with(binding) {
        pbTransactionPageProgressBar.gone()
        transactionPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_http_error_state)
            tvStateTitle.text = getString(R.string.http_error_state_title)
            tvStateDescription.text = getString(R.string.http_error_state_description)
        }
        transactionPageState.root.visible()
        Snackbar.make(root, "Http Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() = with(binding.rvTransactionPage) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter.apply {
            submitList(transactions)
        }
    }


    private fun buttonClickEvent() {
        transactionViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to BUTTON_REVIEW,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private fun selectItemEvent(transaction: TransactionModel) {
        val bundle = bundleOf(
            ITEM_TRANSACTION_NAME to transaction.name,
            ITEM_TRANSACTION_QUANTITY to transaction.items,
            ITEM_TRANSACTION_PAYMENT_TOTAL to transaction.total,
            ITEM_TRANSACTION_DATE to transaction.date,
            ITEM_TRANSACTION_STATUS to transaction.status,
            ITEM_TRANSACTION_REVIEW to transaction.review
        )
        transactionViewModel.logSelectItem(bundle)
    }

    private companion object {
        const val ITEM_TRANSACTION_NAME = "item_transaction_name"
        const val ITEM_TRANSACTION_QUANTITY = "item_transaction_quantity"
        const val ITEM_TRANSACTION_PAYMENT_TOTAL = "item_transaction_payment_total"
        const val ITEM_TRANSACTION_DATE = "item_transaction_date"
        const val ITEM_TRANSACTION_STATUS = "item_transaction_status"
        const val ITEM_TRANSACTION_REVIEW = "item_transaction_review"

        const val SCREEN_NAME = "Transaction"
        const val EVENT_CATEGORY = "Transaction Fragment"
        const val BUTTON_REVIEW = "Button Review"
    }
}