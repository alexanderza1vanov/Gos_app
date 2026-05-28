package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.adapter.ApplicationAdapter
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.ApplicationResponse
import com.example.govservice.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplicationsFragment : Fragment(R.layout.fragment_my_applications) {

    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: ApplicationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val applicationsRecyclerView = view.findViewById<RecyclerView>(R.id.applicationsRecyclerView)

        adapter = ApplicationAdapter(emptyList())

        applicationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        applicationsRecyclerView.adapter = adapter

        loadApplications()
    }

    private fun loadApplications() {
        ApiClient.apiService.getMyApplications(
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
}