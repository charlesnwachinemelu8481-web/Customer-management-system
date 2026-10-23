package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ClientBaseDatabase
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import com.example.data.repository.CrmRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: ClientBaseDatabase
    private lateinit var repository: CrmRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ClientBaseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CrmRepository(
            database.customerDao(),
            database.interactionDao(),
            database.reminderDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun read_string_from_context() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ClientBase", appName)
    }

    @Test
    fun customer_crud_and_search() = runBlocking {
        val customer = Customer(
            name = "Test Lead",
            company = "Acme Test Corp",
            role = "Director",
            email = "lead@acme.com",
            tags = "Enterprise,VIP",
            status = "Lead"
        )
        val customerId = repository.saveCustomer(customer)
        assertTrue(customerId > 0)

        val retrieved = repository.getCustomer(customerId).first()
        assertNotNull(retrieved)
        assertEquals("Test Lead", retrieved?.name)
        assertEquals("Acme Test Corp", retrieved?.company)

        // Search
        val searchResults = repository.searchCustomers("Acme").first()
        assertEquals(1, searchResults.size)
        assertEquals("Test Lead", searchResults[0].name)
    }

    @Test
    fun interaction_logging_and_reminder_toggle() = runBlocking {
        val customer = Customer(
            name = "Sarah Client",
            company = "Apex Systems",
            status = "Active"
        )
        val customerId = repository.saveCustomer(customer)

        val interaction = Interaction(
            customerId = customerId,
            customerName = "Sarah Client",
            companyName = "Apex Systems",
            type = "CALL",
            date = System.currentTimeMillis(),
            description = "Quarterly business review call"
        )
        repository.addInteraction(interaction)

        val timeline = repository.getInteractionsForCustomer(customerId).first()
        assertEquals(1, timeline.size)
        assertEquals("CALL", timeline[0].type)

        // Reminder test
        val reminder = Reminder(
            customerId = customerId,
            customerName = "Sarah Client",
            companyName = "Apex Systems",
            title = "Send review deck",
            dueDate = System.currentTimeMillis() + 86400000L
        )
        val reminderId = repository.saveReminder(reminder)
        assertTrue(reminderId > 0)

        val remindersList = repository.allReminders.first()
        assertEquals(1, remindersList.size)
        assertEquals(false, remindersList[0].isCompleted)

        // Toggle done
        repository.toggleReminderDone(remindersList[0])
        val updatedReminders = repository.allReminders.first()
        assertEquals(true, updatedReminders[0].isCompleted)
    }
}
