package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Customer
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditCustomerDialog(
    customerToEdit: Customer? = null,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customerToEdit?.name ?: "") }
    var company by remember { mutableStateOf(customerToEdit?.company ?: "") }
    var role by remember { mutableStateOf(customerToEdit?.role ?: "Client Contact") }
    var phone by remember { mutableStateOf(customerToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(customerToEdit?.email ?: "") }
    var address by remember { mutableStateOf(customerToEdit?.address ?: "") }
    var tagsInput by remember { mutableStateOf(customerToEdit?.tags ?: "") }
    var status by remember { mutableStateOf(customerToEdit?.status ?: "Lead") }
    var notes by remember { mutableStateOf(customerToEdit?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val presetTags = listOf("Enterprise", "SMB", "VIP", "Tech", "Retail", "Inbound", "Renewal Q4")
    val statuses = listOf("Lead", "Active", "Negotiating", "Churn Risk", "Inactive")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
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
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (customerToEdit == null) "Add New Customer" else "Edit Customer Profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Fill in contact info, segment tags, and account notes",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IndigoContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (customerToEdit == null) "NEW" else "EDITING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }
                }

                // Error Message if any
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // Name & Company in 2 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                        },
                        label = { Text("Customer Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_name")
                    )

                    OutlinedTextField(
                        value = company,
                        onValueChange = {
                            company = it
                            errorMessage = null
                        },
                        label = { Text("Company Name *") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_company")
                    )
                }

                // Role & Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Job Title / Role") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_role")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_email")
                    )
                }

                // Phone & Address
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_phone")
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Office Address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_input_address")
                    )
                }

                // Status Selector
                Column {
                    Text(
                        text = "Customer Lifecycle Status",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statuses.forEach { s ->
                            FilterChip(
                                selected = status == s,
                                onClick = { status = s },
                                label = { Text(s, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoContainer,
                                    selectedLabelColor = IndigoPrimary
                                )
                            )
                        }
                    }
                }

                // Tags / Segments
                Column {
                    Text(
                        text = "Segment Tags (comma-separated or tap presets below)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        placeholder = { Text("Enterprise, VIP, Renewal Q4") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = AccentTeal) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_input_tags")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        presetTags.forEach { preset ->
                            val currentList = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val isPresent = currentList.any { it.equals(preset, ignoreCase = true) }

                            FilterChip(
                                selected = isPresent,
                                onClick = {
                                    val updated = if (isPresent) {
                                        currentList.filterNot { it.equals(preset, ignoreCase = true) }
                                    } else {
                                        currentList + preset
                                    }
                                    tagsInput = updated.joinToString(", ")
                                },
                                label = { Text("+$preset", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentTealContainer,
                                    selectedLabelColor = AccentTeal
                                )
                            )
                        }
                    }
                }

                // Internal Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Internal Notes & Relationship Background") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_input_notes")
                )

                // Actions: Cancel & Save
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
                            if (name.isBlank() || company.isBlank()) {
                                errorMessage = "Customer name and company are required."
                                return@Button
                            }

                            val cleanTags = tagsInput.split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                                .joinToString(",")

                            val updatedCustomer = (customerToEdit ?: Customer(
                                name = name.trim(),
                                company = company.trim()
                            )).copy(
                                name = name.trim(),
                                company = company.trim(),
                                role = role.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                address = address.trim(),
                                tags = cleanTags,
                                status = status,
                                notes = notes.trim()
                            )

                            onSave(updatedCustomer)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_customer_btn")
                    ) {
                        Text(
                            text = if (customerToEdit == null) "Save Customer" else "Update Customer",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
