package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.MainActivity
import com.example.govservice.R
import com.example.govservice.adapter.EmployeeApplicationAdapter
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.ApplicationResponse
import com.example.govservice.dto.MessageResponse
import com.example.govservice.dto.UpdateStatusRequest
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeApplicationsFragment : Fragment(R.layout.fragment_employee_applications) {

    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: EmployeeApplicationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val applicationsRecyclerView = view.findViewById<RecyclerView>(R.id.employeeApplicationsRecyclerView)
        val profileButton = view.findViewById<MaterialButton>(R.id.profileButton)

        adapter = EmployeeApplicationAdapter(
            items = emptyList(),
            onStatusClick = { application, status ->
                updateStatus(application.id, status)
            }
        )

        applicationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        applicationsRecyclerView.adapter = adapter

        profileButton.setOnClickListener {
            (requireActivity() as MainActivity).openProfileScreen()
        }

        loadApplications()
    }

    private fun loadApplications() {
        ApiClient.apiService.getAllApplications(
            tokenManager.getBearerToken()
        ).enqueue(object : Callback<List<ApplicationResponse>> {
            override fun onResponse(
                call: Call<List<ApplicationResponse>>,
                response: Response<List<ApplicationResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateItems(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки заявлений", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ApplicationResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStatus(applicationId: Int, status: String) {
        val comment = when (status) {
            "IN_PROGRESS" -> "Заявление принято в работу"
            "APPROVED" -> "Заявление рассмотрено и одобрено"
            "REJECTED" -> "В предоставлении услуги отказано"
            else -> null
        }

        val request = UpdateStatusRequest(
            status = status,
            employeeComment = comment
        )

        ApiClient.apiService.updateApplicationStatus(
            tokenManager.getBearerToken(),
            applicationId,
            request
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Статус обновлён", Toast.LENGTH_SHORT).show()
                    loadApplications()
                } else {
                    Toast.makeText(requireContext(), "Ошибка обновления статуса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}