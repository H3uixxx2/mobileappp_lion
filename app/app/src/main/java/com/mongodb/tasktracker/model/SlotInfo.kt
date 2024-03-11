package com.mongodb.tasktracker.model

import java.io.Serializable

data class SlotInfo(
    val startTime: String,
    val endTime: String,
    val day: String,
    val courseId: String,
    val courseTitle: String // Tiêu đề của khóa học từ Collection `Courses`
) : Serializable
