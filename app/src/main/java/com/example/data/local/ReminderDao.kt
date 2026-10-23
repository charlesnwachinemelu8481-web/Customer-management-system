package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE customerId = :customerId ORDER BY dueDate ASC")
    fun getRemindersForCustomer(customerId: Long): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<Reminder>)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("UPDATE reminders SET isCompleted = :isDone, completedAt = :completedAt WHERE id = :reminderId")
    suspend fun setReminderDone(reminderId: Long, isDone: Boolean, completedAt: Long?)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT COUNT(*) FROM reminders WHERE isCompleted = 0")
    suspend fun getPendingCount(): Int
}
