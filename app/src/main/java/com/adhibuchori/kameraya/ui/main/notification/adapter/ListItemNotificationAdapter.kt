package com.adhibuchori.kameraya.ui.main.notification.adapter

import android.view.View
import com.adhibuchori.kameraya.databinding.ItemRowNotificationBinding
import com.adhibuchori.kameraya.utils.base.list.BaseListAdapter

class ListItemNotificationAdapter :
    BaseListAdapter<com.adhibuchori.domain.notification.NotificationModel, ItemRowNotificationBinding>(
        ItemRowNotificationBinding::inflate
    ) {
    override fun onItemBind(): (com.adhibuchori.domain.notification.NotificationModel, ItemRowNotificationBinding, View, Int) -> Unit =
        { data, binding, _, _ ->
            with(binding) {
                tvRowNotificationStatus.text = data.notificationStatus
                tvRowNotificationDescription.text = data.notificationDescription
            }
        }
}