package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import com.example.ui.components.InteractionIcon
import com.example.ui.components.StatCard
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.AccentTeal
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    customers: List<Customer>,
    recentInteractions: List<Interaction>,
    pendingReminders: List<Reminder>,
    onAddCustomerClick: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    onViewAllReminders: () -> Unit,
    onViewAllSegments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val thirtyDaysAgo = now - (30L * 86400000L)
    val newThisMonthCount = customers.count { it.createdAt >= thirtyDaysAgo }
    val followUpsDueCount = pendingReminders.size
    val activePipelineCount = customers.count { it.status == "Active" || it.status == "Negotiating" }

    // Overdue or due today
    val urgentReminders = pendingReminders.filter { it.dueDate <= now + 86400000L }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome and Quick Actions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Overview of your active customer relationships & sales pipeline",
                        fontSize = 13.sp,
                        color = Slate500
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAddCustomerClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_add_customer_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Customer", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Urgent Alert Banner (if follow-ups due today)
        if (urgentReminders.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewAllReminders() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusRiskRoseBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StatusRiskRose.copy(alpha = 0.3f)))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(StatusRiskRose),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "${urgentReminders.size} Follow-up${if (urgentReminders.size > 1) "s" else ""} Due Today or Overdue",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF9F1239)
                                )
                                Text(
                                    text = "Action needed for: ${urgentReminders.take(2).joinToString(", ") { it.customerName }}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFBE123C),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Review follow-ups",
                            tint = Color(0xFF9F1239),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Stat Cards Grid
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 3
            ) {
                // Card 1: Total Customers
                StatCard(
                    title = "Total Customers",
                    value = "${customers.size}",
                    subtitle = "across all accounts",
                    icon = Icons.Default.Group,
                    iconTint = IndigoPrimary,
                    trendText = "+14%",
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .widthIn(min = 220.dp)
                )

                // Card 2: New This Month
                StatCard(
                    title = "New This Month",
                    value = "$newThisMonthCount",
                    subtitle = "onboarded leads",
                    icon = Icons.Default.FiberNew,
                    iconTint = AccentTeal,
                    trendText = "+${newThisMonthCount}",
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .widthIn(min = 220.dp)
                )

                // Card 3: Follow-ups Due
                StatCard(
                    title = "Follow-ups Due",
                    value = "$followUpsDueCount",
                    subtitle = "${urgentReminders.size} high priority",
                    icon = Icons.Default.Alarm,
                    iconTint = if (urgentReminders.isNotEmpty()) StatusRiskRose else Color(0xFFD97706),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .widthIn(min = 220.dp)
                )
            }
        }

        // Quick Shortcuts bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewAllSegments,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate900)
                ) {
                    Icon(Icons.Default.Label, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Explore Customer Segments", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onViewAllReminders,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate900)
                ) {
                    Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(16.dp), tint = IndigoPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Manage Follow-ups", fontSize = 13.sp)
                }
            }
        }

        // Recent Activity Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Recent Activity Feed",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Text(
                    text = "${recentInteractions.size} log entries",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        // Recent Activity Feed Items
        if (recentInteractions.isEmpty()) {
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
                                tint = Slate400,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No logged interactions yet",
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )
                            Text(
                                text = "Add calls, meetings, and notes to see client history here",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                        }
                    }
                }
            }
        } else {
            items(recentInteractions, key = { it.id }) { interaction ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCustomerClick(interaction.customerId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        InteractionIcon(type = interaction.type, size = 38)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = interaction.customerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "•",
                                        color = Slate400,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = interaction.companyName,
                                        fontSize = 13.sp,
                                        color = Slate500,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = formatRelativeTime(interaction.date),
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = interaction.description,
                                fontSize = 13.sp,
                                color = Slate700,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = interaction.type.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate700
                                    )
                                }

                                Text(
                                    text = "Logged by ${interaction.author}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
