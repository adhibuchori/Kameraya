package com.adhibuchori.kameraya.ui.main.productDetail

import android.content.res.ColorStateList
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.adhibuchori.data.utils.mapProductDetailToWishlistModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.productDetail.ProductDetailModel
import com.adhibuchori.domain.repository.productDetail.ProductVariant
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentProductDetailBinding
import com.adhibuchori.kameraya.ui.main.productDetail.adapter.ItemSliderProductDetailAdapter
import com.adhibuchori.kameraya.ui.main.wishlist.WishlistViewModel
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orZero
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding, ViewModel>(FragmentProductDetailBinding::inflate) {

    private val productDetailViewModel: ProductDetailViewModel by viewModel()
    private val wishlistViewModel: WishlistViewModel by viewModel()

    private var productDetail: ProductDetailModel? = null
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun initViews() {
        setupNavigation()
        setupToolbar()
        observeProductDetailData()
        observeWishlistState()
    }

    private fun observeWishlistState() {
        productDetailViewModel.isItemInWishlist.launchAndCollectIn(viewLifecycleOwner) { isInWishlistItem ->
            binding.ivProductDetailPageFavoriteIcon.setImageResource(
                if (isInWishlistItem) R.drawable.ic_favorite else R.drawable.ic_unfavorite
            )
        }
    }

    private fun observeProductDetailData() {
        val productId = args.productId
        binding.run {
            if (productId.isNotEmpty()) {
                productDetailViewModel.fetchProductDetail(productId)
                lifecycleScope.launch {
                    productDetailViewModel.productDetail.launchAndCollectIn(viewLifecycleOwner) { resource ->

                        svProductDetailPage.isVisible = resource is Resource.Success
                        tlProductDetailPageButton.isVisible = resource is Resource.Success

                        when (resource) {
                            is Resource.Loading -> {
                                pbProductDetailPageProgressBar.visible()
                            }

                            is Resource.Success -> {
                                pbProductDetailPageProgressBar.gone()
                                productDetail = resource.data
                                bindProductDetailItem()
                                setupWishlistFeature()
                                setupAddCartFeature()
                                productDetailViewModel.isItemInWishlist(
                                    resource.data,
                                    resource.data.productVariant.firstOrNull()?.variantName.orEmpty()
                                )
                            }

                            is Resource.Error -> {
                                pbProductDetailPageProgressBar.gone()
                                Snackbar.make(
                                    root,
                                    "Error: ${resource.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.NetworkError -> {
                                pbProductDetailPageProgressBar.gone()
                                Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                            is Resource.HttpError -> {
                                pbProductDetailPageProgressBar.gone()
                                Snackbar.make(
                                    root,
                                    "HTTP Error: ${resource.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }
                }
            } else {
                Snackbar.make(binding.root, "Product ID is missing", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindProductDetailItem() = with(binding) {
        setupViewPagerProductImageSliderSize()
        setupProductImageSlider(productDetail?.image.orEmpty())
        setupIndicators(productDetail?.image.orEmpty())
        setCurrentIndicator(0)

        tvProductDetailPageProductPrice.text =
            productDetail?.productVariant?.getOrNull(0)?.variantPrice.let {
                formatProductPrice(
                    it?.plus(productDetail?.productPrice.orZero())
                )
            }
        tvProductDetailPageProductName.text = productDetail?.productName
        tvProductDetailPageProductSold.text = productDetail?.sale?.toString()

        val totalRateAndReview = String.format(
            binding.root.context.getString(R.string.camera_rate_and_review),
            productDetail?.productRating.toString(),
            productDetail?.totalReview.toString()
        )

        tvProductDetailPageProductRateAndReview.text = totalRateAndReview
        tvProductDetailPageProductDescription.text = productDetail?.description
        tvProductDetailPageProductRate.text = productDetail?.productRating.toString()

        val totalSatisfaction = String.format(
            binding.root.context.getString(R.string.satisfied_buyers),
            productDetail?.totalSatisfaction.toString()
        )

        tvProductDetailPageBuyerSatisfied.text = totalSatisfaction

        val totalRateAndReviewDetail = String.format(
            binding.root.context.getString(R.string.camera_rate_and_review_detail),
            productDetail?.productRating.toString(),
            productDetail?.totalReview.toString()
        )

        tvProductDetailPageRateAndReviewDetail.text = totalRateAndReviewDetail
        setupProductVariantChips(cgProductDetailPageProductLabel, productDetail?.productVariant)
    }

    private fun setupProductVariantChips(chipGroup: ChipGroup, variants: List<ProductVariant>?) {
        chipGroup.removeAllViews()
        variants?.forEachIndexed { index, variant ->
            val chip = Chip(chipGroup.context).apply {
                id = variant.variantName.hashCode()
                text = variant.variantName
                setTextAppearance(R.style.ChipStyle)
                isCheckable = true

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setChipBackgroundColorResource(R.color.color_primary_light)
                        chipStrokeColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.color_primary
                            )
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.color_primary))
                        isClickable = false
                        binding.tvProductDetailPageProductPrice.text =
                            formatProductPrice(variant.variantPrice.plus(productDetail?.productPrice.orZero()))
                    } else {
                        setChipBackgroundColorResource(android.R.color.transparent)
                        chipStrokeColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.neutral_20
                            )
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.neutral_40))
                        isClickable = true
                    }
                    setupWishlistFeature()
                    productDetailViewModel.isItemInWishlist(productDetail, variant.variantName)
                }

                isChecked = index == 0

                if (isChecked) {
                    setChipBackgroundColorResource(R.color.color_primary_light)
                    chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_primary
                        )
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.color_primary))
                    binding.tvProductDetailPageProductPrice.text =
                        formatProductPrice(variant.variantPrice.plus(productDetail?.productPrice.orZero()))
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun formatProductPrice(price: Int?): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            currencySymbol = "Rp"
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        return "Rp${price?.let { decimalFormat.format(it) } ?: "0"}"
    }

    private fun setupViewPagerProductImageSliderSize() {
        binding.vpProductDetailPageProductImage.apply {
            post {
                val lp = layoutParams as ConstraintLayout.LayoutParams
                lp.height = measuredWidth
                layoutParams = lp
                requestLayout()
            }
        }
    }

    private fun setupProductImageSlider(imageUrl: List<String>) {
        val productImageSlides = ItemSliderProductDetailAdapter().apply { submitList(imageUrl) }

        with(binding) {
            vpProductDetailPageProductImage.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })

            vpProductDetailPageProductImage.adapter = productImageSlides
        }
    }

    private fun setupIndicators(imageUrl: List<String>) {
        val productImageSlides = ItemSliderProductDetailAdapter().apply { submitList(imageUrl) }

        val indicators = arrayOfNulls<ImageView>(productImageSlides.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(16, 0, 16, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.vProductDetailPageIndicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.vProductDetailPageIndicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.vProductDetailPageIndicatorsContainer.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    private fun setupToolbar() {
        with(binding.productDetailPageToolbar) {
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            tvToolbarTitle.text = getString(R.string.product_detail_navigation)
        }
    }

    private fun setupNavigation() {
        with(binding) {
            tvProductDetailPageSeeAll.setOnClickListener {
                val productId = args.productId
                val action =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToReviewFragment(
                        productId
                    )
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action)
            }
            btnProductDetailPageBuyNow.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_productDetailFragment_to_checkoutFragment)
                // TODO: Add checkout functionality.
            }
            ivProductDetailPageShareIcon.setOnClickListener {
                // TODO: Input share feature here.
            }
        }
    }

    private fun setupWishlistFeature() {
        binding.ivProductDetailPageFavoriteIcon.run {
            setOnClickListener {
                productDetailViewModel.handleWishlistItem(
                    productDetail,
                    getSelectedChipVariant(),
                    onHandle = { status ->
                        if (status) {
                            Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
    }

    private fun setupAddCartFeature() {
        binding.btnProductDetailPageAddCart.run {
            setOnClickListener {
                productDetailViewModel.addProductToCart(productDetail, getSelectedChipVariant())
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedChipVariant(): ProductVariant? {
        val selectedVariant = binding.cgProductDetailPageProductLabel.checkedChipId
        val selected =
            productDetail?.productVariant?.find { it.variantName.hashCode() == selectedVariant }

        return selected
    }
}