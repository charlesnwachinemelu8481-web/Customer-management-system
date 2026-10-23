package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Customer::class, Interaction::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class ClientBaseDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun interactionDao(): InteractionDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ClientBaseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClientBaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClientBaseDatabase::class.java,
                    "clientbase_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: ClientBaseDatabase) {
            val now = System.currentTimeMillis()
            val day = 86_400_000L

            val initialCustomers = listOf(
                Customer(
                    id = 1,
                    name = "Sarah Jenkins",
                    company = "Apex Cloud Solutions",
                    role = "VP of Engineering",
                    phone = "+1 (415) 892-3341",
                    email = "sarah.jenkins@apexcloud.io",
                    address = "550 Montgomery St, Suite 400, San Francisco, CA",
                    tags = "Enterprise,VIP,Renewal Q4",
                    status = "Active",
                    notes = "Key decision maker for cloud migration project. Very interested in automated API telemetry sync.",
                    lastContactedAt = now - (day * 1),
                    createdAt = now - (day * 45)
                ),
                Customer(
                    id = 2,
                    name = "Marcus Vance",
                    company = "TechFlow Labs",
                    role = "Chief Technology Officer",
                    phone = "+1 (206) 554-1920",
                    email = "m.vance@techflowlabs.com",
                    address = "1200 6th Ave, Seattle, WA",
                    tags = "Tech,SMB,Inbound",
                    status = "Negotiating",
                    notes = "Evaluating annual contract tier for 150 team seats. Sent contract revision on Tuesday.",
                    lastContactedAt = now - (day * 2),
                    createdAt = now - (day * 12)
                ),
                Customer(
                    id = 3,
                    name = "Elena Rostova",
                    company = "Quantum Dynamics",
                    role = "Head of Product Ops",
                    phone = "+1 (617) 482-9011",
                    email = "elena.r@quantumdyn.com",
                    address = "100 Innovation Way, Cambridge, MA",
                    tags = "Enterprise,Tech,VIP",
                    status = "Active",
                    notes = "Requested custom workflow integration. Very satisfied with SLA performance.",
                    lastContactedAt = now - (day * 4),
                    createdAt = now - (day * 60)
                ),
                Customer(
                    id = 4,
                    name = "David Chen",
                    company = "FinEdge Global",
                    role = "Managing Director",
                    phone = "+1 (212) 904-7712",
                    email = "david.chen@finedge.ny",
                    address = "30 Rockefeller Plaza, New York, NY",
                    tags = "Enterprise,VIP,Inbound",
                    status = "Lead",
                    notes = "Initial demo scheduled. Needs compliance audit sheet and SOC2 report.",
                    lastContactedAt = now - (day * 6),
                    createdAt = now - (day * 8)
                ),
                Customer(
                    id = 5,
                    name = "Priya Patel",
                    company = "Horizon Retail Group",
                    role = "Director of Commerce",
                    phone = "+1 (312) 670-3419",
                    email = "p.patel@horizonretail.com",
                    address = "233 S Wacker Dr, Chicago, IL",
                    tags = "Retail,SMB",
                    status = "Churn Risk",
                    notes = "Billing inquiries pending. Needs urgent customer success check-in on feature adoption.",
                    lastContactedAt = now - (day * 14),
                    createdAt = now - (day * 90)
                ),
                Customer(
                    id = 6,
                    name = "Liam O'Connor",
                    company = "Aurora BioTech",
                    role = "Procurement Lead",
                    phone = "+1 (858) 412-8830",
                    email = "liam@aurorabiotech.net",
                    address = "10555 Science Center Dr, San Diego, CA",
                    tags = "Tech,Renewal Q4",
                    status = "Active",
                    notes = "Renewal discussion went well. Agreement signed pending legal countersign.",
                    lastContactedAt = now - (day * 3),
                    createdAt = now - (day * 180)
                ),
                Customer(
                    id = 7,
                    name = "Amara Diallo",
                    company = "Nexus Logistics",
                    role = "Operations VP",
                    phone = "+1 (404) 521-7600",
                    email = "amara@nexuslogistics.co",
                    address = "3344 Peachtree Rd, Atlanta, GA",
                    tags = "SMB,Inbound",
                    status = "Lead",
                    notes = "Attended webinar on fleet tracking automation. Looking for a pilot proposal.",
                    lastContactedAt = now - (day * 5),
                    createdAt = now - (day * 5)
                )
            )

            val initialInteractions = listOf(
                Interaction(
                    id = 1,
                    customerId = 1,
                    customerName = "Sarah Jenkins",
                    companyName = "Apex Cloud Solutions",
                    type = "MEETING",
                    date = now - (day * 1),
                    description = "Executive product demo with engineering leads. Walked through architecture and security compliance. Strong buy-in.",
                    author = "Alex Turner"
                ),
                Interaction(
                    id = 2,
                    customerId = 2,
                    customerName = "Marcus Vance",
                    companyName = "TechFlow Labs",
                    type = "EMAIL",
                    date = now - (day * 2),
                    description = "Sent updated pricing proposal including multi-seat discount for 150 licenses.",
                    author = "Alex Turner"
                ),
                Interaction(
                    id = 3,
                    customerId = 6,
                    customerName = "Liam O'Connor",
                    companyName = "Aurora BioTech",
                    type = "CALL",
                    date = now - (day * 3),
                    description = "Discussed Q4 contract renewal timeline. Confirmed 2-year expansion commit.",
                    author = "Alex Turner"
                ),
                Interaction(
                    id = 4,
                    customerId = 3,
                    customerName = "Elena Rostova",
                    companyName = "Quantum Dynamics",
                    type = "NOTE",
                    date = now - (day * 4),
                    description = "Noted custom export requirement for their internal data analytics pipeline. Escalated to solutions architect.",
                    author = "Alex Turner"
                ),
                Interaction(
                    id = 5,
                    customerId = 4,
                    customerName = "David Chen",
                    companyName = "FinEdge Global",
                    type = "CALL",
                    date = now - (day * 6),
                    description = "Discovery call. Clarified strict data sovereignty requirements and single sign-on integration.",
                    author = "Alex Turner"
                )
            )

            val initialReminders = listOf(
                Reminder(
                    id = 1,
                    customerId = 1,
                    customerName = "Sarah Jenkins",
                    companyName = "Apex Cloud Solutions",
                    title = "Send security questionnaire & SOC2 report",
                    dueDate = now + (day * 1),
                    isCompleted = false,
                    priority = "High"
                ),
                Reminder(
                    id = 2,
                    customerId = 2,
                    customerName = "Marcus Vance",
                    companyName = "TechFlow Labs",
                    title = "Follow up on proposal review & legal signoff",
                    dueDate = now + (day * 2),
                    isCompleted = false,
                    priority = "High"
                ),
                Reminder(
                    id = 3,
                    customerId = 5,
                    customerName = "Priya Patel",
                    companyName = "Horizon Retail Group",
                    title = "Account review call to address onboarding bottleneck",
                    dueDate = now, // Due Today!
                    isCompleted = false,
                    priority = "High"
                ),
                Reminder(
                    id = 4,
                    customerId = 4,
                    customerName = "David Chen",
                    companyName = "FinEdge Global",
                    title = "Deliver enterprise custom architecture brief",
                    dueDate = now + (day * 4),
                    isCompleted = false,
                    priority = "Normal"
                ),
                Reminder(
                    id = 5,
                    customerId = 6,
                    customerName = "Liam O'Connor",
                    companyName = "Aurora BioTech",
                    title = "Confirm countersigned renewal agreement",
                    dueDate = now - (day * 1),
                    isCompleted = true,
                    completedAt = now - (day * 1),
                    priority = "Normal"
                )
            )

            database.customerDao().insertAll(initialCustomers)
            database.interactionDao().insertAll(initialInteractions)
            database.reminderDao().insertAll(initialReminders)
        }
    }
}
