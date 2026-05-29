package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.adapter.ServiceAdapter
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.ServiceResponse
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicesFragment : Fragment(R.layout.fragment_services) {

    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: ServiceAdapter

    private var allServices: List<ServiceResponse> = emptyList()
    private var selectedCategory: String = "Справки"

    private var servicesCall: Call<List<ServiceResponse>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val servicesRecyclerView = view.findViewById<RecyclerView>(R.id.servicesRecyclerView)

        val certificatesButton = view.findViewById<MaterialButton>(R.id.certificatesButton)
        val socialButton = view.findViewById<MaterialButton>(R.id.socialButton)
        val appointmentButton = view.findViewById<MaterialButton>(R.id.appointmentButton)

        val myApplicationsButton = view.findViewById<MaterialButton>(R.id.myApplicationsButton)
        val profileButton = view.findViewById<MaterialButton>(R.id.profileButton)

        adapter = ServiceAdapter(emptyList()) { service ->
            (requireActivity() as MainActivity).openCreateApplicationScreen(
                serviceId = service.id,
                serviceTitle = service.title
            )
        }

        servicesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        servicesRecyclerView.adapter = adapter

        certificatesButton.setOnClickListener {
            selectedCategory = "Справки"
            updateCategoryButtons(certificatesButton, socialButton, appointmentButton)
            filterServices()
        }

        socialButton.setOnClickListener {
            selectedCategory = "Социальные услуги"
            updateCategoryButtons(socialButton, certificatesButton, appointmentButton)
            filterServices()
        }

        appointmentButton.setOnClickListener {
            selectedCategory = "Запись на приём"
            updateCategoryButtons(appointmentButton, certificatesButton, socialButton)
            filterServices()
        }

        myApplicationsButton.setOnClickListener {
            (requireActivity() as MainActivity).openMyApplicationsScreen()
        }

        profileButton.setOnClickListener {
            (requireActivity() as MainActivity).openProfileScreen()
        }

        updateCategoryButtons(certificatesButton, socialButton, appointmentButton)
        loadServices()
    }

    override fun onDestroyView() {
        servicesCall?.cancel()
        servicesCall = null
        super.onDestroyView()
    }

    private fun loadServices() {
        servicesCall?.cancel()

        servicesCall = ApiClient.apiService.getServices(
            tokenManager.getBearerToken()
        )

        servicesCall?.enqueue(object : Callback<List<ServiceResponse>> {
            override fun onResponse(
                call: Call<List<ServiceResponse>>,
                response: Response<List<ServiceResponse>>
            ) {
                if (!isAdded || !tokenManager.isLoggedIn()) return

                if (response.isSuccessful && response.body() != null) {
                    allServices = response.body()!!
                    filterServices()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки услуг",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ServiceResponse>>, t: Throwable) {
                if (call.isCanceled || !isAdded || !tokenManager.isLoggedIn()) return

                Toast.makeText(
                    requireContext(),
                    "Ошибка соединения: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterServices() {
        val filtered = allServices.filter { it.category == selectedCategory }
        adapter.updateItems(filtered)
    }

    private fun updateCategoryButtons(
        activeButton: MaterialButton,
        button2: MaterialButton,
        button3: MaterialButton
    ) {
        activeButton.setBackgroundColor(resources.getColor(R.color.primary_purple, null))
        activeButton.setTextColor(resources.getColor(R.color.white, null))

        button2.setBackgroundColor(resources.getColor(R.color.white, null))
        button2.setTextColor(resources.getColor(R.color.primary_purple, null))

        button3.setBackgroundColor(resources.getColor(R.color.white, null))
        button3.setTextColor(resources.getColor(R.color.primary_purple, null))
    }
}