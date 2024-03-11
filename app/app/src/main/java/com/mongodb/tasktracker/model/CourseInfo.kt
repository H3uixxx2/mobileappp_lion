package com.mongodb.tasktracker.model

import java.io.Serializable

data class CourseInfo(
    val title: String,
    val description: String,
    val departmentName: String,
    val credits: Int
) : Serializable