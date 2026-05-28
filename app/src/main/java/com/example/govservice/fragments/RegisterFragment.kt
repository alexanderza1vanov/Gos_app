package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.AuthResponse
import com.example.govservice.dto.RegisterRequest
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val fullNameEditText = view.findViewById<TextInputEditText>(R.id.fullNameEditText)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val phoneEditText = view.findViewById<TextInputEditText>(R.id.phoneEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val roleSwitch = view.findViewById<MaterialSwitch>(R.id.roleSwitch)
        val roleTextView = view.findViewById<TextView>(R.id.roleTextView)
        val registerButton = view.findViewById<MaterialButton>(R.id.registerButton)
        val loginButton = view.findViewById<MaterialButton>(R.id.loginButton)

        roleSwitch.setOnCheckedChangeListener { _, isChecked ->
            roleTextView.text =
                if (isChecked) "Роль: сотрудник учреждения"
                else "Роль: заявитель"
        }

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = if (roleSwitch.isChecked) "EMPLOYEE" else "USER"

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(fullName, email, phone, password, role)
        }

        loginButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ) {
        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            phone = phone,
            password = password,
            role = role
        )

        ApiClient.apiService.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    tokenManager.saveAuthData(
                        token = body.token,
                        role = body.user.role,
                        fullName = body.user.fullName,
                        email = body.user.email
                    )

                    (requireActivity() as MainActivity).openStartScreen()
                } else {
                    Toast.makeText(requireContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}