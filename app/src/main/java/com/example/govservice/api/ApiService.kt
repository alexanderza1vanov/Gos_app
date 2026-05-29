package com.example.govservice.api

import com.example.govservice.dto.ChatMessageResponse
import com.example.govservice.dto.SendMessageRequest
import com.example.govservice.dto.ApplicationResponse
import com.example.govservice.dto.AuthResponse
import com.example.govservice.dto.CreateApplicationRequest
import com.example.govservice.dto.LoginRequest
import com.example.govservice.dto.MessageResponse
import com.example.govservice.dto.RegisterRequest
import com.example.govservice.dto.ServiceResponse
import com.example.govservice.dto.UpdateProfileRequest
import com.example.govservice.dto.UpdateStatusRequest
import com.example.govservice.dto.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>

    @GET("profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<UserResponse>

    @PATCH("profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<MessageResponse>

    @GET("services")
    fun getServices(
        @Header("Authorization") token: String
    ): Call<List<ServiceResponse>>

    @POST("applications")
    fun createApplication(
        @Header("Authorization") token: String,
        @Body request: CreateApplicationRequest
    ): Call<ApplicationResponse>

    @GET("applications/my")
    fun getMyApplications(
        @Header("Authorization") token: String
    ): Call<List<ApplicationResponse>>

    @GET("admin/applications")
    fun getAllApplications(
        @Header("Authorization") token: String
    ): Call<List<ApplicationResponse>>

    @GET("applications/{id}/messages")
    fun getMessages(
        @Header("Authorization") token: String,
        @Path("id") applicationId: Int
    ): Call<List<ChatMessageResponse>>

    @POST("applications/{id}/messages")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Path("id") applicationId: Int,
        @Body request: SendMessageRequest
    ): Call<ChatMessageResponse>

    @PATCH("applications/{id}/status")
    fun updateApplicationStatus(
        @Header("Authorization") token: String,
        @Path("id") applicationId: Int,
        @Body request: UpdateStatusRequest
    ): Call<MessageResponse>
}