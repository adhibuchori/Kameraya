package com.adhibuchori.kameraya.ui.main.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.productDetail.ProductReviewModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentReviewBinding
import com.adhibuchori.kameraya.ui.main.review.adapter.ListItemReviewAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.google.android.material.snackbar.Snackbar
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
        layoutManager = LinearLayoutManager(requireContext())
        adapter = listAdapter.apply { submitList(review) }
    }

    private fun setupToolbar() {
        with(binding.reviewPageToolbar) {
            tvToolbarTitle.text = getString(R.string.review_navigation)
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }
}