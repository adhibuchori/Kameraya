package com.adhibuchori.kameraya.ui.main.payment.checkout

import android.content.res.Resources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.checkout.CheckoutModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentParameter
import com.adhibuchori.domain.payment.paymentMethod.PaymentMethodItemModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentCheckoutBinding
import com.adhibuchori.kameraya.databinding.ItemBottomSheetPaymentMethodBinding
import com.adhibuchori.kameraya.ui.main.payment.checkout.adapter.ListItemCheckoutAdapter
import com.adhibuchori.kameraya.ui.main.payment.checkout.adapter.ListItemPaymentMethodAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.formatPrice
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.launchAndCollectIn
import com.adhibuchori.kameraya.utils.notification.KamerayaNotification
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutFragment :
    BaseFragment<FragmentCheckoutBinding, ViewModel>(FragmentCheckoutBinding::inflate) {

    private val checkoutViewModel: CheckoutViewModel by viewModel()
    private val args: CheckoutFragmentArgs by navArgs()

    private var selectedPaymentMethod: PaymentMethodItemModel? = null

    private val checkoutAdapter: ListItemCheckoutAdapter by lazy {
        ListItemCheckoutAdapter()
    }

    private val virtualBankAdapter: ListItemPaymentMethodAdapter by lazy {
        ListItemPaymentMethodAdapter().apply {
            setOnItemClickListener {
                selectItemEvent(it)
                bottomSheetDialog?.dismiss()
                selectPayment(it)
            }
        }
    }
    private val transferBankAdapter: ListItemPaymentMethodAdapter by lazy {
        ListItemPaymentMethodAdapter().apply {
            setOnItemClickListener {
                selectItemEvent(it)
                bottomSheetDialog?.dismiss()
                selectPayment(it)
            }
        }
    }
    private val instantPaymentAdapter: ListItemPaymentMethodAdapter by lazy {
        ListItemPaymentMethodAdapter().apply {
            setOnItemClickListener {
                selectItemEvent(it)
                bottomSheetDialog?.dismiss()
                selectPayment(it)
            }
        }
    }

    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var bottomSheetBinding: ItemBottomSheetPaymentMethodBinding

    override fun initViews() {
        setupToolbar()
        setupNavigation()
        setupRecyclerView()
        observeFulfillmentData()
        observePaymentMethodData()
        displaySelectedItems()
    }

    override fun onResume() {
        super.onResume()
        checkoutViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun setupRecyclerView() = with(binding.rvCheckoutPage) {
        layoutManager = LinearLayoutManager(context)
        adapter = checkoutAdapter
    }

    private fun displaySelectedItems() {
        val checkoutItem = args.checkoutItem
        val cartItems = checkoutItem.cartItems?.toList() ?: emptyList()
        checkoutItem.cartItems?.toList() ?: emptyList()

        val checkoutItems = checkoutViewModel.mapToCheckoutModel(cartItems)

        checkoutAdapter.submitList(checkoutItems)
        updateTotalPayment(checkoutItems)
        setupCountItemPurchased(checkoutItems)
    }

    private fun setupCountItemPurchased(selectedItems: List<CheckoutModel>) {
        val uniqueProductIds = selectedItems.map { it.productId to it.productVariant }.toSet()
        val totalUniqueProducts = uniqueProductIds.size
        binding.tvCheckoutPageItemPurchasedTitle.text =
            getString(R.string.item_purchased_title, totalUniqueProducts)
    }

    private fun updateTotalPayment(items: List<CheckoutModel>) {
        val totalAmount = items.sumOf { it.productCount * it.productPrice }
        binding.tvCheckoutPageTotalPaymentData.text = totalAmount.formatPrice()
    }

    private fun observeFulfillmentData() {
        checkoutViewModel.fulfillmentData.launchAndCollectIn(viewLifecycleOwner) {
            if (it is Resource.Success) {
                val fulfillmentModelData = it.data
                val action =
                    CheckoutFragmentDirections.actionCheckoutFragmentToPaymentStatusFragment(
                        fulfillmentModelData
                    )
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.productDetailFragment, true).build()
                Navigation.findNavController(requireActivity(), R.id.fcv_container)
                    .navigate(action, navOptions)

                initNotification(fulfillmentModelData)
            }
        }
    }

    private fun observePaymentMethodData() {
        with(checkoutViewModel) {
            paymentList.observe(viewLifecycleOwner) { it ->
                if (it is Resource.Success) {
                    val virtualAccount = it.data.find { it.title == VIRTUAL_ACCOUNT_TRANSFER }
                    val transferBank = it.data.find { it.title == BANK_TRANSFER }
                    val instantPayment = it.data.find { it.title == INSTANT_PAYMENT }

                    virtualAccount?.item?.let { item -> virtualBankAdapter.submitList(item) }
                    transferBank?.item?.let { item -> transferBankAdapter.submitList(item) }
                    instantPayment?.item?.let { item -> instantPaymentAdapter.submitList(item) }
                }
            }
        }
    }

    private fun selectPayment(item: PaymentMethodItemModel) {
        selectedPaymentMethod = item
        with(binding) {
            binding.tvCheckoutPageChoosePaymentMethod.text = item.label
            ivCheckoutPagePaymentMethodIcon.apply {
                val lp = layoutParams as ConstraintLayout.LayoutParams
                lp.apply {
                    width = convertDpToPixel(48)
                    height = convertDpToPixel(15)
                }
                layoutParams = lp
            }
            Glide.with(root.context)
                .load(item.image)
                .into(ivCheckoutPagePaymentMethodIcon)
        }
    }

    private fun convertDpToPixel(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun setupNavigation() {
        with(binding) {
            btnCheckoutPageBuy.setOnClickListener {
                buttonClickEvent(BUTTON_BUY)
                if (selectedPaymentMethod == null) {
                    view?.let { view ->
                        Snackbar.make(
                            view,
                            getString(R.string.snackbar_choose_payment_method),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    return@setOnClickListener
                }
                val checkoutItem = args.checkoutItem
                val cartItems = checkoutItem.cartItems?.toList() ?: emptyList()
                checkoutViewModel.run {
                    val fulfillmentParameters = mapToFulfillmentParameter(cartItems)
                    fulfillTransaction(
                        FulfillmentParameter(
                            payment = selectedPaymentMethod?.label.orEmpty(),
                            items = fulfillmentParameters
                        )
                    )
                }
            }
            cvCheckoutPageChoosePaymentMethodContainer.setOnClickListener {
                buttonClickEvent(CHOOSE_PAYMENT_METHOD_NAVIGATION)
                showPaymentMethodBottomSheet()
            }
        }
    }

    private fun initNotification(data: FulfillmentModel) {
        val notificationModelData = checkoutViewModel.getNotificationModelData(data)
        context?.let {
            val pendingIntent = NavDeepLinkBuilder(it)
                .setGraph(R.navigation.nav_graphs)
                .setDestination(R.id.notificationFragment)
                .createPendingIntent()

            KamerayaNotification(context = it).invoke(
                title = notificationModelData.notificationStatus.orEmpty(),
                description = notificationModelData.notificationDescription.orEmpty(),
                pendingIntent = pendingIntent
            )
        }
    }

    private fun showPaymentMethodBottomSheet() {
        initPaymentMethodBottomSheet()

        bottomSheetDialog?.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.ivBottomSheetCloseIcon.setOnClickListener {
            buttonClickEvent(CHECKOUT_CLOSE_ICON)
            bottomSheetDialog?.dismiss()
        }

        setupPaymentMethodsRecyclerView(bottomSheetBinding)

        bottomSheetDialog?.show()
    }

    private fun initPaymentMethodBottomSheet() {
        bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        bottomSheetBinding = ItemBottomSheetPaymentMethodBinding.inflate(layoutInflater)
    }

    private fun setupPaymentMethodsRecyclerView(bottomSheetBinding: ItemBottomSheetPaymentMethodBinding) {
        with(bottomSheetBinding) {
            rvBottomSheetVirtualAccount.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = virtualBankAdapter
            }
            rvBottomSheetTransferBank.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = transferBankAdapter
            }
            rvBottomSheetInstantPayment.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = instantPaymentAdapter
            }
        }
    }

    private fun setupToolbar() {
        with(binding.checkoutPageToolbar) {
            buttonClickEvent(ARROW_BACK_ICON)
            tvToolbarTitle.text = getString(R.string.checkout_navigation)
            ivToolbarArrowBackIcon.setOnClickListener {
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }

    private fun selectItemEvent(paymentMethodItemModel: PaymentMethodItemModel) {
        val bundle = bundleOf(
            ITEM_PAYMENT_METHOD_NAME to paymentMethodItemModel.label
        )
        checkoutViewModel.logSelectItem(bundle)
    }

    private fun buttonClickEvent(buttonName: String) {
        checkoutViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to buttonName,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val VIRTUAL_ACCOUNT_TRANSFER = "Transfer Virtual Account"
        const val BANK_TRANSFER = "Transfer Bank"
        const val INSTANT_PAYMENT = "Pembayaran Instan"

        const val SCREEN_NAME = "Checkout"
        const val EVENT_CATEGORY = "Checkout Fragment"

        const val BUTTON_BUY = "Button Buy"
        const val ARROW_BACK_ICON = "Arrow Back Icon"
        const val CHOOSE_PAYMENT_METHOD_NAVIGATION = "Choose Payment Method Navigation"
        const val CHECKOUT_CLOSE_ICON = "Close Icon"

        const val ITEM_PAYMENT_METHOD_NAME = "item_payment_method_name"
    }
}