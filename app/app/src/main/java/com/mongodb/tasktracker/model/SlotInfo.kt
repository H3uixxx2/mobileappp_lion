package com.mongodb.tasktracker.model

import java.io.Serializable

data class SlotInfo(
    val slotId: String,// Thêm trường này để lưu ID của Slot
    val startTime: String,
    val endTime: String,
    val day: String,
    val courseId: String,
    val courseTitle: String, // Tiêu đề của khóa học từ Collection `Courses`
    var building: String? = null // Khởi tạo giá trị mặc định là null cho building.
) : Serializable
