package com.adhibuchori.kameraya.ui.main.productDetail

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.checkout.CheckoutItem
import com.adhibuchori.domain.productDetail.ProductDetailModel
import com.adhibuchori.domain.productDetail.ProductVariant
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentProductDetailBinding
import com.adhibuchori.kameraya.ui.main.productDetail.adapter.ItemSliderProductDetailAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orZero
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding, ViewModel>(FragmentProductDetailBinding::inflate) {

    private val productDetailViewModel: ProductDetailViewModel by viewModel()

    private var productDetail: ProductDetailModel? = null
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun initViews() {
        setupNavigation()
        setupToolbar()
        observeProductDetailData()
        observeWishlistState()
    }

    override fun onResume() {
        super.onResume()
        productDetailViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
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

                            else -> {
                                Log.e("ProductDetailFragment", "Unexpected result type: $resource")
                            }
                        }
                    }
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.snackbar_product_id_missing),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindProductDetailItem() = with(binding) {
        setupViewPagerProductImageSliderSize()
        setupProductImageSlider(productDetail?.image.orEmpty())
        setupIndicators(productDetail?.image.orEmpty())

        tvProductDetailPageProductPrice.text =
            productDetail?.productVariant?.getOrNull(0)?.variantPrice.let {
                it?.plus(productDetail?.productPrice.orZero()).formatPrice()
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
                        setChipBackgroundColorResource(R.color.color_chip_background)
                        chipStrokeColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.color_chip_stroke
                            )
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.color_chip_text))
                        isClickable = false
                        binding.tvProductDetailPageProductPrice.text =
                            variant.variantPrice.plus(productDetail?.productPrice.orZero())
                                .formatPrice()
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
                    setChipBackgroundColorResource(R.color.color_chip_background)
                    chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_chip_stroke
                        )
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.color_chip_text))
                    binding.tvProductDetailPageProductPrice.text =
                        variant.variantPrice.plus(productDetail?.productPrice.orZero())
                            .formatPrice()
                }
            }
            chipGroup.addView(chip)
        }
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
                    setCurrentIndicator(position, imageUrl.size)
                }
            })

            vpProductDetailPageProductImage.adapter = productImageSlides
        }
    }

    private fun setupIndicators(imageUrl: List<String>) {
        binding.run {
            vProductDetailPageIndicatorsContainer.removeAllViews()
            val indicators = arrayOfNulls<ImageView>(imageUrl.size)
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            layoutParams.setMargins(16, 0, 16, 0)
            for (i in indicators.indices) {
                indicators[i] = ImageView(context)
                indicators[i].apply {
                    this?.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_inactive
                        )
                    )
                    this?.layoutParams = layoutParams
                }
                vProductDetailPageIndicatorsContainer.addView(indicators[i])
            }
        }
        setCurrentIndicator(0, imageUrl.size)
    }

    private fun setCurrentIndicator(index: Int, itemCount: Int) {
        for (i in 0 until itemCount) {
            val imageView = binding.vProductDetailPageIndicatorsContainer.getChildAt(i) as ImageView
            if (i == index) {
                context?.let { context ->
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_active
                        )
                    )
                }
            } else {
                context?.let { context ->
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_inactive
                        )
                    )
                }
            }
        }
    }

    private fun setupToolbar() {
        with(binding.productDetailPageToolbar) {
            ivToolbarArrowBackIcon.setOnClickListener {
                buttonClickEvent(ARROW_BACK_ICON)
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            tvToolbarTitle.text = getString(R.string.product_detail_navigation)
        }
    }

    private fun setupNavigation() {
        with(binding) {
            tvProductDetailPageSeeAll.setOnClickListener {
                buttonClickEvent(REVIEW_NAVIGATION)
                val productId = args.productId
                val action =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToReviewFragment(
                        productId
                    )
                activity?.let { activity ->
                    Navigation.findNavController(activity, R.id.fcv_container)
                        .navigate(action)
                }
            }
            btnProductDetailPageBuyNow.setOnClickListener {
                buttonClickEvent(BUTTON_BUY_NOW)
                val selectedProduct = productDetail?.let { productDetailData ->
                    listOf(
                        productDetailViewModel.mapProductDetailToCart(
                            productDetailData,
                            getSelectedChipVariant()
                        )
                    )
                }

                val checkoutItem = CheckoutItem(cartItems = selectedProduct)
                val bundle = Bundle().apply {
                    putParcelable(CHECKOUT_ITEM, checkoutItem)
                }

                findNavController().navigate(
                    R.id.action_productDetailFragment_to_checkoutFragment,
                    bundle
                )
            }
            ivProductDetailPageShareIcon.setOnClickListener {
                buttonClickEvent(SHARE_ICON)
                val productId = args.productId
                val deepLinkUrl = "https://kameraya.link/products/$productId"

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_text, deepLinkUrl))
                    type = "text/plain"
                }

                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_product_chooser_title)
                    )
                )
            }
        }
    }

    private fun setupWishlistFeature() {
        binding.ivProductDetailPageFavoriteIcon.run {
            setOnClickListener {
                buttonClickEvent(FAVORITE_ICON)
                productDetailViewModel.handleWishlistItem(
                    productDetail,
                    getSelectedChipVariant(),
                    onHandle = { status ->
                        if (status) {
                            Toast.makeText(
                                context,
                                getString(R.string.wishlist_added),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.wishlist_removed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    private fun setupAddCartFeature() {
        binding.btnProductDetailPageAddCart.run {
            setOnClickListener {
                buttonClickEvent(BUTTON_ADD_TO_CART)
                productDetailViewModel.addProductToCart(productDetail, getSelectedChipVariant())
                Toast.makeText(context, getString(R.string.added_to_cart), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getSelectedChipVariant(): ProductVariant? {
        val selectedVariant = binding.cgProductDetailPageProductLabel.checkedChipId
        val selected =
            productDetail?.productVariant?.find { it.variantName.hashCode() == selectedVariant }

        return selected
    }

    private fun buttonClickEvent(buttonName: String) {
        productDetailViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val CHECKOUT_ITEM = "checkoutItem"

        const val SCREEN_NAME = "Product Detail"
        const val EVENT_CATEGORY = "Product Detail Fragment"

        const val BUTTON_ADD_TO_CART = "Button Add to Cart"
        const val BUTTON_BUY_NOW = "Button Buy Now"
        const val SHARE_ICON = "Share Icon"
        const val FAVORITE_ICON = "Favorite Icon"
        const val REVIEW_NAVIGATION = "Review Navigation"
        const val ARROW_BACK_ICON = "Arrow Back Icon"
    }
}