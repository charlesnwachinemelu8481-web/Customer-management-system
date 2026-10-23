package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.ui.components.CustomerAvatar
import com.example.ui.components.SegmentTagChip
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRelativeTime
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerListScreen(
    customers: List<Customer>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    selectedTagFilter: String?,
    onClearTagFilter: () -> Unit,
    onAddCustomerClick: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusFilters = listOf("All", "Active", "Lead", "Negotiating", "Churn Risk", "Inactive")

    val filteredCustomers = customers.filter { customer ->
        val matchesStatus = if (selectedStatusFilter == "All") true else customer.status.equals(selectedStatusFilter, ignoreCase = true)
        val matchesTag = if (selectedTagFilter.isNullOrBlank()) true else customer.getTagList().any { it.equals(selectedTagFilter, ignoreCase = true) }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            val q = searchQuery.trim().lowercase()
            customer.name.lowercase().contains(q) ||
                    customer.company.lowercase().contains(q) ||
                    customer.email.lowercase().contains(q) ||
                    customer.tags.lowercase().contains(q)
        }
        matchesStatus && matchesTag && matchesSearch
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        val isWideScreen = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with title and "+ Add Customer"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customers",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${filteredCustomers.size} of ${customers.size} customer accounts",
                            fontSize = 13.sp,
                            color = Slate500
                        )
                    }

                    Button(
                        onClick = onAddCustomerClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_customer_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Customer", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Controls row: Search + Status filter chips
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search by customer name, company, email or tag...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Slate400)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_search_input")
                        )

                        // Filters row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate500
                            )

                            statusFilters.forEach { status ->
                                val isSelected = status == selectedStatusFilter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onStatusFilterChange(status) },
                                    label = { Text(status, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndigoContainer,
                                        selectedLabelColor = IndigoPrimary
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("filter_status_${status.lowercase().replace(" ", "_")}")
                                )
                            }
                        }

                        // Active Segment tag filter banner (if filtered by tag)
                        if (selectedTagFilter != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFCCFBF1))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Filtered by segment:",
                                    fontSize = 12.sp,
                                    color = Slate700
                                )
                                SegmentTagChip(tag = selectedTagFilter)
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = onClearTagFilter,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear segment filter",
                                        tint = AccentTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customer List / Table view
            if (filteredCustomers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No customers found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Try modifying your search query or adjusting your filters.",
                                fontSize = 13.sp,
                                color = Slate500,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )
                            Button(
                                onClick = onAddCustomerClick,
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Customer")
                            }
                        }
                    }
                }
            } else if (isWideScreen) {
                // DESKTOP TABLE HYBRID VIEW
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                    ) {
                        Column {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CUSTOMER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.weight(2.5f)
                                )
                                Text(
                                    text = "COMPANY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    text = "SEGMENT / TAGS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    text = "LAST CONTACTED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "STATUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "ACTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate400,
                                    modifier = Modifier.width(70.dp)
                                )
                            }

                            HorizontalDivider(color = SlateCardBorder)

                            // Table Rows
                            filteredCustomers.forEachIndexed { index, customer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCustomerClick(customer.id) }
                                        .padding(horizontal = 20.dp, vertical = 14.dp)
                                        .testTag("customer_row_${customer.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Name + Avatar + Role
                                    Row(
                                        modifier = Modifier.weight(2.5f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CustomerAvatar(name = customer.name, sizeDp = 36)
                                        Column {
                                            Text(
                                                text = customer.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Slate900
                                            )
                                            Text(
                                                text = customer.role,
                                                fontSize = 12.sp,
                                                color = Slate500
                                            )
                                        }
                                    }

                                    // Company
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(
                                            text = customer.company,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = customer.email,
                                            fontSize = 11.sp,
                                            color = Slate400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Tags
                                    Row(
                                        modifier = Modifier.weight(2f),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val tagList = customer.getTagList()
                                        if (tagList.isEmpty()) {
                                            Text("None", fontSize = 12.sp, color = Slate400)
                                        } else {
                                            tagList.take(2).forEach { tag ->
                                                SegmentTagChip(tag = tag)
                                            }
                                            if (tagList.size > 2) {
                                                Text(
                                                    text = "+${tagList.size - 2}",
                                                    fontSize = 11.sp,
                                                    color = Slate400
                                                )
                                            }
                                        }
                                    }

                                    // Last Contacted
                                    Text(
                                        text = formatRelativeTime(customer.lastContactedAt),
                                        fontSize = 12.sp,
                                        color = Slate500,
                                        modifier = Modifier.weight(1.5f)
                                    )

                                    // Status Badge
                                    Box(modifier = Modifier.weight(1.5f)) {
                                        StatusBadge(status = customer.status)
                                    }

                                    // Action
                                    Box(
                                        modifier = Modifier.width(70.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "View Profile",
                                            tint = Slate400,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                if (index < filteredCustomers.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                }
                            }
                        }
                    }
                }
            } else {
                // MOBILE / COMPACT CARD HYBRID VIEW
                items(filteredCustomers, key = { it.id }) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCustomerClick(customer.id) }
                            .testTag("customer_card_${customer.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CustomerAvatar(name = customer.name, sizeDp = 40)
                                    Column {
                                        Text(
                                            text = customer.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = "${customer.company} • ${customer.role}",
                                            fontSize = 12.sp,
                                            color = Slate500
                                        )
                                    }
                                }

                                StatusBadge(status = customer.status)
                            }

                            // Tags row
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                customer.getTagList().forEach { tag ->
                                    SegmentTagChip(tag = tag)
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Last contacted: ${formatRelativeTime(customer.lastContactedAt)}",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )

                                Text(
                                    text = "View Profile →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IndigoPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
