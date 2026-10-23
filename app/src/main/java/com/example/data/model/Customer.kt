package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val company: String,
    val role: String = "Contact",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val tags: String = "", // Comma-separated tags, e.g. "Enterprise,VIP,Tech"
    val status: String = "Lead", // "Lead", "Active", "Negotiating", "Churn Risk", "Inactive"
    val notes: String = "",
    val lastContactedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTagList(): List<String> {
        return if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
