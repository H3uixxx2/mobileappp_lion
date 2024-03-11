package com.mongodb.tasktracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mongodb.tasktracker.model.CourseAdapter
import com.mongodb.tasktracker.model.CourseInfo

class InforFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_infor, container, false)
    }

    fun updateCourses(courses: List<CourseInfo>) {
        courseAdapter.updateCourses(courses)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập thông tin cá nhân
        val nameTextView = view.findViewById<TextView>(R.id.name_data)
        val emailTextView = view.findViewById<TextView>(R.id.email_data)
        val departmentTextView = view.findViewById<TextView>(R.id.department_data)

        val name = arguments?.getString("name", "N/A")
        val email = arguments?.getString("email", "N/A")
        val department = arguments?.getString("department", "N/A")

        nameTextView.text = name ?: "N/A"
        emailTextView.text = email ?: "N/A"
        departmentTextView.text = department ?: "N/A"

        // Thiết lập RecyclerView
        recyclerView = view.findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseAdapter = CourseAdapter(emptyList())
        recyclerView.adapter = courseAdapter

        // Lấy dữ liệu khóa học từ arguments và cập nhật RecyclerView
        val coursesInfo = arguments?.getSerializable("courses") as? ArrayList<CourseInfo>
        coursesInfo?.let {
            courseAdapter.updateCourses(it)
        }
    }
}



