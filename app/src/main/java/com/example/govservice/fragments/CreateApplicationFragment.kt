package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.ApplicationResponse
import com.example.govservice.dto.CreateApplicationRequest
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateApplicationFragment : Fragment(R.layout.fragment_create_application) {

    private lateinit var tokenManager: TokenManager

    private var serviceId: Int = 0
    private var serviceTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceId = arguments?.getInt(ARG_SERVICE_ID) ?: 0
        serviceTitle = arguments?.getString(ARG_SERVICE_TITLE) ?: "Государственная услуга"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val serviceTitleTextView = view.findViewById<TextView>(R.id.serviceTitleTextView)
        val fullNameEditText = view.findViewById<TextInputEditText>(R.id.fullNameEditText)
        val birthDateEditText = view.findViewById<TextInputEditText>(R.id.birthDateEditText)
        val passportEditText = view.findViewById<TextInputEditText>(R.id.passportEditText)
        val addressEditText = view.findViewById<TextInputEditText>(R.id.addressEditText)
        val purposeEditText = view.findViewById<TextInputEditText>(R.id.purposeEditText)
        val submitButton = view.findViewById<MaterialButton>(R.id.submitButton)

        serviceTitleTextView.text = serviceTitle

        submitButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()
            val passport = passportEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val purpose = purposeEditText.text.toString().trim()

            if (fullName.isEmpty() || birthDate.isEmpty() || passport.isEmpty() || address.isEmpty() || purpose.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createApplication(fullName, birthDate, passport, address, purpose)
        }
    }

    private fun createApplication(
        fullName: String,
        birthDate: String,
        passport: String,
        address: String,
        purpose: String
    ) {
        val request = CreateApplicationRequest(
            serviceId = serviceId,
            applicantFullName = fullName,
            birthDate = birthDate,
            passportData = passport,
            address = address,
            purpose = purpose
        )

        ApiClient.apiService.createApplication(
            tokenManager.getBearerToken(),
            request
        ).enqueue(object : Callback<ApplicationResponse> {
            override fun onResponse(call: Call<ApplicationResponse>, response: Response<ApplicationResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Заявление отправлено", Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainActivity).openMyApplicationsScreen()
                } else {
                    Toast.makeText(requireContext(), "Ошибка отправки заявления", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApplicationResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_SERVICE_ID = "serviceId"
        private const val ARG_SERVICE_TITLE = "serviceTitle"

        fun newInstance(serviceId: Int, serviceTitle: String): CreateApplicationFragment {
            val fragment = CreateApplicationFragment()
            val bundle = Bundle()

            bundle.putInt(ARG_SERVICE_ID, serviceId)
            bundle.putString(ARG_SERVICE_TITLE, serviceTitle)

            fragment.arguments = bundle

            return fragment
        }
    }
}