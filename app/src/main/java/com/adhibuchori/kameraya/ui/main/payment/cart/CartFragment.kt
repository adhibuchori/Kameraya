package com.adhibuchori.kameraya.ui.main.payment.cart

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.payment.cart.CartModel
import com.adhibuchori.domain.payment.checkout.CheckoutItem
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentCartBinding
import com.adhibuchori.kameraya.ui.main.payment.cart.adapter.ListItemCartAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : BaseFragment<FragmentCartBinding, ViewModel>(FragmentCartBinding::inflate) {

    private val cartViewModel: CartViewModel by viewModel()

    private val listAdapter: ListItemCartAdapter by lazy {
        ListItemCartAdapter().apply {
            cartViewModel.run {
                setOnItemClickListener { cartItem ->
                    selectItemEvent(cartItem)
                    activity?.let { activity ->
                        val action =
                            CartFragmentDirections.actionCartFragmentToProductDetailFragment(
                                cartItem.productId
                            )
                        Navigation.findNavController(activity, R.id.fcv_container).navigate(action)
                    }
                }
                setOnDeleteItemListener { cartItem ->
                    buttonClickEvent(DELETE_ITEM_ICON)
                    removeCartItem(cartItem)
                }
                setOnIncreaseListener { cartItem ->
                    buttonClickEvent(INCREASE_ICON)
                    increaseCartQuantity(cartItem)
                }
                setOnDecreaseListener { cartItem ->
                    buttonClickEvent(DECREASE_ICON)
                    decreaseCartQuantity(cartItem)
                }
                setOnCheckboxChangedListener { cartItem, isChecked ->
                    handleOnItemChecked(isChecked, cartItem)
                }
            }
        }
    }

    override fun initViews() {
        setupToolbar()
        setupRecyclerView()
        setupCheckoutNavigation()
        setupSelectAllCheckboxListener()
        setupDeleteButtonListener()
        observeCartItems()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupToolbar() {
        with(binding.cartPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_cart)
            ivToolbarArrowBackIcon.setOnClickListener {
                buttonClickEvent(ARROW_BACK_ICON)
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }

    private fun setupCheckoutNavigation() {
        with(binding) {
            btnCartPageCheckout.setOnClickListener {
                buttonClickEvent(BUTTON_CHECKOUT)
                val selectedItems = cartViewModel.cartItems.value.filter { it.isChecked }
                val checkoutItem =
                    CheckoutItem(cartItems = selectedItems.toList())
                val bundle = Bundle().apply {
                    putParcelable(CHECKOUT_ITEM, checkoutItem)
                }
                if (selectedItems.isNotEmpty()) {
                    findNavController().navigate(
                        R.id.action_cartFragment_to_checkoutFragment,
                        bundle
                    )
                } else {
                    Snackbar.make(
                        root,
                        getString(R.string.snackbar_select_item),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun observeCartItems() {
        cartViewModel.cartItems.launchAndCollectIn(viewLifecycleOwner) { cartItems ->
            observeCartData(cartItems)
            if (cartItems.isEmpty()) showEmptyState() else showCartItems()
        }
    }

    private fun showEmptyState() = with(binding) {
        cartPageState.root.visible()
        rvCartPageProduct.gone()
    }

    private fun showCartItems() = with(binding) {
        cartPageState.root.gone()
        rvCartPageProduct.visible()
    }

    private fun observeCartData(listCart: List<CartModel>) {
        listAdapter.submitList(listCart)
        updateTotalPayment(listCart)
        updateSelectAllCheckbox(listCart)
    }

    private fun setupDeleteButtonListener() {
        binding.tvCartPageDeleteProduct.setOnClickListener {
            buttonClickEvent(DELETE_CHECKOUT_ITEM_SELECTED)
            cartViewModel.removeSelectedItems()
        }
    }

    private fun setupSelectAllCheckboxListener() {
        binding.mcbCartPageSelectAllProduct.setOnCheckedChangeListener { _, isChecked ->
            if (cartViewModel.isAllItemSelectEligible) {
                cartViewModel.run {
                    handleSelectAllItem(isChecked)
                    isAllItemSelectEligible = false
                }
            }
        }
    }

    private fun updateSelectAllCheckbox(listCart: List<CartModel>) {
        binding.mcbCartPageSelectAllProduct.run {
            cartViewModel.isAllItemSelectEligible = false
            isChecked = cartViewModel.isAllItemSelected(listCart) && listCart.isNotEmpty()
            cartViewModel.isAllItemSelectEligible = true
        }

        val selectedItemCount = cartViewModel.getSelectedItemCount(listCart)
        binding.tvCartPageSelectAllProduct.text = if (selectedItemCount > 0) {
            getString(R.string.items_selected, selectedItemCount)
        } else {
            getString(R.string.select_all_product)
        }
    }

    private fun setupRecyclerView() = with(binding.rvCartPageProduct) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter
    }


    private fun updateTotalPayment(listCart: List<CartModel>) {
        val totalAmount = cartViewModel.calculateSelectedItems(listCart)
        binding.tvCartPageTotalPaymentData.text = totalAmount.formatPrice()
    }

    private fun selectItemEvent(cart: CartModel) {
        val bundle = bundleOf(
            ITEM_NAME to cart.productName,
            ITEM_VARIANT to cart.productVariant,
            ITEM_PRICE to cart.productVariant,
            ITEM_STOCK to cart.productStock
        )
        cartViewModel.logSelectItem(bundle)
    }

    private fun buttonClickEvent(buttonName: String) {
        cartViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val CHECKOUT_ITEM = "checkoutItem"

        const val SCREEN_NAME = "Cart"
        const val EVENT_CATEGORY = "Cart Fragment"

        const val BUTTON_CHECKOUT = "Button Checkout"
        const val DELETE_CHECKOUT_ITEM_SELECTED = "Delete Checkout Item Selected"
        const val DECREASE_ICON = "Decrease Icon"
        const val INCREASE_ICON = "Increase Icon"
        const val DELETE_ITEM_ICON = "Delete Item Icon"
        const val ARROW_BACK_ICON = "Arrow Back Icon"

        const val ITEM_NAME = "item_name"
        const val ITEM_VARIANT = "item_variant"
        const val ITEM_STOCK = "item_stock"
        const val ITEM_PRICE = "item_price"
    }
}