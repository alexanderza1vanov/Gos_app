package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.UserResponse
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var tokenManager: TokenManager
    private var profileCall: Call<UserResponse>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val avatarTextView = view.findViewById<TextView>(R.id.avatarTextView)
        val fullNameTextView = view.findViewById<TextView>(R.id.fullNameTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val phoneTextView = view.findViewById<TextView>(R.id.phoneTextView)
        val roleTextView = view.findViewById<TextView>(R.id.roleTextView)

        val smallBackButton = view.findViewById<MaterialButton>(R.id.smallBackButton)
        val editProfileButton = view.findViewById<MaterialButton>(R.id.editProfileButton)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logoutButton)

        smallBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        editProfileButton.setOnClickListener {
            (requireActivity() as MainActivity).openEditProfileScreen()
        }

        logoutButton.setOnClickListener {
            profileCall?.cancel()
            tokenManager.clearToken()
            (requireActivity() as MainActivity).openLoginScreen()
        }

        profileCall = ApiClient.apiService.getProfile(
            tokenManager.getBearerToken()
        )

        profileCall?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (!isAdded || !tokenManager.isLoggedIn()) return

                val user = response.body()

                if (response.isSuccessful && user != null) {
                    fullNameTextView.text = user.fullName
                    emailTextView.text = user.email
                    phoneTextView.text = user.phone
                    roleTextView.text = translateRole(user.role)
                    avatarTextView.text = getInitials(user.fullName)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                if (call.isCanceled || !isAdded || !tokenManager.isLoggedIn()) return

                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        profileCall?.cancel()
        profileCall = null
        super.onDestroyView()
    }

    private fun translateRole(role: String): String {
        return when (role) {
            "USER" -> "Заявитель"
            "EMPLOYEE" -> "Сотрудник учреждения"
            else -> role
        }
    }

    private fun getInitials(fullName: String): String {
        val parts = fullName.trim().split(" ")

        return if (parts.size >= 2) {
            "${parts[0].first()}${parts[1].first()}".uppercase()
        } else {
            fullName.take(2).uppercase()
        }
    }
}