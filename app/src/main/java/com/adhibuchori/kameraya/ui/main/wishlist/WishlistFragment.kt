package com.adhibuchori.kameraya.ui.main.wishlist

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.wishlist.WishlistModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentWishlistBinding
import com.adhibuchori.kameraya.ui.main.MainFragmentDirections
import com.adhibuchori.kameraya.ui.main.payment.cart.CartViewModel
import com.adhibuchori.kameraya.ui.main.wishlist.adapter.GridItemWishlistAdapter
import com.adhibuchori.kameraya.ui.main.wishlist.adapter.ListItemWishlistAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.orEmpty
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class WishlistFragment :
    BaseFragment<FragmentWishlistBinding, ViewModel>(FragmentWishlistBinding::inflate) {

    private val wishlistViewModel: WishlistViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()

    private val listAdapter: ListItemWishlistAdapter by lazy {
        ListItemWishlistAdapter().apply {
            setOnItemClickListener { wishlistItem ->
                selectItemEvent(wishlistItem)
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(wishlistItem.productId.orEmpty())
                activity?.let { activity ->
                    Navigation.findNavController(activity, R.id.fcv_container)
                        .navigate(action)
                }
            }
            setOnDeleteItemListener { wishlistItem ->
                buttonClickEvent(BUTTON_REMOVE_WISHLIST_ITEM)
                wishlistViewModel.removeWishlistItem(wishlistItem)
                Toast.makeText(context, getString(R.string.wishlist_removed), Toast.LENGTH_SHORT)
                    .show()
            }

            setOnAddCartItemListener { wishlistItem ->
                buttonClickEvent(BUTTON_ADD_TO_CART)
                cartViewModel.addCartItem(wishlistItem)
                Toast.makeText(context, getString(R.string.added_to_cart), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val gridAdapter: GridItemWishlistAdapter by lazy {
        GridItemWishlistAdapter().apply {
            setOnItemClickListener { wishlistItem ->
                selectItemEvent(wishlistItem)
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailFragment(wishlistItem.productId.orEmpty())
                activity?.let { activity ->
                    Navigation.findNavController(activity, R.id.fcv_container)
                        .navigate(action)
                }
            }
            setOnDeleteItemListener { wishlistItem ->
                buttonClickEvent(BUTTON_REMOVE_WISHLIST_ITEM)
                wishlistViewModel.removeWishlistItem(wishlistItem)
            }
            setOnAddCartItemListener { wishlistItem ->
                buttonClickEvent(BUTTON_ADD_TO_CART)
                cartViewModel.addCartItem(wishlistItem)
                Toast.makeText(context, getString(R.string.added_to_cart), Toast.LENGTH_SHORT)
                    .show()
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

    override fun onResume() {
        super.onResume()
        wishlistViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupViews() {
        binding.run {
            ivWishlistPageSetLayoutView.setOnClickListener {
                buttonClickEvent(SET_LAYOUT_ICON)
                wishlistViewModel.toggleRecyclerViewLayout()
            }
        }
    }

    private fun observeWishlistItems() {
        wishlistViewModel.wishlistItems.launchAndCollectIn(viewLifecycleOwner) { wishlistItems ->
            observeWishlistData(wishlistItems)
            updateWishlistItemCount(wishlistItems.size)
            if (wishlistItems.isEmpty()) showEmptyState() else showWishlistItems()
        }
    }

    private fun showEmptyState() = with(binding) {
        wishlistPageState.root.visible()
        rvWishlistPage.gone()
    }

    private fun showWishlistItems() = with(binding) {
        wishlistPageState.root.gone()
        rvWishlistPage.visible()
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
        layoutManager = GridLayoutManager(context, 2)
        adapter = gridAdapter
    }

    private fun setupListView() = with(binding.rvWishlistPage) {
        layoutManager = LinearLayoutManager(context)
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

    private fun buttonClickEvent(buttonName: String) {
        wishlistViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private fun selectItemEvent(wishlist: WishlistModel) {
        val bundle = bundleOf(
            ITEM_NAME to wishlist.productName,
            ITEM_VARIANT to wishlist.productVariant,
            ITEM_PRICE to wishlist.productPrice,
            ITEM_STORE to wishlist.productStore
        )
        wishlistViewModel.logSelectItem(bundle)
    }

    private companion object {
        const val SCREEN_NAME = "Wishlist"
        const val EVENT_CATEGORY = "Wishlist Fragment"

        const val BUTTON_REMOVE_WISHLIST_ITEM = "Button Remove Wishlist Item"
        const val BUTTON_ADD_TO_CART = "Button Add to Cart"
        const val SET_LAYOUT_ICON = "Set Layout Icon"

        const val ITEM_NAME = "item_name"
        const val ITEM_VARIANT = "item_variant"
        const val ITEM_PRICE = "item_price"
        const val ITEM_STORE = "item_store"
    }
}
