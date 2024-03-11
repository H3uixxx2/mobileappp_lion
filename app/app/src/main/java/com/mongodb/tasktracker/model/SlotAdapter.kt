package com.mongodb.tasktracker.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mongodb.tasktracker.R

class SlotAdapter(private var slots: List<SlotInfo>) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.sbname_data)
        val dayTextView: TextView = view.findViewById(R.id.day_data)
        val startTextView: TextView = view.findViewById(R.id.start_data)
        val endTextView: TextView = view.findViewById(R.id.end_data)
        // Các thành phần khác của item_attendance.xml
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.titleTextView.text = slot.courseTitle
        holder.dayTextView.text = slot.day
        holder.startTextView.text = slot.startTime
        holder.endTextView.text = slot.endTime
        // Bind các dữ liệu khác vào view
    }

    override fun getItemCount() = slots.size

    fun updateSlots(newSlots: List<SlotInfo>) {
        slots = newSlots
        notifyDataSetChanged()
    }
}