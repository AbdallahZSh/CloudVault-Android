package com.abdallahshabat.cloudvault.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.databinding.FragmentNotificationBinding
import com.abdallahshabat.cloudvault.ui.notifications.adapter.NotificationAdapter

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentNotificationBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter()

        binding.rvNotifications.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvNotifications.adapter =
            adapter

        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->

            adapter.submitList(notifications)

            if (notifications.isEmpty()) {

                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvNotifications.visibility = View.GONE

            } else {

                binding.layoutEmpty.visibility = View.GONE
                binding.rvNotifications.visibility = View.VISIBLE

            }

        }

        viewModel.loadNotifications()
        viewModel.markAllAsRead()

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}