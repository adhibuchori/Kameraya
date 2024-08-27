package com.adhibuchori.kameraya.ui.main.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.store.ProductsModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentSearchBinding
import com.adhibuchori.kameraya.ui.main.home.adapter.ListItemStoreAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class SearchFragment :
    BaseFragment<FragmentSearchBinding, ViewModel>(FragmentSearchBinding::inflate) {

    private val searchViewModel: SearchViewModel by viewModel()
    private val listAdapter = ListItemStoreAdapter()

    override fun initViews() {
        setupRecyclerView()
        setupNavigation()
        observeSearchQuery()
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupRecyclerView() = with(binding.rvSearchPage) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter.apply {
            addLoadStateListener { loadState ->
                observeLoadingState(loadState)
            }
            setOnItemClickListener { product ->
                selectItemEvent(product)
                activity?.let { activity ->
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product.productId.orEmpty())
                    Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                }
            }
        }
    }

    private fun observeLoadingState(loadState: CombinedLoadStates) {
        val isLoading =
            loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
        handleLoading(isLoading)

        val errorState = when {
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            else -> null
        }

        errorState?.let {
            handleLoading(false)
            when (it.error) {
                is IOException -> showNetworkErrorState()
                else -> {
                    val message = it.error.localizedMessage
                    when {
                        message != null && message.contains("not found", ignoreCase = true) -> {
                            showNotFoundState()
                        }
                        else -> showErrorState(message)
                    }
                }
            }
        }
    }

    private fun showNotFoundState() = with(binding) {
        searchPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_not_found_state)
            tvStateTitle.text = getString(R.string.not_found_state_title)
            tvStateDescription.text = getString(R.string.not_found_state_description)
        }
        searchPageState.root.visible()
        Snackbar.make(root, "No results found", Snackbar.LENGTH_SHORT).show()
    }

    private fun showErrorState(message: String?) = with(binding) {
        searchPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_error_state)
            tvStateTitle.text = getString(R.string.error_state_title)
            tvStateDescription.text = getString(R.string.error_state_description)
        }
        searchPageState.root.visible()
        Snackbar.make(root, "Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun showNetworkErrorState() = with(binding) {
        searchPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_network_error_state)
            tvStateTitle.text = getString(R.string.network_error_state_title)
            tvStateDescription.text = getString(R.string.network_error_state_description)
        }
        searchPageState.root.visible()
        Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun handleLoading(status: Boolean) {
        binding.run {
            pbSearchPageProgressBar.run { if (status) visible() else gone() }
            rvSearchPage.run { if (status) gone() else visible() }
        }
    }

    private fun observeSearchQuery() {
        searchViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            searchViewModel.searchProducts(query).observe(viewLifecycleOwner) { pagingData ->
                lifecycleScope.launch {
                    listAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun setupNavigation() {
        with(binding) {
            ivSearchPageArrowBackIcon.setOnClickListener {
                buttonClickEvent()
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            svSearchPage.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { searchViewModel.setSearchQuery(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            svSearchPage.post {
                svSearchPage.requestFocus()
                showKeyboard()
            }
            rvSearchPage.gone()
        }
    }

    private fun buttonClickEvent() {
        searchViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to ARROW_BACK_ICON,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private fun selectItemEvent(product: ProductsModel) {
        val bundle = bundleOf(
            ITEM_NAME to product.productName,
            ITEM_PRICE to product.productPrice,
            ITEM_STORE to product.store,
            ITEM_REVIEW to product.productRating
        )
        searchViewModel.logSelectItem(bundle)
    }

    @Suppress("DEPRECATION")
    private fun showKeyboard() {
        context?.let { context ->
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    private companion object {
        const val ITEM_NAME = "item_name"
        const val ITEM_PRICE = "item_price"
        const val ITEM_STORE = "item_store"
        const val ITEM_REVIEW = "item_review"

        const val SCREEN_NAME = "Search"
        const val EVENT_CATEGORY = "Search Fragment"
        const val ARROW_BACK_ICON = "Arrow Back Icon"
    }
}