package com.mongodb.tasktracker.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mongodb.tasktracker.R

class CourseAdapter(private var courses: List<CourseInfo>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.TT_data)
        val departmentTextView: TextView = view.findViewById(R.id.Depart_data)
        val descriptionTextView: TextView = view.findViewById(R.id.Desc_data)
        val creditTextView: TextView = view.findViewById(R.id.credit_data)
        //thêm vào các view khác nếu cần
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.titleTextView.text = course.title
        holder.departmentTextView.text = course.departmentName // Display department name
        holder.descriptionTextView.text = course.description
        holder.creditTextView.text = course.credits.toString()
    }

    override fun getItemCount() = courses.size

    fun updateCourses(newCourses: List<CourseInfo>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}
