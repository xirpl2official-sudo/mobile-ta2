package com.xirpl2.SASMobile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer
import java.util.concurrent.TimeUnit

class VerifikasiOtpActivity : AppCompatActivity() {

    private var resendTimer: CountDownTimer? = null // opsional: simpan referensi untuk cancel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verifikasi_otp)
        window.statusBarColor = 0xFFE48134.toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvEmailInfo = findViewById<TextView>(R.id.tvEmailInfo)
        val email = intent.getStringExtra("USER_EMAIL")
        if (email != null) {
            tvEmailInfo.text = getString(R.string.email_verifikasi, email)
        }

        // Setup OTP Input
        setupOtpInput()

        // Setup Kirim Ulang OTP
        val btnKirimUlang = findViewById<TextView>(R.id.btnKirimUlang)
        btnKirimUlang.isClickable = true
        btnKirimUlang.setOnClickListener {
            kirimUlangOtpKeServer()
            startResendTimer(btnKirimUlang)
        }

        // Mulai timer jika perlu (misal setelah pertama kali masuk halaman)
        startResendTimer(btnKirimUlang)
    }

    private fun startResendTimer(view: TextView) {
        // Batalkan timer sebelumnya (opsional, cegah multiple timer)
        resendTimer?.cancel()

        view.isEnabled = false

        resendTimer = object : CountDownTimer(TimeUnit.MINUTES.toMillis(1), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                view.text = "Kirim Ulang (${seconds}s)"
            }

            override fun onFinish() {
                view.text = getString(R.string.KirimUlgKode) // atau "Kirim Ulang OTP"
                view.isEnabled = true
            }
        }.start()
    }

    private fun kirimUlangOtpKeServer() {
        val email = intent.getStringExtra("USER_EMAIL")
        // TODO: Panggil API untuk kirim ulang OTP
        // Contoh (gunakan Retrofit + Coroutines):
        // viewModel.kirimUlangOtp(email)
    }

    private fun setupOtpInput() {
        val otpBox1 = findViewById<EditText>(R.id.otpBox1)
        val otpBox2 = findViewById<EditText>(R.id.otpBox2)
        val otpBox3 = findViewById<EditText>(R.id.otpBox3)
        val otpBox4 = findViewById<EditText>(R.id.otpBox4)
        val otpBox5 = findViewById<EditText>(R.id.otpBox5)
        val otpBox6 = findViewById<EditText>(R.id.otpBox6)

        val otpBoxes = arrayOf(otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6)

        for (i in otpBoxes.indices) {
            otpBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpBoxes.size - 1) {
                        otpBoxes[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            otpBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (otpBoxes[i].text.isEmpty() && i > 0) {
                        otpBoxes[i - 1].requestFocus()
                        otpBoxes[i - 1].text.clear()
                    }
                }
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resendTimer?.cancel() // hindari memory leak
    }
}