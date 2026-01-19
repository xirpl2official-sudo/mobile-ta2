package com.xirpl2.SASMobile

import java.util.Calendar

/**
 * Helper class untuk mengelola jadwal sholat berdasarkan jenis kelamin dan hari
 */
object JadwalSholatHelper {
    
    enum class JenisKelamin {
        LAKI_LAKI,
        PEREMPUAN
    }
    
    /**
     * Mendapatkan daftar jadwal sholat berdasarkan jenis kelamin dan hari
     * @param jenisKelamin Jenis kelamin user (dari data yang sudah ada)
     * @param calendar Calendar object untuk mengecek hari (default: hari ini)
     * @return List jadwal sholat yang sesuai
     */
    fun getJadwalSholatByGender(
        jenisKelamin: JenisKelamin,
        calendar: Calendar = Calendar.getInstance()
    ): List<String> {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isJumat = dayOfWeek == Calendar.FRIDAY
        
        return when {
            // Hari Jumat
            isJumat -> {
                when (jenisKelamin) {
                    JenisKelamin.LAKI_LAKI -> listOf("Dhuha", "Jumat") // Laki-laki: Dhuha + Jumat
                    JenisKelamin.PEREMPUAN -> listOf("Dhuha", "Zuhur") // Perempuan: Dhuha + Dzuhur
                }
            }
            // Hari selain Jumat
            else -> {
                listOf("Dhuha", "Zuhur") // Semua: Dhuha + Dzuhur
            }
        }
    }
    
    /**
     * Mendapatkan waktu default untuk setiap sholat
     * Nanti bisa diganti dengan data dari API
     */
    fun getWaktuSholat(namaSholat: String): Pair<String, String> {
        return when (namaSholat) {
            "Dhuha" -> Pair("07:00", "08:00")
            "Zuhur" -> Pair("12:00", "12:30")
            "Jumat" -> Pair("11:30", "12:30")
            else -> Pair("00:00", "00:00")
        }
    }
    
    /**
     * Menentukan status sholat berdasarkan waktu saat ini
     * @param jamMulai Jam mulai sholat (format: "HH:mm")
     * @param jamSelesai Jam selesai sholat (format: "HH:mm")
     * @return Status sholat
     */
    fun getStatusSholat(jamMulai: String, jamSelesai: String): StatusSholat {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute
        
        // Parse jam mulai
        val mulaiParts = jamMulai.split(":")
        val mulaiInMinutes = mulaiParts[0].toInt() * 60 + mulaiParts[1].toInt()
        
        // Parse jam selesai
        val selesaiParts = jamSelesai.split(":")
        val selesaiInMinutes = selesaiParts[0].toInt() * 60 + selesaiParts[1].toInt()
        
        return when {
            currentTimeInMinutes < mulaiInMinutes -> StatusSholat.AKAN_DATANG
            currentTimeInMinutes in mulaiInMinutes..selesaiInMinutes -> StatusSholat.SEDANG_BERLANGSUNG
            else -> StatusSholat.SELESAI
        }
    }
    
    /**
     * Generate jadwal sholat lengkap dengan status
     * @param jenisKelamin Jenis kelamin user
     * @return List JadwalSholat dengan status real-time
     */
    fun generateJadwalSholat(jenisKelamin: JenisKelamin): List<JadwalSholat> {
        val namaSholatList = getJadwalSholatByGender(jenisKelamin)
        
        return namaSholatList.map { namaSholat ->
            val (jamMulai, jamSelesai) = getWaktuSholat(namaSholat)
            val status = getStatusSholat(jamMulai, jamSelesai)
            
            JadwalSholat(
                namaSholat = namaSholat,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai,
                status = status
            )
        }
    }
}
