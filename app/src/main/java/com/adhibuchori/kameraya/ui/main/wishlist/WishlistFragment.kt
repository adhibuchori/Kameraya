package com.adhibuchori.kameraya.ui.main.wishlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.repository.wishlist.WishlistModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentWishlistBinding
import com.adhibuchori.kameraya.ui.main.MainFragmentDirections
import com.adhibuchori.kameraya.ui.main.transaction.cart.CartViewModel
import com.adhibuchori.kameraya.ui.main.wishlist.adapter.GridItemWishlistAdapter
import com.adhibuchori.kameraya.ui.main.wishlist.adapter.ListItemWishlistAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class WishlistFragment :
    BaseFragment<FragmentWishlistBinding, ViewModel>(FragmentWishlistBinding::inflate) {

    private val wishlistViewModel: WishlistViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()

    private val listAdapter: ListItemWishlistAdapter by lazy {
        ListItemWishlistAdapter().apply {
            setOnItemClickListener { wishlistItem ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(wishlistItem.productId.orEmpty())
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action)
            }
            setOnDeleteItemListener { wishlistItem ->
                wishlistViewModel.removeWishlistItem(wishlistItem)
                Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                Log.d("wishlistItemWishlistFragment: ", wishlistItem.toString())
            }

            setOnAddCartItemListener { cartItem ->
                cartViewModel.addCartItem(cartItem)
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val gridAdapter: GridItemWishlistAdapter by lazy {
        GridItemWishlistAdapter().apply {
            setOnItemClickListener { wishlistItem ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(wishlistItem.productId.orEmpty())
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action)
            }
            setOnDeleteItemListener { item ->
                wishlistViewModel.removeWishlistItem(item)
            }
            setOnAddCartItemListener {
                // TODO: Implement add to cart feature.
            }
        }
    }

    override fun initViews() {
        setupToolbar()
        setupRecyclerView()
        observeRecyclerViewLayout()
        observeWishlistItems()
        setupViews()
    }

    private fun setupViews() {
        binding.run {
            ivWishlistPageSetLayoutView.setOnClickListener {
                wishlistViewModel.toggleRecyclerViewLayout()
            }
        }
    }

    private fun observeWishlistItems() {
        wishlistViewModel.wishlistItems.launchAndCollectIn(viewLifecycleOwner) { items ->
            observeWishlistData(items)
            updateWishlistItemCount(items.size)
        }
    }

    private fun observeRecyclerViewLayout() {
        wishlistViewModel.isListView.observe(viewLifecycleOwner) { isListView ->
            if (isListView) {
                setupListView()
                binding.ivWishlistPageSetLayoutView.setImageResource(R.drawable.ic_list_view)
            } else {
                setupGridView()
                binding.ivWishlistPageSetLayoutView.setImageResource(R.drawable.ic_grid_view)
            }
        }
    }

    private fun setupRecyclerView() {
        wishlistViewModel.isListView.value?.let { isListView ->
            if (isListView) {
                setupListView()
            } else {
                setupGridView()
            }
        }
    }

    private fun setupGridView() = with(binding.rvWishlistPage) {
        layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = gridAdapter
    }

    private fun setupListView() = with(binding.rvWishlistPage) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = listAdapter
    }

    private fun observeWishlistData(listWishlist: List<WishlistModel>) {
        gridAdapter.submitList(listWishlist)
        listAdapter.submitList(listWishlist)
    }

    private fun setupToolbar() {
        with(binding.wishlistPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_wishlist)
            ivToolbarArrowBackIcon.gone()
        }
    }

    private fun updateWishlistItemCount(count: Int) {
        binding.tvWishlistPageWishlistQuantity.text =
            getString(R.string.wishlist_quantity, count.toString())
    }
}
