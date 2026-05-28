package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.AuthResponse
import com.example.govservice.dto.LoginRequest
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<MaterialButton>(R.id.loginButton)
        val registerButton = view.findViewById<MaterialButton>(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        registerButton.setOnClickListener {
            (requireActivity() as MainActivity).openRegisterScreen()
        }
    }

    private fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        ApiClient.apiService.login(request).enqueue(object : Callback<AuthResponse> {
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
                    Toast.makeText(requireContext(), "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}