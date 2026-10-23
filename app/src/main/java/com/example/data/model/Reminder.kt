package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val companyName: String,
    val title: String,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val priority: String = "Normal", // "High", "Normal", "Low"
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
