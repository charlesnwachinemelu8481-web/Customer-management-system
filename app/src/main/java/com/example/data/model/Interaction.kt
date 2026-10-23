package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interactions")
data class Interaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val companyName: String,
    val type: String, // "CALL", "MEETING", "EMAIL", "NOTE"
    val date: Long,
    val description: String,
    val author: String = "You",
    val createdAt: Long = System.currentTimeMillis()
)
