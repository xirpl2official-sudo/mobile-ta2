package com.xirpl2.SASMobile.model

/**
 * Response model untuk API Jadwal Sholat
 */
data class JadwalSholatResponse(
    val success: Boolean,
    val message: String,
    val data: List<JadwalSholatData>
)

data class JadwalSholatData(
    val id: Int,
    val nama_sholat: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val hari: String? = null // Optional: jika API mengirim info hari
)

/**
 * Response model untuk API Riwayat Absensi
 */
data class RiwayatAbsensiResponse(
    val success: Boolean,
    val message: String,
    val data: List<RiwayatAbsensiData>
)

data class RiwayatAbsensiData(
    val id: Int,
    val tanggal: String,
    val nama_sholat: String,
    val status: String, // "HADIR", "ALPHA", "SAKIT", "IZIN"
    val waktu_absen: String? = null
)

/**
 * Response model untuk User Profile (untuk mendapatkan jenis kelamin)
 */
data class UserProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val nama: String,
    val nis: String,
    val jenis_kelamin: String, // "L" atau "P"
    val kelas: String,
    val email: String? = null,
    val no_hp: String? = null
)

/**
 * Response model untuk Statistik Absensi
 */
data class StatistikAbsensiResponse(
    val success: Boolean,
    val message: String,
    val data: StatistikData
)

data class StatistikData(
    val total_hari: Int,
    val total_hadir: Int,
    val total_alpha: Int,
    val total_sakit: Int,
    val total_izin: Int,
    val persentase_kehadiran: Float
)

/**
 * Request body untuk submit absensi
 */
data class AbsensiRequest(
    val jadwal_sholat_id: Int,
    val latitude: Double,
    val longitude: Double,
    val foto: String? = null // Base64 encoded image
)

/**
 * Response untuk submit absensi
 */
data class AbsensiResponse(
    val success: Boolean,
    val message: String,
    val data: AbsensiData? = null
)

data class AbsensiData(
    val id: Int,
    val waktu_absen: String,
    val status: String
)
