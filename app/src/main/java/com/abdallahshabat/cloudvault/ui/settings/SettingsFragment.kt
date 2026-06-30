package com.abdallahshabat.cloudvault.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.databinding.FragmentSettingsBinding
import com.abdallahshabat.cloudvault.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener {
            /*SettingsFragment
                      │
                      ▼
                AuthViewModel
                     │
                     ▼
                AuthRepository
                    │
                    ▼
            FirebaseAuth.signOut()*/
            //يسجل خروج المستخدم من Firebase:
            FirebaseAuth.getInstance().signOut()
            //يحذف البيانات المحلية:
            TokenManager.clear(requireContext())
            //يفتح شاشة تسجيل الدخول:
            val intent = Intent(requireContext(), LoginActivity::class.java)
            //يمنع الرجوع إلى MainActivity بزر الرجوع باستخدام:
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            requireActivity().finish()

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}