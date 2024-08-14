package com.adhibuchori.kameraya.ui.main.transaction.cart

import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentCartBinding
import com.adhibuchori.kameraya.ui.main.transaction.cart.adapter.ListItemCartAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CartFragment : BaseFragment<FragmentCartBinding, ViewModel>(FragmentCartBinding::inflate) {

    private val cartViewModel: CartViewModel by viewModel()

    private val listAdapter: ListItemCartAdapter by lazy {
        ListItemCartAdapter().apply {
            setOnItemClickListener { cartItem ->
                val action =
                    CartFragmentDirections.actionCartFragmentToProductDetailFragment(cartItem.productId)
                Navigation.findNavController(requireActivity(), R.id.fcv_container).navigate(action)
            }
            setOnDeleteItemListener { cartItem ->
                cartViewModel.removeCartItem(cartItem)
            }
            setOnIncreaseListener { cartItem ->
                cartViewModel.increaseCartQuantity(cartItem)
            }
            setOnDecreaseListener { cartItem ->
                cartViewModel.decreaseCartQuantity(cartItem)
            }
            setOnCheckboxChangedListener { cartItem, isChecked ->
                cartViewModel.run {
                    handleOnItemChecked(isChecked, cartItem)
                }
            }
        }
    }

    override fun initViews() {
        setupToolbar()
        setupNavigation()
        setupRecyclerView()
        observeCartItems()
    }

    private fun setupToolbar() {
        with(binding.cartPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_cart)
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }

    private fun setupNavigation() {
        with(binding) {
            btnCartPageCheckout.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(R.id.action_cartFragment_to_checkoutFragment)
            }
        }
    }

    private fun observeCartItems() {
        cartViewModel.cartItems.launchAndCollectIn(viewLifecycleOwner) { observeCartData(it) }
    }

    private fun observeCartData(listCart: List<CartModel>) {
        listAdapter.submitList(listCart)
        updateTotalPayment(listCart)

        binding.mcbCartPageSelectAllProduct.run {
            isChecked = cartViewModel.isAllItemSelected(listCart)

            setOnCheckedChangeListener { _, isChecked ->
                if (cartViewModel.isAllItemSelectEligible) {
                    cartViewModel.run {
                        handleSelectAllItem(isChecked)
                        isAllItemSelectEligible = true
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() = with(binding.rvCartPageProduct) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = listAdapter
    }


    private fun updateTotalPayment(listCart: List<CartModel>) {
        val totalAmount = cartViewModel.calculateSelectedItems(listCart)
        binding.tvCartPageTotalPaymentData.text = formatPrice(totalAmount)
    }

    private fun formatPrice(price: Int): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            currencySymbol = "Rp"
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        return "Rp${decimalFormat.format(price)}"
    }
}