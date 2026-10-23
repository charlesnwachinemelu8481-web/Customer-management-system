package com.example.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Customer
import com.example.data.model.Reminder
import com.example.ui.components.formatDateOnly
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusRiskRose
import com.example.ui.theme.StatusRiskRoseBg

@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    customers: List<Customer>,
    onToggleReminderDone: (Reminder) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    onCustomerClick: (Long) -> Unit,
    onAddReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val day = 86400000L

    var selectedTab by remember { mutableStateOf("Pending") } // "Pending", "Due Today", "All", "Completed"
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredReminders = reminders.filter { reminder ->
        when (selectedTab) {
            "Pending" -> !reminder.isCompleted
            "Due Today" -> !reminder.isCompleted && reminder.dueDate <= now + day
            "Completed" -> reminder.isCompleted
            else -> true
        }
    }.sortedWith(
        compareBy<Reminder> { it.isCompleted }
            .thenBy { it.dueDate }
    )

    if (showAddDialog) {
        AddReminderDialog(
            customers = customers,
            onDismiss = { showAddDialog = false },
            onSave = {
                onAddReminder(it)
                showAddDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Follow-up Reminders",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Upcoming calls, proposal deadlines, and scheduled client tasks",
                        fontSize = 13.sp,
                        color = Slate500
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_reminder_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Follow-up", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Tabs Row: Pending, Due Today, All, Completed
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Pending", "Due Today", "All", "Completed").forEach { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = {
                            val count = when (tab) {
                                "Pending" -> reminders.count { !it.isCompleted }
                                "Due Today" -> reminders.count { !it.isCompleted && it.dueDate <= now + day }
                                "Completed" -> reminders.count { it.isCompleted }
                                else -> reminders.size
                            }
                            Text("$tab ($count)", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoContainer,
                            selectedLabelColor = IndigoPrimary
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                }
            }
        }

        // Reminders List
        if (filteredReminders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AccentTeal,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (selectedTab == "Completed") "No completed follow-ups yet" else "All caught up!",
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "No follow-up tasks in this category.",
                                fontSize = 13.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredReminders, key = { it.id }) { reminder ->
                val isOverdue = !reminder.isCompleted && reminder.dueDate < now
                val isDueToday = !reminder.isCompleted && (reminder.dueDate in now..(now + day))

                val (badgeBg, badgeFg, dueLabel) = when {
                    reminder.isCompleted -> Triple(Color(0xFFF1F5F9), Slate500, "Completed")
                    isOverdue -> Triple(StatusRiskRoseBg, StatusRiskRose, "OVERDUE • ${formatDateOnly(reminder.dueDate)}")
                    isDueToday -> Triple(StatusRiskRoseBg, StatusRiskRose, "DUE TODAY • ${formatDateOnly(reminder.dueDate)}")
                    else -> Triple(Color(0xFFEEF2FF), IndigoPrimary, "Due ${formatDateOnly(reminder.dueDate)}")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_item_${reminder.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reminder.isCompleted) Color(0xFFF8FAFC) else SlateSurface
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Mark Done Checkbox
                        Checkbox(
                            checked = reminder.isCompleted,
                            onCheckedChange = { onToggleReminderDone(reminder) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentTeal,
                                uncheckedColor = Slate400
                            ),
                            modifier = Modifier.testTag("reminder_checkbox_${reminder.id}")
                        )

                        // Title & Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = reminder.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (reminder.isCompleted) Slate400 else Slate900,
                                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )

                                if (reminder.priority == "High" && !reminder.isCompleted) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(StatusRiskRoseBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "HIGH",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusRiskRose
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Customer name clickable link
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onCustomerClick(reminder.customerId) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${reminder.customerName} (${reminder.companyName})",
                                        fontSize = 12.sp,
                                        color = IndigoPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text("•", color = Slate400, fontSize = 11.sp)

                                // Due Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = dueLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeFg
                                    )
                                }
                            }
                        }

                        // Delete reminder action
                        IconButton(
                            onClick = { onDeleteReminder(reminder) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Reminder",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var daysUntilDue by remember { mutableStateOf(1) }
    var priority by remember { mutableStateOf("Normal") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val now = System.currentTimeMillis()
    val day = 86400000L

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Add Follow-up Reminder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Follow-up Action *") },
                    placeholder = { Text("E.g. Send contract addendum, call regarding SLA...") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Select Customer
                if (customers.isNotEmpty()) {
                    Column {
                        Text("Associate with Customer:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            customers.take(4).forEach { customer ->
                                val isSelected = selectedCustomer?.id == customer.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCustomer = customer },
                                    label = { Text(customer.name.substringBefore(" "), fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                // Due Time
                Column {
                    Text("Due Date:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "Today", 1 to "Tomorrow", 3 to "In 3 Days", 7 to "In 1 Week").forEach { (d, label) ->
                            FilterChip(
                                selected = daysUntilDue == d,
                                onClick = { daysUntilDue = d },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Priority
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    listOf("Normal", "High").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a reminder title"
                                return@Button
                            }
                            val cust = selectedCustomer ?: customers.firstOrNull()
                            val reminder = Reminder(
                                customerId = cust?.id ?: 0L,
                                customerName = cust?.name ?: "General Client",
                                companyName = cust?.company ?: "ClientBase",
                                title = title.trim(),
                                dueDate = now + (daysUntilDue * day),
                                priority = priority
                            )
                            onSave(reminder)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Follow-up")
                    }
                }
            }
        }
    }
}
