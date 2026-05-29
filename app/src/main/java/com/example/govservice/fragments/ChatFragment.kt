package com.example.govservice.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.adapter.ChatMessageAdapter
import com.example.govservice.api.ApiClient
import com.example.govservice.dto.ChatMessageResponse
import com.example.govservice.dto.SendMessageRequest
import com.example.govservice.util.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: ChatMessageAdapter

    private var applicationId: Int = 0
    private var screenTitle: String = "Чат"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationId = arguments?.getInt(ARG_APPLICATION_ID) ?: 0
        screenTitle = arguments?.getString(ARG_TITLE) ?: "Чат"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenManager = TokenManager(requireContext())

        val titleTextView = view.findViewById<TextView>(R.id.chatTitleTextView)
        val messagesRecyclerView = view.findViewById<RecyclerView>(R.id.messagesRecyclerView)
        val messageEditText = view.findViewById<TextInputEditText>(R.id.messageEditText)
        val sendButton = view.findViewById<MaterialButton>(R.id.sendButton)
        val backButton = view.findViewById<MaterialButton>(R.id.backButton)

        titleTextView.text = screenTitle

        adapter = ChatMessageAdapter(
            items = emptyList(),
            currentUserRole = tokenManager.getUserRole()
        )

        messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        messagesRecyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        sendButton.setOnClickListener {
            val text = messageEditText.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Введите сообщение", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendMessage(text)
            messageEditText.setText("")
        }

        loadMessages()
    }

    private fun loadMessages() {
        ApiClient.apiService.getMessages(
            tokenManager.getBearerToken(),
            applicationId
        ).enqueue(object : Callback<List<ChatMessageResponse>> {
            override fun onResponse(
                call: Call<List<ChatMessageResponse>>,
                response: Response<List<ChatMessageResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateItems(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<ChatMessageResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки сообщений", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(text: String) {
        ApiClient.apiService.sendMessage(
            tokenManager.getBearerToken(),
            applicationId,
            SendMessageRequest(text)
        ).enqueue(object : Callback<ChatMessageResponse> {
            override fun onResponse(
                call: Call<ChatMessageResponse>,
                response: Response<ChatMessageResponse>
            ) {
                if (response.isSuccessful) {
                    loadMessages()
                } else {
                    Toast.makeText(requireContext(), "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChatMessageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка соединения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_APPLICATION_ID = "applicationId"
        private const val ARG_TITLE = "title"

        fun newInstance(applicationId: Int, title: String): ChatFragment {
            val fragment = ChatFragment()
            val bundle = Bundle()

            bundle.putInt(ARG_APPLICATION_ID, applicationId)
            bundle.putString(ARG_TITLE, title)

            fragment.arguments = bundle

            return fragment
        }
    }
}