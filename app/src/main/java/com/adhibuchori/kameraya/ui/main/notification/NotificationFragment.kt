package com.adhibuchori.kameraya.ui.main.notification

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.notification.NotificationModel
import com.adhibuchori.kameraya.R
import com.adhibuchori.kameraya.databinding.FragmentNotificationBinding
import com.adhibuchori.kameraya.ui.main.notification.adapter.ListItemNotificationAdapter
import com.adhibuchori.kameraya.utils.base.BaseFragment
import com.adhibuchori.kameraya.utils.extension.gone
import com.adhibuchori.kameraya.utils.extension.visible
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment :
    BaseFragment<FragmentNotificationBinding, ViewModel>(FragmentNotificationBinding::inflate) {

    private val notificationViewModel: NotificationViewModel by viewModel()
    private var notifications: List<NotificationModel> =
        emptyList()
    private val listAdapter = ListItemNotificationAdapter()

    override fun initViews() {
        observeNotificationData()
        setupToolbar()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        notificationViewModel.logScreenView(
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to SCREEN_NAME,
                FirebaseAnalytics.Param.SCREEN_CLASS to this::class.java.name,
            )
        )
    }

    private fun observeNotificationData() {
        notificationViewModel.notifications.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.pbNotificationPageProgressBar.visible()

                is Resource.Success -> {
                    resource.data.let { notificationsList ->
                        notifications = notificationsList
                        if (notificationsList.isEmpty()) showEmptyState() else showNotificationData()
                    }
                }

                is Resource.HttpError -> showHttpErrorState(resource.message)
                is Resource.NetworkError -> showNetworkErrorState()
                is Resource.Error -> showErrorState(resource.message)
                else -> Log.e("NotificationFragment", "Unexpected result type: $resource")

            }
        }
        notificationViewModel.fetchNotifications()
    }

    private fun showNotificationData() = with(binding) {
        pbNotificationPageProgressBar.gone()
        setupRecyclerView()
    }

    private fun showEmptyState() = with(binding) {
        pbNotificationPageProgressBar.gone()
        notificationPageState.root.visible()
        setupRecyclerView()
    }

    private fun showErrorState(message: String?) = with(binding) {
        pbNotificationPageProgressBar.gone()
        notificationPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_error_state)
            tvStateTitle.text = getString(R.string.error_state_title)
            tvStateDescription.text = getString(R.string.error_state_description)
        }
        notificationPageState.root.visible()
        Snackbar.make(root, "Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun showNetworkErrorState() = with(binding) {
        pbNotificationPageProgressBar.gone()
        notificationPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_network_error_state)
            tvStateTitle.text = getString(R.string.network_error_state_title)
            tvStateDescription.text = getString(R.string.network_error_state_description)
        }
        notificationPageState.root.visible()
        Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun showHttpErrorState(message: String?) = with(binding) {
        pbNotificationPageProgressBar.gone()
        notificationPageState.run {
            ivStateImage.setImageResource(R.drawable.iv_http_error_state)
            tvStateTitle.text = getString(R.string.http_error_state_title)
            tvStateDescription.text = getString(R.string.http_error_state_description)
        }
        notificationPageState.root.visible()
        Snackbar.make(root, "Http Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun setupToolbar() {
        with(binding.notificationPageToolbar) {
            tvToolbarTitle.text = getString(R.string.navigation_notification)
            ivToolbarArrowBackIcon.setOnClickListener {
                buttonClickEvent()
                val navController = Navigation.findNavController(it)
                navController.navigateUp()
            }
        }
    }

    private fun setupRecyclerView() = with(binding.rvNotificationPage) {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter.apply {
            submitList(notifications)
        }
    }

    private fun buttonClickEvent() {
        notificationViewModel.logButtonEvent(
            bundleOf(
                FirebaseConstant.Event.BUTTON_NAME to ARROW_BACK_ICON,
                FirebaseConstant.Event.SCREEN_NAME to SCREEN_NAME,
                FirebaseConstant.Event.EVENT_CATEGORY to EVENT_CATEGORY,
            )
        )
    }

    private companion object {
        const val SCREEN_NAME = "Notification"
        const val EVENT_CATEGORY = "Notification Fragment"
        const val ARROW_BACK_ICON = "Arrow Back Icon"
    }
}