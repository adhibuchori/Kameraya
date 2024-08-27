package com.adhibuchori.kameraya.ui.main.review

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.productDetail.ProductReviewModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentReviewBinding
import com.adhibuchori.kameraya.ui.main.review.adapter.ListItemReviewAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment :
    BaseFragment<FragmentReviewBinding, ViewModel>(FragmentReviewBinding::inflate) {

    private val reviewViewModel: ReviewViewModel by viewModel()
    private val args: ReviewFragmentArgs by navArgs()

    private var review: List<ProductReviewModel> = emptyList()

    private val listAdapter = ListItemReviewAdapter()

    override fun initViews() {
        setupToolbar()
        observeReviewData()
    }

    override fun onResume() {
        super.onResume()
        reviewViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun observeReviewData() {
        val productId = args.productId
        binding.run {
            if (productId.isNotEmpty()) {
                reviewViewModel.fetchProductReviews(productId)
                lifecycleScope.launch {
                    reviewViewModel.productReviews.observe(viewLifecycleOwner) { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                pbReviewPageProgressBar.visible()
                            }

                            is Resource.Success -> {
                                pbReviewPageProgressBar.gone()
                                resource.data.let { reviewList ->
                                    review = reviewList
                                    setupRecyclerView()
                                }
                            }

                            is Resource.HttpError -> {
                                pbReviewPageProgressBar.gone()
                                Snackbar.make(
                                    root,
                                    "HTTP Error: ${resource.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.NetworkError -> {
                                pbReviewPageProgressBar.gone()
                                Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                            is Resource.Error -> {
                                pbReviewPageProgressBar.gone()
                                Snackbar.make(
                                    root,
                                    "Error: ${resource.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() = with(binding.rvReviewPage) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter.apply { submitList(review) }
    }

    private fun setupToolbar() {
        with(binding.reviewPageToolbar) {
            tvToolbarTitle.text = getString(R.string.review_navigation)
            ivToolbarArrowBackIcon.setOnClickListener {
                buttonClickEvent()
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }

    private fun buttonClickEvent() {
        reviewViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to ARROW_BACK_ICON,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val SCREEN_NAME = "Review"
        const val EVENT_CATEGORY = "Review Fragment"

        const val ARROW_BACK_ICON = "Arrow Back Icon"
    }
}