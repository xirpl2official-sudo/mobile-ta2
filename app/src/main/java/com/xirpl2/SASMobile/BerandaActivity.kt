package com.xirpl2.SASMobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xirpl2.SASMobile.repository.BerandaRepository
import kotlinx.coroutines.launch

class BerandaActivity : AppCompatActivity() {

    private lateinit var rvJadwalSholat: RecyclerView
    private lateinit var rvRiwayatAbsensi: RecyclerView
    private lateinit var tvTotalValue: TextView
    private lateinit var tvHadirValue: TextView
    private lateinit var tvStatistikValue: TextView

    private lateinit var jadwalAdapter: JadwalSholatAdapter
    private lateinit var riwayatAdapter: RiwayatAbsensiAdapter
    
    private var popupWindow: PopupWindow? = null
    
    private val repository = BerandaRepository()
    private val TAG = "BerandaActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        // Initialize Views
        initializeViews()
        
        // Setup Popup Menu
        setupPopupMenu()
        
        // Setup RecyclerViews dengan data dummy dulu
        setupJadwalSholat()
        setupRiwayatAbsensi()

        setupAbsensiButton()
        // TODO: Uncomment ketika API sudah ready
        // loadDataFromAPI()
    }
    
    private fun initializeViews() {
        rvJadwalSholat = findViewById(R.id.rvJadwalSholat)
        rvRiwayatAbsensi = findViewById(R.id.rvRiwayatAbsensi)
        tvTotalValue = findViewById(R.id.tvTotalValue)
        tvHadirValue = findViewById(R.id.tvHadirValue)
        tvStatistikValue = findViewById(R.id.tvStatistikValue)
    }

    private fun setupAbsensiButton() {
        val btnAbsensi = findViewById<android.widget.Button>(R.id.btnAbsensi)
        btnAbsensi.setOnClickListener {
            startActivity(Intent(this@BerandaActivity, ScanQrActivity::class.java))
        }
    }
    private fun setupJadwalSholat() {
        // Ambil jenis kelamin dari SharedPreferences atau data user yang tersimpan
        val jenisKelamin = getJenisKelaminFromStorage()
        
        // Generate jadwal sholat berdasarkan jenis kelamin dan hari
        val jadwalList = JadwalSholatHelper.generateJadwalSholat(jenisKelamin)

        // Setup adapter
        jadwalAdapter = JadwalSholatAdapter(jadwalList)

        // Setup RecyclerView
        rvJadwalSholat.apply {
            layoutManager = LinearLayoutManager(this@BerandaActivity)
            adapter = jadwalAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupRiwayatAbsensi() {
        // Data dummy untuk Riwayat Absensi
        val riwayatList = listOf(
            RiwayatAbsensi("14 NOV 2024", "Dhuha", StatusAbsensi.HADIR, "07:30"),
            RiwayatAbsensi("14 NOV 2024", "Zuhur", StatusAbsensi.HADIR, "12:15"),
            RiwayatAbsensi("13 NOV 2024", "Dhuha", StatusAbsensi.ALPHA, "07:45"),
            RiwayatAbsensi("13 NOV 2024", "Zuhur", StatusAbsensi.SAKIT, "12:20"),
            RiwayatAbsensi("12 NOV 2024", "Dhuha", StatusAbsensi.HADIR, "07:35"),
            RiwayatAbsensi("12 NOV 2024", "Zuhur", StatusAbsensi.IZIN, "12:10")
        )

        // Setup adapter
        riwayatAdapter = RiwayatAbsensiAdapter(riwayatList)

        // Setup RecyclerView
        rvRiwayatAbsensi.apply {
            layoutManager = LinearLayoutManager(this@BerandaActivity)
            adapter = riwayatAdapter
            isNestedScrollingEnabled = false
        }
    }
    
    /**
     * Mendapatkan jenis kelamin dari SharedPreferences
     * Data ini seharusnya sudah disimpan saat login/registrasi
     */
    private fun getJenisKelaminFromStorage(): JadwalSholatHelper.JenisKelamin {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val jenisKelaminStr = sharedPref.getString("jenis_kelamin", "L") ?: "L"
        
        return if (jenisKelaminStr == "P") {
            JadwalSholatHelper.JenisKelamin.PEREMPUAN
        } else {
            JadwalSholatHelper.JenisKelamin.LAKI_LAKI
        }
    }
    
    /**
     * Setup Popup Menu untuk icon hamburger
     */
    private fun setupPopupMenu() {
        val iconMenu = findViewById<ImageView>(R.id.iconMenu)
        iconMenu.setOnClickListener {
            showPopupMenu(it)
        }
    }
    
    /**
     * Menampilkan popup menu di bawah icon hamburger
     */
    private fun showPopupMenu(anchorView: android.view.View) {
        // Dismiss popup yang sedang aktif jika ada
        dismissPopupMenu()
        
        // Inflate layout popup menu
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_menu, null)
        
        // Get user data dari SharedPreferences
        val (nama, nis) = getUserDataFromStorage()
        
        // Populate data ke popup
        val tvStudentName = popupView.findViewById<TextView>(R.id.tvStudentName)
        val tvStudentNIS = popupView.findViewById<TextView>(R.id.tvStudentNIS)
        tvStudentName.text = nama
        tvStudentNIS.text = nis
        
        // Setup click listeners untuk menu items
        val btnSettings = popupView.findViewById<LinearLayout>(R.id.btnSettings)
        val btnLogout = popupView.findViewById<LinearLayout>(R.id.btnLogout)
        
        btnSettings.setOnClickListener {
            dismissPopupMenu()
            // Navigate to Settings Activity
            val intent = Intent(this, PengaturanAkunActivity::class.java)
            startActivity(intent)
        }
        
        btnLogout.setOnClickListener {
            dismissPopupMenu()
            handleLogout()
        }
        
        // Create PopupWindow
        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // focusable agar bisa dismiss saat klik di luar
        )
        
        // Set background untuk shadow dan dismiss on outside touch
        popupWindow?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = true
        
        // Show popup di bawah anchor view (icon menu)
        popupWindow?.showAsDropDown(anchorView, 0, 10, Gravity.START)
    }
    
    /**
     * Dismiss popup menu jika sedang ditampilkan
     */
    private fun dismissPopupMenu() {
        popupWindow?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        popupWindow = null
    }
    
    /**
     * Mendapatkan data user (nama dan NIS) dari SharedPreferences
     */
    private fun getUserDataFromStorage(): Pair<String, String> {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val nama = sharedPref.getString("nama_siswa", "Nama Siswa") ?: "Nama Siswa"
        val nis = sharedPref.getString("nis", "0000000000") ?: "0000000000"
        return Pair(nama, nis)
    }
    
    /**
     * Handle logout dengan konfirmasi dialog
     */
    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                // Clear auth token dan user data
                val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                sharedPref.edit().apply {
                    remove("auth_token")
                    remove("nama_siswa")
                    remove("nis")
                    remove("jenis_kelamin")
                    apply()
                }
                
                // TODO: Navigate to Login Activity
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    /**
     * Load data dari API
     * Uncomment ketika backend sudah ready
     */
    private fun loadDataFromAPI() {
        val token = getAuthToken()
        
        if (token.isEmpty()) {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to login
            return
        }
        
        lifecycleScope.launch {
            // Load Jadwal Sholat
            loadJadwalSholatFromAPI(token)
            
            // Load Riwayat Absensi
            loadRiwayatAbsensiFromAPI(token)
            
            // Load Statistik
            loadStatistikFromAPI(token)
        }
    }
    
    private suspend fun loadJadwalSholatFromAPI(token: String) {
        repository.getJadwalSholat(token).fold(
            onSuccess = { jadwalDataList ->
                // Convert API data ke model lokal
                val jadwalList = jadwalDataList.map { data ->
                    val status = JadwalSholatHelper.getStatusSholat(data.jam_mulai, data.jam_selesai)
                    JadwalSholat(
                        namaSholat = data.nama_sholat,
                        jamMulai = data.jam_mulai,
                        jamSelesai = data.jam_selesai,
                        status = status
                    )
                }
                
                // Update adapter
                runOnUiThread {
                    jadwalAdapter = JadwalSholatAdapter(jadwalList)
                    rvJadwalSholat.adapter = jadwalAdapter
                }
            },
            onFailure = { error ->
                Log.e(TAG, "Error loading jadwal sholat: ${error.message}")
                // Tetap gunakan data dummy jika API gagal
            }
        )
    }
    
    private suspend fun loadRiwayatAbsensiFromAPI(token: String) {
        repository.getRiwayatAbsensi(token, 10).fold(
            onSuccess = { riwayatDataList ->
                // Convert API data ke model lokal
                val riwayatList = riwayatDataList.map { data ->
                    val status = when (data.status.uppercase()) {
                        "HADIR" -> StatusAbsensi.HADIR
                        "ALPHA" -> StatusAbsensi.ALPHA
                        "SAKIT" -> StatusAbsensi.SAKIT
                        "IZIN" -> StatusAbsensi.IZIN
                        else -> StatusAbsensi.ALPHA
                    }
                    
                    RiwayatAbsensi(
                        tanggal = formatTanggal(data.tanggal), // Format ke "DD MMM YYYY"
                        namaSholat = data.nama_sholat,
                        status = status,
                        waktuAbsen = formatWaktu(data.waktu_absen) // Format ke "HH:mm"
                    )
                }
                
                // Update adapter
                runOnUiThread {
                    riwayatAdapter = RiwayatAbsensiAdapter(riwayatList)
                    rvRiwayatAbsensi.adapter = riwayatAdapter
                }
            },
            onFailure = { error ->
                Log.e(TAG, "Error loading riwayat absensi: ${error.message}")
            }
        )
    }
    
    private suspend fun loadStatistikFromAPI(token: String) {
        repository.getStatistikAbsensi(token).fold(
            onSuccess = { statistik ->
                runOnUiThread {
                    tvTotalValue.text = statistik.total_hari.toString()
                    tvHadirValue.text = statistik.total_hadir.toString()
                    tvStatistikValue.text = "${statistik.persentase_kehadiran.toInt()}%"
                }
            },
            onFailure = { error ->
                Log.e(TAG, "Error loading statistik: ${error.message}")
            }
        )
    }
    
    private fun getAuthToken(): String {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", "") ?: ""
    }
    
    /**
     * Format tanggal dari "YYYY-MM-DD" ke "DD MMM YYYY"
     * Contoh: "2024-11-14" -> "14 NOV 2024"
     */
    private fun formatTanggal(tanggal: String): String {
        return try {
            val parts = tanggal.split("-")
            if (parts.size == 3) {
                val tahun = parts[0]
                val bulan = when (parts[1]) {
                    "01" -> "JAN"
                    "02" -> "FEB"
                    "03" -> "MAR"
                    "04" -> "APR"
                    "05" -> "MEI"
                    "06" -> "JUN"
                    "07" -> "JUL"
                    "08" -> "AGU"
                    "09" -> "SEP"
                    "10" -> "OKT"
                    "11" -> "NOV"
                    "12" -> "DES"
                    else -> parts[1]
                }
                val hari = parts[2].toIntOrNull()?.toString() ?: parts[2]
                "$hari $bulan $tahun"
            } else {
                tanggal
            }
        } catch (e: Exception) {
            tanggal
        }
    }
    
    /**
     * Format waktu dari "HH:mm:ss" ke "HH:mm"
     * Contoh: "07:30:00" -> "07:30"
     */
    private fun formatWaktu(waktu: String?): String? {
        if (waktu == null) return null
        return try {
            val parts = waktu.split(":")
            if (parts.size >= 2) {
                "${parts[0]}:${parts[1]}"
            } else {
                waktu
            }
        } catch (e: Exception) {
            waktu
        }
    }

    // Fungsi helper untuk refresh data dari database/API
    fun refreshJadwalSholat(newData: List<JadwalSholat>) {
        jadwalAdapter = JadwalSholatAdapter(newData)
        rvJadwalSholat.adapter = jadwalAdapter
    }

    fun refreshRiwayatAbsensi(newData: List<RiwayatAbsensi>) {
        riwayatAdapter = RiwayatAbsensiAdapter(newData)
        rvRiwayatAbsensi.adapter = riwayatAdapter
    }
}