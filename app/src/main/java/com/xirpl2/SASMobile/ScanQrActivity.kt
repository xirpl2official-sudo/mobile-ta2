package com.xirpl2.SASMobile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class ScanQrActivity : AppCompatActivity() {

    private lateinit var barcodeView: BarcodeView
    private lateinit var btnScan: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_qr)

        // Inisialisasi view
        barcodeView = findViewById(R.id.barcodeView)
        btnScan = findViewById(R.id.btnScan)
        tvStatus = findViewById(R.id.tvStatus)

        // Atur padding untuk sistem bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup tombol kembali
        findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Setup tombol scan
        btnScan.setOnClickListener {
            startScanning()
        }

        // Opsional: atur format yang ingin dipindai (misal QR_CODE saja)
        barcodeView.decoderFactory = DefaultDecoderFactory(listOf(com.google.zxing.BarcodeFormat.QR_CODE))
    }

    private fun startScanning() {
        tvStatus.visibility = android.view.View.GONE
        barcodeView.resume()

        // Gunakan BarcodeCallback
        barcodeView.setTorch(false) // matikan senter jika nyala
        barcodeView.decodeSingle(object : com.journeyapps.barcodescanner.BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                barcodeView.pause() // hentikan kamera setelah dapat hasil

                runOnUiThread {
                    if (result != null && result.text.isNotBlank()) {
                        val qrContent = result.text
                        Log.d("QR_SCAN", "Hasil: $qrContent")

                        tvStatus.text = "Berhasil: $qrContent"
                        tvStatus.visibility = android.view.View.VISIBLE
                        tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))

                        Toast.makeText(this@ScanQrActivity, "QR: $qrContent", Toast.LENGTH_SHORT).show()

                        // Optional: kembali ke Beranda
                        // finish()
                    } else {
                        tvStatus.text = "Tidak ada QR yang terdeteksi"
                        tvStatus.visibility = android.view.View.VISIBLE
                        tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
                // Bisa diabaikan
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Jangan resume di sini karena kita control manual via decodeSingle
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}