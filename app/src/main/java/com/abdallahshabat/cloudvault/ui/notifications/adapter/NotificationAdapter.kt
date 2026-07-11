package com.abdallahshabat.cloudvault.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.utils.TimeFormatter
import com.abdallahshabat.cloudvault.data.model.NotificationItem
import com.abdallahshabat.cloudvault.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter :
    ListAdapter<NotificationItem, NotificationAdapter.ViewHolder>(Diff()) {

    inner class ViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {

            binding.tvMessage.text = item.message

            binding.tvDate.text =
                TimeFormatter.getTimeAgo(
                    item.createdAt
                )

            binding.tvTitle.text = item.title
            binding.tvMessage.text = item.message

            binding.ivType.setImageResource(

                when (item.type) {

                    "upload" -> R.drawable.ic_upload

                    "favorite" -> R.drawable.ic_favorite

                    "delete" -> R.drawable.ic_delete

                    "rename" -> R.drawable.ic_edit

                    "download" -> R.drawable.ic_download

                    else -> R.drawable.ic_notifications

                }

            )
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemNotificationBinding.inflate(

                LayoutInflater.from(parent.context),

                parent,

                false

            )

        )

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }

    class Diff : DiffUtil.ItemCallback<NotificationItem>() {

        override fun areItemsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ) =
            oldItem == newItem

    }

}