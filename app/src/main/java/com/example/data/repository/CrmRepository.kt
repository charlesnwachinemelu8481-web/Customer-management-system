package com.example.data.repository

import com.example.data.local.CustomerDao
import com.example.data.local.InteractionDao
import com.example.data.local.ReminderDao
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class SegmentInfo(
    val name: String,
    val count: Int,
    val sampleCompanies: List<String>
)

class CrmRepository(
    private val customerDao: CustomerDao,
    private val interactionDao: InteractionDao,
    private val reminderDao: ReminderDao
) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    fun getCustomer(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)

    fun searchCustomers(query: String): Flow<List<Customer>> {
        return if (query.isBlank()) {
            customerDao.getAllCustomers()
        } else {
            customerDao.searchCustomers(query.trim())
        }
    }

    suspend fun saveCustomer(customer: Customer): Long {
        return if (customer.id == 0L) {
            customerDao.insertCustomer(customer)
        } else {
            customerDao.updateCustomer(customer)
            customer.id
        }
    }

    suspend fun deleteCustomer(customer: Customer) {
        interactionDao.deleteInteractionsForCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    // Interactions
    fun getInteractionsForCustomer(customerId: Long): Flow<List<Interaction>> {
        return interactionDao.getInteractionsForCustomer(customerId)
    }

    val recentInteractions: Flow<List<Interaction>> = interactionDao.getAllRecentInteractions(25)

    suspend fun addInteraction(interaction: Interaction, updateCustomerContactDate: Boolean = true): Long {
        val id = interactionDao.insertInteraction(interaction)
        if (updateCustomerContactDate) {
            customerDao.updateLastContacted(interaction.customerId, interaction.date)
        }
        return id
    }

    // Reminders
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()
    val pendingReminders: Flow<List<Reminder>> = reminderDao.getPendingReminders()

    fun getRemindersForCustomer(customerId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForCustomer(customerId)
    }

    suspend fun saveReminder(reminder: Reminder): Long {
        return if (reminder.id == 0L) {
            reminderDao.insertReminder(reminder)
        } else {
            reminderDao.updateReminder(reminder)
            reminder.id
        }
    }

    suspend fun toggleReminderDone(reminder: Reminder) {
        val newStatus = !reminder.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        reminderDao.setReminderDone(reminder.id, newStatus, completedAt)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    // Dynamic segments calculation
    val segments: Flow<List<SegmentInfo>> = allCustomers.map { customers ->
        val tagMap = mutableMapOf<String, MutableList<String>>()
        customers.forEach { customer ->
            customer.getTagList().forEach { tag ->
                val list = tagMap.getOrPut(tag) { mutableListOf() }
                if (list.size < 3) {
                    list.add(customer.company)
                }
            }
        }
        tagMap.map { (tag, companies) ->
            val totalInTag = customers.count { it.getTagList().contains(tag) }
            SegmentInfo(name = tag, count = totalInTag, sampleCompanies = companies)
        }.sortedByDescending { it.count }
    }
}
