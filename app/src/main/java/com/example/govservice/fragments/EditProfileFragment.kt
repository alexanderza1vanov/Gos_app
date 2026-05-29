package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.govservice.R
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.MessageResponse
import com.example.govservice.dto.UpdateProfileRequest
import com.example.govservice.dto.UserResponse
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val fullNameEditText = view.findViewById<TextInputEditText>(R.id.fullNameEditText)
        val phoneEditText = view.findViewById<TextInputEditText>(R.id.phoneEditText)
        val birthDateEditText = view.findViewById<TextInputEditText>(R.id.birthDateEditText)
        val passportEditText = view.findViewById<TextInputEditText>(R.id.passportEditText)
        val addressEditText = view.findViewById<TextInputEditText>(R.id.addressEditText)

        val saveButton = view.findViewById<MaterialButton>(R.id.saveButton)
        val backButton = view.findViewById<MaterialButton>(R.id.backButton)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        loadProfile(
            fullNameEditText,
            phoneEditText,
            birthDateEditText,
            passportEditText,
            addressEditText
        )

        saveButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()
            val passport = passportEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()

            if (
                fullName.isEmpty() ||
                phone.isEmpty() ||
                birthDate.isEmpty() ||
                passport.isEmpty() ||
                address.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(fullName, phone, birthDate, passport, address)
        }
    }

    private fun loadProfile(
        fullNameEditText: TextInputEditText,
        phoneEditText: TextInputEditText,
        birthDateEditText: TextInputEditText,
        passportEditText: TextInputEditText,
        addressEditText: TextInputEditText
    ) {
        ApiClient.apiService.getProfile(
            tokenManager.getBearerToken()
        ).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val user = response.body()

                if (response.isSuccessful && user != null) {
                    fullNameEditText.setText(user.fullName)
                    phoneEditText.setText(user.phone)
                    birthDateEditText.setText(user.birthDate ?: "")
                    passportEditText.setText(user.passportData ?: "")
                    addressEditText.setText(user.address ?: "")
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile(
        fullName: String,
        phone: String,
        birthDate: String,
        passport: String,
        address: String
    ) {
        val request = UpdateProfileRequest(
            fullName = fullName,
            phone = phone,
            birthDate = birthDate,
            passportData = passport,
            address = address
        )

        ApiClient.apiService.updateProfile(
            tokenManager.getBearerToken(),
            request
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Профиль обновлён", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Ошибка сохранения профиля", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}