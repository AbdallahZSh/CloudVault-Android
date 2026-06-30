package com.abdallahshabat.cloudvault.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        val user = TokenManager.getUser(requireContext())
        binding.tvUserName.text = user?.fullName ?: "مرحباً"
        binding.tvUserInitial.text = user?.email?.first()?.toString() ?: "أ"

        // Storage bar
        val used = user?.storageUsed ?: 0L
        val total = user?.storageTotal ?: 10L
        val percent = ((used.toFloat() / total) * 100).toInt()
        binding.storageProgress.progress = percent
        binding.tvStorageInfo.text = "${formatSize(used)} من ${formatSize(total)}"

        // Quick actions
        binding.cardUpload.setOnClickListener {
            parentFragmentManager.let {
                requireActivity().findViewById<BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.uploadFragment
            }
        }
    }

    private fun observeData() {
        viewModel.recentFiles.observe(viewLifecycleOwner) { files ->
            // سنربطها بالـ RecyclerView لاحقاً
        }
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            else -> "%.1f KB".format(bytes / 1_024.0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}