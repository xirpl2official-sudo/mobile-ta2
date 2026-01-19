package com.xirpl2.SASMobile

import android.os.Bundle
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class GantiKataSandi : AppCompatActivity() {
    private lateinit var nisLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lupa_katasandi)
        window.statusBarColor = 0xFF2886D6.toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nisLayout = findViewById(R.id.nisLayout)
        emailLayout = findViewById(R.id.emailLayout)

        val etNis = findViewById<android.widget.EditText>(R.id.et_nis)
        val etEmail = findViewById<android.widget.EditText>(R.id.et_email)
        val btnKirim = findViewById<android.widget.Button>(R.id.buttonKirim)

        setHintTextColors()


        btnKirim.setOnClickListener {
            val nis = etNis.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Validasi NIS tidak boleh kosong
            if (nis.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "Gagal",
                    "NIS tidak boleh kosong",
                    MotionToastStyle.ERROR,
                    Gravity.CENTER,
                    MotionToast.LONG_DURATION,
                    null
                )
                etNis.requestFocus()
                return@setOnClickListener
            }

            // Validasi email tidak boleh kosong
            if (email.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "Gagal",
                    "Email tidak boleh kosong",
                    MotionToastStyle.ERROR,
                    Gravity.CENTER,
                    MotionToast.LONG_DURATION,
                    null
                )
                etEmail.requestFocus()
                return@setOnClickListener
            }

            // Validasi format email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                MotionToast.createColorToast(
                    this,
                    "Gagal",
                    "Format email tidak valid",
                    MotionToastStyle.ERROR,
                    Gravity.CENTER,
                    MotionToast.LONG_DURATION,
                    null
                )
                etEmail.requestFocus()
                return@setOnClickListener
            }

            // Jika semua validasi berhasil, lanjut ke halaman verifikasi OTP
            val intent = android.content.Intent(this, VerifikasiOtpActivity::class.java)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
    }

    private fun setHintTextColors() {
        val hintColor = ContextCompat.getColorStateList(this, R.color.hint_and_floating)
        nisLayout.defaultHintTextColor = hintColor
        emailLayout.defaultHintTextColor = hintColor
    }
}