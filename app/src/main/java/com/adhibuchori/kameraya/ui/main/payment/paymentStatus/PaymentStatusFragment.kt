package com.adhibuchori.kameraya.ui.main.payment.paymentStatus

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentPaymentStatusBinding
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.orZero
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentStatusFragment :
    BaseFragment<FragmentPaymentStatusBinding, ViewModel>(FragmentPaymentStatusBinding::inflate) {

    private val paymentStatusViewModel: PaymentStatusViewModel by viewModel()
    private var fulfillmentModel: FulfillmentModel? = null
    private val args: PaymentStatusFragmentArgs by navArgs()

    override fun initViews() {
        setupToolbar()
        observeRatingData()
        setupSubmitButtonListener()
        retrievePaymentStatusData()
    }

    override fun onResume() {
        super.onResume()
        paymentStatusViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupSubmitButtonListener() = with(binding) {
        btnPaymentStatusPageDone.setOnClickListener {
            buttonClickEvent()
            val rating = rbPaymentStatusPagePaymentRate.rating
            val review = tietPaymentStatusPageReview.text.toString()
            if (fulfillmentModel?.rating.orZero() != 0.0) {
                if (review.isEmpty()) {
                    Snackbar.make(
                        root,
                        R.string.review_required,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else if (fulfillmentModel?.review?.isNotEmpty() == true) {
                    activity?.let { activity ->
                        Navigation.findNavController(activity, R.id.fcv_container)
                            .navigate(R.id.action_paymentStatusFragment_to_mainFragment)
                    }
                } else {
                    submitRating()
                }
            } else {
                if (rating == 0f) {
                    Snackbar.make(
                        root,
                        R.string.review_required,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    submitRating()
                }
            }
        }
    }

    private fun setupToolbar() = with(binding.paymentStatusToolbar) {
        tvToolbarTitle.text = getString(R.string.payment_status_navigation)
        ivToolbarArrowBackIcon.gone()
    }

    private fun retrievePaymentStatusData() {
        fulfillmentModel = args.fulfillmentModelData
        bindPaymentStatusItems()
    }

    @SuppressLint("SetTextI18n")
    private fun bindPaymentStatusItems() = with(binding) {
        tvPaymentStatusPagePaymentIdData.text = fulfillmentModel?.invoiceId
        tvPaymentStatusPagePaymentStatusData.text = if (fulfillmentModel?.status == true) "Selesai" else "Belum Selesai"
        tvPaymentStatusPagePaymentDateData.text = fulfillmentModel?.date
        tvPaymentStatusPagePaymentTimeData.text = fulfillmentModel?.time
        tvPaymentStatusPagePaymentMethodData.text = fulfillmentModel?.payment
        tvPaymentStatusPagePaymentTotalData.text =
            fulfillmentModel?.total?.let { it.formatPrice() }

        if (fulfillmentModel?.rating.orZero() != 0.0) {
            rbPaymentStatusPagePaymentRate.run {
                rating = fulfillmentModel?.rating?.toFloat() ?: 0f
                setIsIndicator(true)
            }
            if (fulfillmentModel?.review?.isNotEmpty() == true) {
                btnPaymentStatusPageDone.text = "Okay"
                tilPaymentStatusPagePaymentReview.apply {
                    tietPaymentStatusPageReview.setText(fulfillmentModel?.review)
                    tietPaymentStatusPageReview.isEnabled = false
                    isHintAnimationEnabled = false
                    hint = ""
                }
            }
        }
    }

    private fun submitRating() {
        val rating = binding.rbPaymentStatusPagePaymentRate.rating.toInt()
        val review = binding.tietPaymentStatusPageReview.text.toString()
        val invoiceId = fulfillmentModel?.invoiceId.orEmpty()

        if (invoiceId.isNotEmpty() && rating > 0) {
            paymentStatusViewModel.run {
                val ratingParameter = mapToRatingParameter(rating, review, invoiceId)
                ratingParameter.let {
                    paymentStatusViewModel.submitRating(ratingParameter)
                }
            }
        }
    }

    private fun observeRatingData() {
        paymentStatusViewModel.ratingData.launchAndCollectIn(viewLifecycleOwner) {
            if (it is Resource.Success) {
                activity?.let { activity ->
                    Navigation.findNavController(activity, R.id.fcv_container)
                        .navigate(R.id.action_paymentStatusFragment_to_mainFragment)
                }
            }
        }
    }

    private fun buttonClickEvent() {
        paymentStatusViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to BUTTON_REVIEW,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val SCREEN_NAME = "Payment Status"
        const val EVENT_CATEGORY = "Payment Status Fragment"

        const val BUTTON_REVIEW = "Button Review"
    }
}