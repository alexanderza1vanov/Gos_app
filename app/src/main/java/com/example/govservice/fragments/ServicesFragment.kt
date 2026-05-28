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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val servicesRecyclerView = view.findViewById<RecyclerView>(R.id.servicesRecyclerView)
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

        myApplicationsButton.setOnClickListener {
            (requireActivity() as MainActivity).openMyApplicationsScreen()
        }

        profileButton.setOnClickListener {
            (requireActivity() as MainActivity).openProfileScreen()
        }

        loadServices()
    }

    private fun loadServices() {
        ApiClient.apiService.getServices(
            tokenManager.getBearerToken()
        ).enqueue(object : Callback<List<ServiceResponse>> {
            override fun onResponse(call: Call<List<ServiceResponse>>, response: Response<List<ServiceResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateItems(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки услуг", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ServiceResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}