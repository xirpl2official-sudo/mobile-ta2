package com.xirpl2.SASMobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class BuatSandiBaruActivity : AppCompatActivity() {
    private lateinit var passwordLayoutBaru: TextInputLayout
    private lateinit var passwordlayoutKonfirm: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buat_sandi_baru)
        window.statusBarColor = 0xFF2886D6.toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passwordLayoutBaru = findViewById(R.id.passwordLayoutBaru)
        passwordlayoutKonfirm = findViewById(R.id.passwordLayoutKonfirm)

        setHintTextColors()
    }

    private fun setHintTextColors() {
        val hintColor = ContextCompat.getColorStateList(this, R.color.hint_and_floating)
        passwordLayoutBaru.defaultHintTextColor = hintColor
        passwordlayoutKonfirm.defaultHintTextColor = hintColor
    }
}