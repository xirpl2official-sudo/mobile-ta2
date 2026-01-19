package com.xirpl2.SASMobile.repository

import com.xirpl2.SASMobile.model.*
import com.xirpl2.SASMobile.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository untuk mengelola data Beranda dari API
 * Menggunakan coroutines untuk async operations
 */
class BerandaRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Get jadwal sholat dari API
     * @param token Auth token user
     * @return Result dengan data atau error
     */
    suspend fun getJadwalSholat(token: String): Result<List<JadwalSholatData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<List<JadwalSholatData>>> =
                    apiService.getJadwalSholat("Bearer $token")

                handleApiResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get riwayat absensi dari API
     */
    suspend fun getRiwayatAbsensi(token: String, limit: Int = 10): Result<List<RiwayatAbsensiData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<List<RiwayatAbsensiData>>> =
                    apiService.getRiwayatAbsensi("Bearer $token", limit)

                handleApiResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get user profile (untuk mendapatkan jenis kelamin)
     */
    suspend fun getUserProfile(token: String): Result<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<UserData>> =
                    apiService.getUserProfile("Bearer $token")

                handleApiResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get statistik absensi
     */
    suspend fun getStatistikAbsensi(
        token: String,
        bulan: Int? = null,
        tahun: Int? = null
    ): Result<StatistikData> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<StatistikData>> =
                    apiService.getStatistikAbsensi("Bearer $token", bulan, tahun)

                handleApiResponse(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Helper function untuk menangani respons API secara konsisten
     */
    private fun <T> handleApiResponse(response: Response<ApiResponse<T>>): Result<T> {
        if (!response.isSuccessful) {
            return Result.failure(
                Exception("HTTP Error: ${response.code()} - ${response.message()}")
            )
        }

        val body = response.body()
        if (body == null) {
            return Result.failure(Exception("Respons body kosong"))
        }

        // Sesuaikan dengan model ApiResponse.kt: gunakan "status", bukan "success"
        if (body.status) {
            if (body.data != null) {
                return Result.success(body.data)
            } else {
                return Result.failure(Exception("Data tidak tersedia"))
            }
        } else {
            // message di modelmu tidak nullable, jadi aman pakai langsung
            return Result.failure(Exception(body.message))
        }
    }
}