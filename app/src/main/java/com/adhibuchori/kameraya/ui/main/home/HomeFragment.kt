package com.adhibuchori.kameraya.ui.main.home

import android.content.res.ColorStateList
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.store.ProductsModel
import com.adhibuchori.domain.repository.store.ProductsParameter
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentHomeBinding
import com.adhibuchori.kameraya.ui.auth.AuthViewModel
import com.adhibuchori.kameraya.ui.main.MainFragmentDirections
import com.adhibuchori.kameraya.ui.main.home.adapter.GridItemStoreAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.adhibuchori.kameraya.ui.main.home.adapter.ListItemStoreAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.visible
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class HomeFragment : BaseFragment<FragmentHomeBinding, ViewModel>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    private val listAdapter = ListItemStoreAdapter()
    private val gridAdapter = GridItemStoreAdapter()

    private var products: List<ProductsModel> = emptyList()

    override fun initViews() {
        observeProductsData()
        observeUserData()
        observeRecyclerViewLayout()
        setupFilter()
        setupNavigation()
    }

    private fun observeUserData() {
        authViewModel.run {
            userName.observe(viewLifecycleOwner) { userName ->
                binding.tvHomePageWelcomeMessage.text =
                    getString(R.string.welcome_message, userName)
            }
            userImage.observe(viewLifecycleOwner) { userImage ->
                Log.d("userImage at HomeFragment: ", userImage.toString())
                Glide.with(this@HomeFragment)
                    .load(userImage)
                    .into(binding.sivHomePageProfilePicture)
            }
        }
    }

    private fun setupNavigation() {
        with(binding) {
            mcvHomePageNotificationIconContainer.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_mainFragment_to_notificationFragment)
            }
            mcvHomePageCartIconContainer.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_mainFragment_to_cartFragment)
            }
            sbHomePage.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_mainFragment_to_searchFragment)
            }
            ivHomePageSetLayoutView.setOnClickListener {
                homeViewModel.toggleRecyclerViewLayout()
            }
        }
    }

    private fun setupFilter() {
        binding.cgHomePage.setOnCheckedStateChangeListener { group, checkedIds ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                if (checkedIds.contains(chip.id)) {
                    chip.setChipBackgroundColorResource(R.color.color_primary_light)
                    chip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primary
                        )
                    )
                    chip.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primary
                        )
                    )
                } else {
                    chip.setChipBackgroundColorResource(android.R.color.transparent)
                    chip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.neutral_20
                        )
                    )
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_40))
                }
            }

            if (checkedIds.isEmpty()) {
                resetFilter()
            } else {
                checkedIds.forEach { id ->
                    val selectedChip = group.findViewById<Chip>(id)
                    selectedChip?.let {
                        group.removeView(it)
                        group.addView(it, 0)
                        group.requestChildFocus(it, it)
                    }

                    when (id) {
                        R.id.c_home_page_reviews -> {
                            applyFilter(sort = "rating")
                        }

                        R.id.c_home_page_sales -> {
                            applyFilter(sort = "sale")
                        }

                        R.id.c_home_page_lowest_price -> {
                            applyFilter(sort = "lowest")
                        }

                        R.id.c_home_page_highest_price -> {
                            applyFilter(sort = "highest")
                        }
                    }
                }
            }
        }
    }

    private fun resetFilter() {
        applyFilter(sort = "")
    }

    private fun applyFilter(sort: String) = with(homeViewModel) {
        val productsParameter = ProductsParameter(sort = sort)
        setFetchEligible(true)
        fetchProducts(productsParameter)
    }

    private fun observeProductsData() {
        homeViewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbHomePageProgressBar.visible()
                }

                is Resource.Success -> {
                    binding.pbHomePageProgressBar.gone()
                    resource.data.let { productsList ->
                        products = productsList
                        setupRecyclerView()
                    }
                }

                is Resource.HttpError -> {
                    binding.pbHomePageProgressBar.gone()
                    Snackbar.make(
                        binding.root,
                        "HTTP Error: ${resource.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is Resource.NetworkError -> {
                    binding.pbHomePageProgressBar.gone()
                    Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    binding.pbHomePageProgressBar.gone()
                    Snackbar.make(
                        binding.root,
                        "Error: ${resource.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val productsParameter = ProductsParameter()
        homeViewModel.fetchProducts(productsParameter)
    }

    private fun observeRecyclerViewLayout() {
        homeViewModel.isListView.observe(viewLifecycleOwner) { isListView ->
            if (isListView) {
                setupListView()
                binding.ivHomePageSetLayoutView.setImageResource(R.drawable.ic_list_view)
            } else {
                setupGridView()
                binding.ivHomePageSetLayoutView.setImageResource(R.drawable.ic_grid_view)
            }
        }
    }

    private fun setupRecyclerView() {
        homeViewModel.isListView.value?.let { isListView ->
            if (isListView) {
                setupListView()
            } else {
                setupGridView()
            }
        }
    }

    private fun setupGridView() = with(binding.rvHomePage) {
        layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = gridAdapter.apply {
            submitList(products)
            setOnItemClickListener { product ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.productId.orEmpty())
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action)
            }
        }
    }

    private fun setupListView() = with(binding.rvHomePage) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = listAdapter.apply {
            submitList(products)
            setOnItemClickListener { product ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.productId.orEmpty())
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action)
            }
        }
    }
}