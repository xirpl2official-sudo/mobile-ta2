package com.xirpl2.SASMobile

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xirpl2.SASMobile.model.StatistikAdmin

class BerandaAdminActivity : AppCompatActivity() {

    private lateinit var rvJurusan: RecyclerView
    private lateinit var tvTotalSiswaValue: TextView
    private lateinit var tvHadirHariIniValue: TextView
    private lateinit var tvIzinSakitValue: TextView
    private lateinit var tvKehadiranValue: TextView
    private lateinit var tvNamaSholat: TextView
    private lateinit var tvWaktuSholat: TextView
    
    private lateinit var jurusanAdapter: JurusanAdapter
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda_admin)

        // Initialize Views
        initializeViews()
        
        // Setup Popup Menu
        setupPopupMenu()
        
        // Setup Data dengan dummy data
        setupStatistikData()
        setupJadwalSholat()
        setupJurusanList()
    }
    
    private fun initializeViews() {
        rvJurusan = findViewById(R.id.rvJurusan)
        tvTotalSiswaValue = findViewById(R.id.tvTotalSiswaValue)
        tvHadirHariIniValue = findViewById(R.id.tvHadirHariIniValue)
        tvIzinSakitValue = findViewById(R.id.tvIzinSakitValue)
        tvKehadiranValue = findViewById(R.id.tvKehadiranValue)
        tvNamaSholat = findViewById(R.id.tvNamaSholat)
        tvWaktuSholat = findViewById(R.id.tvWaktuSholat)
    }
    
    /**
     * Setup data statistik dengan dummy data
     * Nanti bisa diganti dengan data dari API
     */
    private fun setupStatistikData() {
        // Data dummy
        val statistik = StatistikAdmin(
            totalSiswa = 1250,
            totalHadirHariIni = 1180,
            izinSakit = 45,
            persentaseKehadiran = 94
        )
        
        tvTotalSiswaValue.text = statistik.totalSiswa.toString()
        tvHadirHariIniValue.text = statistik.totalHadirHariIni.toString()
        tvIzinSakitValue.text = statistik.izinSakit.toString()
        tvKehadiranValue.text = "${statistik.persentaseKehadiran}%"
    }
    
    /**
     * Setup jadwal sholat terdekat
     */
    private fun setupJadwalSholat() {
        // Data dummy - jadwal sholat terdekat
        tvNamaSholat.text = "Dhuha"
        tvWaktuSholat.text = "Waktu : 07:00 - 08:00"
    }
    
    /**
     * Setup RecyclerView untuk list jurusan
     */
    private fun setupJurusanList() {
        val listJurusan = JurusanHelper.getAllJurusan()
        
        jurusanAdapter = JurusanAdapter(listJurusan)
        
        rvJurusan.apply {
            layoutManager = LinearLayoutManager(this@BerandaAdminActivity)
            adapter = jurusanAdapter
            isNestedScrollingEnabled = false
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
            // TODO: Navigate to Settings Activity
            Toast.makeText(this, "Pengaturan - Coming Soon", Toast.LENGTH_SHORT).show()
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
        val nama = sharedPref.getString("nama_siswa", "Admin") ?: "Admin"
        val nis = sharedPref.getString("nis", "ADMIN001") ?: "ADMIN001"
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
                    remove("user_role")
                    apply()
                }
                
                // TODO: Navigate to Login Activity
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}