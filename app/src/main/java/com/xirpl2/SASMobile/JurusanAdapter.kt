package com.xirpl2.SASMobile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import model.Jurusan

/**
 * Adapter untuk RecyclerView list Jurusan
 */
class JurusanAdapter(
    private val listJurusan: List<Jurusan>
) : RecyclerView.Adapter<JurusanAdapter.JurusanViewHolder>() {

    inner class JurusanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardJurusan: CardView = itemView.findViewById(R.id.cardJurusan)
        val tvNamaJurusan: TextView = itemView.findViewById(R.id.tvNamaJurusan)
        val tvLabelHariIni: TextView = itemView.findViewById(R.id.tvLabelHariIni)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JurusanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jurusan, parent, false)
        return JurusanViewHolder(view)
    }

    override fun onBindViewHolder(holder: JurusanViewHolder, position: Int) {
        val jurusan = listJurusan[position]
        
        // Set nama jurusan
        holder.tvNamaJurusan.text = jurusan.nama
        
        // Set warna background card sesuai warna jurusan
        try {
            val color = Color.parseColor(jurusan.warna)
            holder.cardJurusan.setCardBackgroundColor(color)
        } catch (e: IllegalArgumentException) {
            // Fallback ke warna default jika parsing gagal
            holder.cardJurusan.setCardBackgroundColor(Color.parseColor("#2196F3"))
        }
    }

    override fun getItemCount(): Int = listJurusan.size
}
