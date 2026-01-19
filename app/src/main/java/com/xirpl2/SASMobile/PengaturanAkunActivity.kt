package com.xirpl2.SASMobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PengaturanAkunActivity : AppCompatActivity() {

    // UI Components
    private lateinit var btnBack: ImageView
    private lateinit var cardProfilePhoto: CardView
    private lateinit var tvInitial: TextView
    private lateinit var etNIS: TextView
    private lateinit var etNamaLengkap: TextView
    private lateinit var tvJenisKelamin: TextView
    private lateinit var etEmail: TextView
    private lateinit var tvChangeEmail: TextView
    
    // Data
    private var currentUserData: UserData? = null
    private var selectedImageUri: Uri? = null
    
    private val TAG = "PengaturanAkunActivity"
    
    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                selectedImageUri = imageUri
                // TODO: Update foto profil preview
                Toast.makeText(this, "Foto dipilih (preview segera menyusul)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan_akun)

        // Initialize Views
        initializeViews()
        
        // Setup Spinner
        setupJenisKelaminSpinner()
        
        // Load User Data
        loadUserData()
        
        // Setup Click Listeners
        setupListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        cardProfilePhoto = findViewById(R.id.cardProfilePhoto)
        tvInitial = findViewById(R.id.tvInitial)
        etNIS = findViewById(R.id.etNIS)
        etNamaLengkap = findViewById(R.id.etNamaLengkap)
        tvJenisKelamin = findViewById(R.id.tvJenisKelamin)
        etEmail = findViewById(R.id.etEmail)
        tvChangeEmail = findViewById(R.id.tvChangeEmail)
    }

    private fun setupJenisKelaminSpinner() {
        // No longer needed as we use TextView
    }

    private fun setupListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }
        
        // Profile photo click
        cardProfilePhoto.setOnClickListener {
            showPhotoPickerDialog()
        }
        
        // Change email link
        tvChangeEmail.setOnClickListener {
            showChangeEmailDialog()
        }
    }

    /**
     * Load user data dari SharedPreferences
     * Nanti bisa diganti dengan API call
     */
    private fun loadUserData() {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        
        currentUserData = UserData(
            nis = sharedPref.getString("nis", "") ?: "",
            namaLengkap = sharedPref.getString("nama_siswa", "") ?: "",
            jenisKelamin = sharedPref.getString("jenis_kelamin", "L") ?: "L",
            email = sharedPref.getString("email", "") ?: "",
            fotoProfil = sharedPref.getString("foto_profil", null)
        )
        
        // Populate fields
        populateFields(currentUserData!!)
    }

    /**
     * Populate form fields dengan data user (read-only)
     */
    private fun populateFields(userData: UserData) {
        etNIS.text = userData.nis
        etNamaLengkap.text = userData.namaLengkap
        etEmail.text = userData.email
        
        // Set jenis kelamin
        val jenisKelaminText = if (userData.jenisKelamin == "P") "Perempuan" else "Laki - laki"
        tvJenisKelamin.text = jenisKelaminText
        
        // Set initial dari nama
        if (userData.namaLengkap.isNotEmpty()) {
            tvInitial.text = userData.namaLengkap.first().uppercase()
        }
    }

    /**
     * Show dialog untuk memilih foto profil
     */
    private fun showPhotoPickerDialog() {
        val options = arrayOf("Pilih dari Galeri", "Batal")
        AlertDialog.Builder(this)
            .setTitle("Ganti Foto Profil")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openImagePicker()
                    1 -> dialog.dismiss()
                }
            }
            .show()
    }

    /**
     * Open image picker
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    /**
     * Show dialog untuk change email dengan custom layout
     */
    private fun showChangeEmailDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_ubah_email, null)
        
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Initialize dialog views
        val etEmailDialog = dialogView.findViewById<EditText>(R.id.etEmailDialog)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClose)
        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btnBatalDialog)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpanDialog)
        
        // Set current email
        etEmailDialog.setText(currentUserData?.email)
        
        // Setup listeners
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
        
        btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }
        
        btnSimpan.setOnClickListener {
            val newEmail = etEmailDialog.text.toString().trim()
            
            if (newEmail.isEmpty()) {
                etEmailDialog.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            
            if (!newEmail.contains("@")) {
                etEmailDialog.error = "Email tidak valid"
                return@setOnClickListener
            }
            
            // Update data
            updateEmail(newEmail)
            alertDialog.dismiss()
        }
        
        alertDialog.show()
    }

    private fun updateEmail(newEmail: String) {
        val currentData = currentUserData ?: return
        
        val updatedData = currentData.copy(email = newEmail)
        
        // Save to SharedPref
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPref.edit().putString("email", newEmail).apply()
        
        // Update UI
        etEmail.text = newEmail
        currentUserData = updatedData
        
        Toast.makeText(this, "Email berhasil diubah", Toast.LENGTH_SHORT).show()
    }


    /**
     * Data class untuk user data
     */
    data class UserData(
        val nis: String,
        val namaLengkap: String,
        val jenisKelamin: String, // "L" atau "P"
        val email: String,
        val fotoProfil: String? = null
    )
}
