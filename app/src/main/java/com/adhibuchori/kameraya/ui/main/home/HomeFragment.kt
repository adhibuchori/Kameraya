package com.adhibuchori.kameraya.ui.main.home

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.domain.store.ProductsParameter
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentHomeBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.ui.main.MainFragmentDirections
import com.adhibuchori.kameraya.ui.main.home.adapter.GridItemStoreAdapter
import com.adhibuchori.kameraya.ui.main.home.adapter.ListItemStoreAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class HomeFragment : BaseFragment<FragmentHomeBinding, ViewModel>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    private val listAdapter = ListItemStoreAdapter()
    private val gridAdapter = GridItemStoreAdapter()

    override fun initViews() {
        observeUserData()
        observeInitialData()
        observeProductFilterData()
        observeLoadingState()
        setupFilter()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun observeUserData() {
        authViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.tvHomePageWelcomeMessage.text =
                getString(R.string.welcome_message, userName)
        }
        authViewModel.userImage.observe(viewLifecycleOwner) { userImage ->
            Glide.with(this)
                .load(userImage)
                .into(binding.sivHomePageProfilePicture)
        }
    }

    private fun setupNavigation() {
        with(binding) {
            mcvHomePageNotificationIconContainer.setOnClickListener {
                buttonClickEvent(NOTIFICATION_ICON)
                navigateTo(R.id.action_mainFragment_to_notificationFragment)
            }
            mcvHomePageCartIconContainer.setOnClickListener {
                buttonClickEvent(CART_ICON)
                navigateTo(R.id.action_mainFragment_to_cartFragment)
            }
            sbHomePage.setOnClickListener {
                buttonClickEvent(SEARCH_BAR)
                navigateTo(R.id.action_mainFragment_to_searchFragment)
            }
            ivHomePageSetLayoutView.setOnClickListener {
                buttonClickEvent(SET_LAYOUT)
                homeViewModel.toggleRecyclerViewLayout()
            }
        }
    }

    private fun navigateTo(actionId: Int) {
        activity?.let { activity ->
            Navigation.findNavController(activity, R.id.fcv_container)
                .navigate(actionId)
        }
    }

    private fun buttonClickEvent(buttonName: String) {
        homeViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private fun setupFilter() {
        binding.cgHomePage.setOnCheckedStateChangeListener { group, checkedIds ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                val isChecked = checkedIds.contains(chip.id)
                updateChipStyle(chip, isChecked)
            }

            if (checkedIds.isEmpty()) {
                resetFilter()
            } else {
                handleFilterSelection(checkedIds)
            }
        }
    }

    private fun updateChipStyle(chip: Chip, isChecked: Boolean) {
        context?.let { context ->
            if (isChecked) {
                chip.setChipBackgroundColorResource(R.color.color_chip_background)
                chip.chipStrokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        R.color.color_chip_stroke
                    )
                )
                chip.setTextColor(ContextCompat.getColor(context, R.color.color_chip_text))
            } else {
                chip.setChipBackgroundColorResource(android.R.color.transparent)
                chip.chipStrokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.neutral_20))
                chip.setTextColor(ContextCompat.getColor(context, R.color.neutral_40))
            }
        }
    }

    private fun handleFilterSelection(checkedIds: List<Int>) {
        checkedIds.forEach { id ->
            binding.run {
                val selectedChip = cgHomePage.findViewById<Chip>(id)
                selectedChip?.let {
                    cgHomePage.removeView(it)
                    cgHomePage.addView(it, 0)
                    cgHomePage.requestChildFocus(it, it)
                }
            }

            when (id) {
                R.id.c_home_page_reviews -> applyFilter(FILTER_RATING)
                R.id.c_home_page_sales -> applyFilter(FILTER_SALE)
                R.id.c_home_page_lowest_price -> applyFilter(FILTER_LOWEST)
                R.id.c_home_page_highest_price -> applyFilter(FILTER_HIGHEST)
            }
        }
    }

    private fun resetFilter() {
        applyFilter("")
    }

    private fun applyFilter(sort: String) {
        homeViewModel.updateFilter(sort)
        binding.rvHomePage.scrollToPosition(0)
    }

    private fun fetchProductsList(parameter: ProductsParameter) {
        homeViewModel.fetchProducts(parameter).observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                listAdapter.submitData(it)
            }
        }
    }

    private fun fetchProductsGrid(parameter: ProductsParameter) {
        homeViewModel.fetchProducts(parameter).observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                gridAdapter.submitData(it)
            }
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launch {
            val combinedFlow = merge(listAdapter.loadStateFlow, gridAdapter.loadStateFlow)
            combinedFlow.collectLatest { loadState ->
                val isLoading = loadState.refresh is LoadState.Loading
                handleLoading(isLoading)

                val isAppendLoading = loadState.append is LoadState.Loading
                handleAppendLoading(isAppendLoading)

                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }

                errorState?.let {
                    handleLoading(false)
                    if (it.error is IOException) {
                        showNetworkErrorState()
                    } else {
                        showErrorState(it.error.localizedMessage)
                    }
                }
            }
        }
    }

    private fun showErrorState(message: String?) = with(binding) {
        homePageState.run {
            ivStateImage.setImageResource(R.drawable.iv_error_state)
            tvStateTitle.text = getString(R.string.error_state_title)
            tvStateDescription.text = getString(R.string.error_state_description)
        }
        homePageState.root.visible()
        Snackbar.make(root, "Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun showNetworkErrorState() = with(binding) {
        homePageState.run {
            ivStateImage.setImageResource(R.drawable.iv_network_error_state)
            tvStateTitle.text = getString(R.string.network_error_state_title)
            tvStateDescription.text = getString(R.string.network_error_state_description)
        }
        homePageState.root.visible()
        Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun handleLoading(status: Boolean) = with(binding) {
        pbHomePageProgressBar.run { if (status) visible() else gone() }
        rvHomePage.run { if (status) gone() else visible() }
    }

    private fun handleAppendLoading(status: Boolean) =
        with(binding.pbHomePageProgressBarAppend) {
            if (status) visible() else gone()
        }

    private fun observeInitialData() = with(homeViewModel) {
        fetchInitialData().observe(viewLifecycleOwner) { parameter ->
            isListView.observe(viewLifecycleOwner) { isListView ->
                if (isListView) {
                    setupListView()
                    binding.ivHomePageSetLayoutView.setImageResource(R.drawable.ic_list_view)
                    fetchProductsList(parameter)
                } else {
                    setupGridView()
                    binding.ivHomePageSetLayoutView.setImageResource(R.drawable.ic_grid_view)
                    fetchProductsGrid(parameter)
                }
            }
        }
    }

    private fun observeProductFilterData() = with(homeViewModel) {
        productFilter.observe(viewLifecycleOwner) { parameter ->
            isListView.observe(viewLifecycleOwner) { isListView ->
                if (isListView) fetchProductsList(parameter) else fetchProductsGrid(parameter)
            }
        }
    }

    private fun setupGridView() = with(binding.rvHomePage) {
        layoutManager = GridLayoutManager(context, 2)
        adapter = gridAdapter.apply {
            setOnItemClickListener { product ->
                selectItemEvent(product)
                activity?.let { activity ->
                    val action =
                        MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.productId.orEmpty())
                    Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                }
            }
        }
    }

    private fun setupListView() = with(binding.rvHomePage) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter.apply {
            setOnItemClickListener { product ->
                selectItemEvent(product)
                activity?.let { activity ->
                    val action =
                        MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.productId.orEmpty())
                    Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                }
            }
        }
    }

    private fun selectItemEvent(product: ProductsModel) {
        val bundle = bundleOf(
            ITEM_NAME to product.productName,
            ITEM_PRICE to product.productPrice,
            ITEM_STORE to product.store,
            ITEM_REVIEW to product.productRating
        )
        homeViewModel.logSelectItem(bundle)
    }

    private companion object {
        const val SCREEN_NAME = "Home"
        const val EVENT_CATEGORY = "Home Fragment"

        const val ITEM_NAME = "item_name"
        const val ITEM_PRICE = "item_price"
        const val ITEM_STORE = "item_store"
        const val ITEM_REVIEW = "item_review"

        const val NOTIFICATION_ICON = "Notification Icon"
        const val CART_ICON = "Cart Icon"
        const val SEARCH_BAR = "Search Bar"
        const val SET_LAYOUT = "Set Layout Icon"

        const val FILTER_RATING = "rating"
        const val FILTER_SALE = "sale"
        const val FILTER_LOWEST = "lowest"
        const val FILTER_HIGHEST = "highest"
    }
}
