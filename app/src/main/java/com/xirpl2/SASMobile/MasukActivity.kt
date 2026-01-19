package com.xirpl2.SASMobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import com.xirpl2.SASMobile.model.LoginRequest
import com.xirpl2.SASMobile.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout


class MasukActivity : AppCompatActivity() {

    private lateinit var etNis: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnMasuk: Button
    private lateinit var textBuatAkun: TextView
    private lateinit var textLupaPassword: TextView
    private lateinit var nisLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)
        window.statusBarColor = 0xFF2886D6.toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNis = findViewById(R.id.et_nis)
        etPassword = findViewById(R.id.et_password)
        btnMasuk = findViewById(R.id.btn_masuk)
        textBuatAkun = findViewById(R.id.textBuatAkun)
        textLupaPassword = findViewById(R.id.textLupaPassword)
        nisLayout = findViewById(R.id.nisLayout)
        passwordLayout = findViewById(R.id.passwordLayout)

        setHintTextColors()

        btnMasuk.setOnClickListener {
            loginUser()
        }

        textBuatAkun.setOnClickListener {
            val intent = Intent(this@MasukActivity, DaftarActivity::class.java)
            startActivity(intent)
        }

        textLupaPassword.setOnClickListener {
            val intent = Intent(this@MasukActivity, GantiKataSandi::class.java)
            startActivity(intent)
        }
    }

    private fun setHintTextColors() {
        val hintColor = ContextCompat.getColorStateList(this, R.color.hint_and_floating)
        nisLayout.defaultHintTextColor = hintColor
        passwordLayout.defaultHintTextColor = hintColor
    }

    private fun loginUser() {
        val nis = etNis.text.toString().trim()
        val password = etPassword.text.toString()

        // Validasi input
        if (nis.isEmpty() || password.isEmpty()) {
            MotionToast.createColorToast(
                this,
                "Gagal",
                "NIS dan Password wajib diisi!",
                MotionToastStyle.ERROR,
                Gravity.CENTER,
                MotionToast.LONG_DURATION,
                null
            )
            return
        }

        // Disable button saat proses
        btnMasuk.isEnabled = false
        btnMasuk.text = "Masuk..."

        // Panggil API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MasukActivity", "Mengirim request login: NIS=$nis")
                val response = RetrofitClient.apiService.login(LoginRequest(nis, password))

                withContext(Dispatchers.Main) {
                    btnMasuk.isEnabled = true
                    btnMasuk.text = "Masuk"

                    if (response.isSuccessful) {
                        val body = response.body()
                        val respCode = response.code()
                        Log.d("MasukActivity", "Response: ${response.code()} - $body")

                        if (respCode == 200) {
                            MotionToast.createColorToast(
                                this@MasukActivity,
                                "Berhasil",
                                "Selamat datang, ${body!!.data?.nama_siswa ?: ""}!",
                                MotionToastStyle.SUCCESS,
                                Gravity.CENTER,
                                MotionToast.LONG_DURATION,
                                null
                            )

                            // Simpan data user ke SharedPreferences jika diperlukan
                            saveUserSession(body.data)

                            // Arahkan ke halaman utama
                            startActivity(Intent(this@MasukActivity, BerandaActivity::class.java))
                            finish()
                        } else {
                            MotionToast.createColorToast(
                                this@MasukActivity,
                                "MBOT",
                                body?.message ?: "Login gagal",
                                MotionToastStyle.ERROR,
                                Gravity.CENTER,
                                MotionToast.LONG_DURATION,
                                null
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MasukActivity", "Error: ${response.code()} - $errorBody")
                        MotionToast.createColorToast(
                            this@MasukActivity,
                            "Gagal",
                            "NIS atau password salah",
                            MotionToastStyle.ERROR,
                            Gravity.CENTER,
                            MotionToast.LONG_DURATION,
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MasukActivity", "Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    btnMasuk.isEnabled = true
                    btnMasuk.text = "Masuk"
                    MotionToast.createColorToast(
                        this@MasukActivity,
                        "Error",
                        "Error: ${e.message}",
                        MotionToastStyle.ERROR,
                        Gravity.CENTER,
                        MotionToast.LONG_DURATION,
                        null
                    )
                }
            }
        }
    }

    private fun saveUserSession(user: com.xirpl2.SASMobile.model.AkunLoginResponse?) {
        if (user == null) return

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_nis", user.nis)
            putString("user_name", user.nama_siswa)
            putString("user_jk", user.jk)
            putString("user_kelas", user.kelas)
            putString("user_jurusan", user.jurusan)
            apply()
        }
    }
}