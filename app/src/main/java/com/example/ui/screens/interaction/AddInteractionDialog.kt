package com.example.ui.screens.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AccentTealContainer
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurface

data class InteractionTypeOption(
    val type: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AddInteractionDialog(
    customer: Customer,
    authorName: String = "You",
    onDismiss: () -> Unit,
    onSaveInteraction: (Interaction, Reminder?) -> Unit
) {
    val types = listOf(
        InteractionTypeOption("CALL", Icons.Default.Call, "Phone Call"),
        InteractionTypeOption("MEETING", Icons.Default.Event, "Meeting"),
        InteractionTypeOption("EMAIL", Icons.Default.Email, "Email"),
        InteractionTypeOption("NOTE", Icons.Default.Note, "Internal Note")
    )

    var selectedType by remember { mutableStateOf("CALL") }
    var description by remember { mutableStateOf("") }
    var selectedDateOffset by remember { mutableStateOf("Today") } // "Today", "Yesterday", "2 days ago"
    var createFollowUp by remember { mutableStateOf(false) }
    var followUpTitle by remember { mutableStateOf("") }
    var followUpDays by remember { mutableStateOf(3) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val now = System.currentTimeMillis()
    val day = 86400000L

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Add Note / Interaction",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Logging for: ${customer.name} (${customer.company})",
                            fontSize = 13.sp,
                            color = Slate500
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // Interaction Type Selector
                Column {
                    Text(
                        text = "Interaction Type *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        types.forEach { item ->
                            val isSelected = item.type == selectedType
                            val bg = if (isSelected) IndigoContainer else Color(0xFFF1F5F9)
                            val tint = if (isSelected) IndigoPrimary else Slate500
                            val border = if (isSelected) IndigoPrimary else SlateCardBorder

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { selectedType = item.type }
                                    .testTag("interaction_type_${item.type.lowercase()}")
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = tint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = tint
                                    )
                                }
                            }
                        }
                    }
                }

                // Date Selector
                Column {
                    Text(
                        text = "Date of Interaction",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Today", "Yesterday", "2 days ago", "1 week ago").forEach { dateLabel ->
                            FilterChip(
                                selected = selectedDateOffset == dateLabel,
                                onClick = { selectedDateOffset = dateLabel },
                                label = { Text(dateLabel, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentTealContainer,
                                    selectedLabelColor = AccentTeal
                                )
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = null
                    },
                    label = { Text("Meeting notes, discussion summary, or email details *") },
                    placeholder = { Text("E.g. Discussed renewal term options. Client requested discount for multi-year lock...") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("interaction_description_input")
                )

                // Optional Follow-up reminder checkbox
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = createFollowUp,
                            onCheckedChange = {
                                createFollowUp = it
                                if (it && followUpTitle.isBlank()) {
                                    followUpTitle = "Follow up with ${customer.name} on ${selectedType.lowercase()}"
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                        )
                        Text(
                            text = "Set an automated follow-up reminder",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate900
                        )
                    }

                    if (createFollowUp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = followUpTitle,
                            onValueChange = { followUpTitle = it },
                            label = { Text("Follow-up task / subject") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Due in:", fontSize = 12.sp, color = Slate500)
                            listOf(1 to "Tomorrow", 3 to "3 days", 7 to "1 week", 14 to "2 weeks").forEach { (d, label) ->
                                FilterChip(
                                    selected = followUpDays == d,
                                    onClick = { followUpDays = d },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (description.isBlank()) {
                                errorMessage = "Please enter an interaction description or notes."
                                return@Button
                            }

                            val timestamp = when (selectedDateOffset) {
                                "Yesterday" -> now - day
                                "2 days ago" -> now - (day * 2)
                                "1 week ago" -> now - (day * 7)
                                else -> now
                            }

                            val interaction = Interaction(
                                customerId = customer.id,
                                customerName = customer.name,
                                companyName = customer.company,
                                type = selectedType,
                                date = timestamp,
                                description = description.trim(),
                                author = authorName
                            )

                            val reminder = if (createFollowUp && followUpTitle.isNotBlank()) {
                                Reminder(
                                    customerId = customer.id,
                                    customerName = customer.name,
                                    companyName = customer.company,
                                    title = followUpTitle.trim(),
                                    dueDate = now + (day * followUpDays),
                                    priority = "Normal"
                                )
                            } else null

                            onSaveInteraction(interaction, reminder)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_interaction_btn")
                    ) {
                        Text("Save Interaction", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
