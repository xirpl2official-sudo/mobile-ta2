package com.xirpl2.SASMobile.network

import com.xirpl2.SASMobile.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== Auth Endpoints ==========
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<String>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AkunLoginResponse>>

    @POST("auth/request-otp")
    suspend fun requestPasswordReset(
        @Body request: PasswordResetRequest
    ): Response<ApiResponse<String>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerificationRequest
    ): Response<ApiResponse<String>>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<String>>

    // ========== Beranda Endpoints ==========
    @GET("jadwal-sholat")
    suspend fun getJadwalSholat(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<JadwalSholatData>>>

    @GET("jadwal-sholat/{tanggal}")
    suspend fun getJadwalSholatByDate(
        @Header("Authorization") token: String,
        @Path("tanggal") tanggal: String
    ): Response<ApiResponse<List<JadwalSholatData>>>

    @GET("riwayat-absensi")
    suspend fun getRiwayatAbsensi(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<RiwayatAbsensiData>>>

    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserData>>

    @GET("statistik-absensi")
    suspend fun getStatistikAbsensi(
        @Header("Authorization") token: String,
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null
    ): Response<ApiResponse<StatistikData>>

    @POST("absensi")
    suspend fun submitAbsensi(
        @Header("Authorization") token: String,
        @Body absensiData: AbsensiRequest
    ): Response<ApiResponse<AbsensiResponse>>
}