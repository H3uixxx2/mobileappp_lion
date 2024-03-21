package com.mongodb.tasktracker.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mongodb.tasktracker.R

class SlotAdapter(private var slots: List<SlotInfo>) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.sbname_data)
        private val dayTextView: TextView = view.findViewById(R.id.day_data)
        private val startTextView: TextView = view.findViewById(R.id.start_data)
        private val endTextView: TextView = view.findViewById(R.id.end_data)
        private val buildingTextView: TextView = view.findViewById(R.id.building_data) // TextView cho thông tin tòa nhà

        fun bind(slot: SlotInfo) {
            titleTextView.text = slot.courseTitle
            dayTextView.text = slot.day
            startTextView.text = slot.startTime
            endTextView.text = slot.endTime
            buildingTextView.text = slot.building // Hiển thị thông tin tòa nhà
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.bind(slot) // Gọi phương thức bind để cập nhật dữ liệu vào view
    }

    override fun getItemCount(): Int = slots.size

    fun updateSlots(newSlots: List<SlotInfo>) {
        slots = newSlots
        notifyDataSetChanged() // Cập nhật dữ liệu và làm mới RecyclerView
    }
}
