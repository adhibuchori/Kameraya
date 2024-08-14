package com.adhibuchori.kameraya.ui.main.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentSearchBinding
import com.adhibuchori.kameraya.ui.main.home.adapter.ListItemStoreAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.visible
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment :
    BaseFragment<FragmentSearchBinding, ViewModel>(FragmentSearchBinding::inflate) {

    private val searchViewModel: SearchViewModel by viewModel()
    private val listAdapter = ListItemStoreAdapter()

    override fun initViews() {
        setupRecyclerView()
        setupNavigation()
        observeSearchData()
    }

    private fun setupRecyclerView() = with(binding.rvSearchPage) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = listAdapter
        listAdapter.setOnItemClickListener { product ->
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product.productId.orEmpty())
            Navigation.findNavController(requireActivity(), R.id.fcv_container)
                .navigate(action)
        }
    }

    private fun observeSearchData() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbSearchPageProgressBar.visible()
                }

                is Resource.Success -> {
                    binding.pbSearchPageProgressBar.gone()
                    val searchResults = resource.data
                    listAdapter.submitList(searchResults)
                    binding.rvSearchPage.visible()
                }

                is Resource.Error -> {
                    binding.pbSearchPageProgressBar.gone()
                    Snackbar.make(
                        binding.root,
                        "Error: ${resource.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is Resource.NetworkError -> {
                    binding.pbSearchPageProgressBar.gone()
                    Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT).show()
                }

                is Resource.HttpError -> {
                    binding.pbSearchPageProgressBar.gone()
                    Snackbar.make(
                        binding.root,
                        "HTTP Error: ${resource.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupNavigation() {
        with(binding) {
            ivSearchPageArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
            svSearchPage.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        searchViewModel.searchProducts(it)
                    }
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

    @Suppress("DEPRECATION")
    private fun showKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}