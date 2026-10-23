package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClientBaseSidebar
import com.example.ui.components.NavDestination
import com.example.ui.screens.customer.AddEditCustomerDialog
import com.example.ui.screens.customer.CustomerListScreen
import com.example.ui.screens.customer.CustomerProfileScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.interaction.AddInteractionDialog
import com.example.ui.screens.login.LoginScreen
import com.example.ui.screens.reminders.RemindersScreen
import com.example.ui.screens.segments.SegmentsScreen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()

    // If not logged in, render the business login screen
    if (!userSession.isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { viewModel.login(it) },
            modifier = modifier
        )
        return
    }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()
    val recentInteractions by viewModel.recentInteractions.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()
    val pendingReminders by viewModel.pendingReminders.collectAsState()
    val segments by viewModel.segments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val selectedTagFilter by viewModel.selectedTagFilter.collectAsState()

    val showAddEditCustomerDialog by viewModel.showAddEditCustomerDialog.collectAsState()
    val customerToEdit by viewModel.customerToEdit.collectAsState()
    val showAddInteractionDialog by viewModel.showAddInteractionDialog.collectAsState()
    val customerForInteraction by viewModel.customerForInteraction.collectAsState()

    // Dialogs
    if (showAddEditCustomerDialog) {
        AddEditCustomerDialog(
            customerToEdit = customerToEdit,
            onDismiss = { viewModel.dismissAddEditCustomerDialog() },
            onSave = { viewModel.saveCustomer(it) }
        )
    }

    if (showAddInteractionDialog && customerForInteraction != null) {
        AddInteractionDialog(
            customer = customerForInteraction!!,
            authorName = userSession.fullName,
            onDismiss = { viewModel.dismissAddInteractionDialog() },
            onSaveInteraction = { interaction, reminder ->
                viewModel.saveInteraction(interaction, reminder)
            }
        )
    }

    // Determine currently active nav destination
    val activeNavDestination = when (currentScreen) {
        is CrmScreen.Main -> (currentScreen as CrmScreen.Main).destination
        is CrmScreen.CustomerProfile -> NavDestination.CUSTOMERS
    }

    // Handle Back Press when in sub-screen
    if (currentScreen is CrmScreen.CustomerProfile) {
        BackHandler {
            viewModel.navigateBackToCustomers()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        val isDesktop = maxWidth >= 700.dp

        if (isDesktop) {
            // DESKTOP LAYOUT: Left Sidebar + Right Content Area
            Row(modifier = Modifier.fillMaxSize()) {
                ClientBaseSidebar(
                    currentDestination = activeNavDestination,
                    onNavigate = { viewModel.navigateTo(it) },
                    userSession = userSession,
                    onLogout = { viewModel.logout() },
                    pendingRemindersCount = pendingReminders.size,
                    customersCount = allCustomers.size
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        allCustomers = allCustomers,
                        recentInteractions = recentInteractions,
                        allReminders = allReminders,
                        pendingReminders = pendingReminders,
                        segments = segments,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        selectedTagFilter = selectedTagFilter
                    )
                }
            }
        } else {
            // MOBILE / COMPACT LAYOUT: Top App Bar + Content + Bottom Navigation
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IndigoPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "ClientBase",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Slate900
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.logout() }) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = Slate400
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = SlateSurface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = SlateSurface,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        NavDestination.values().forEach { destination ->
                            val isSelected = activeNavDestination == destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(destination) },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                },
                                label = { Text(destination.title, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IndigoPrimary,
                                    selectedTextColor = IndigoPrimary,
                                    indicatorColor = IndigoContainer,
                                    unselectedIconColor = Slate400,
                                    unselectedTextColor = Slate400
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        allCustomers = allCustomers,
                        recentInteractions = recentInteractions,
                        allReminders = allReminders,
                        pendingReminders = pendingReminders,
                        segments = segments,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        selectedTagFilter = selectedTagFilter
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    currentScreen: CrmScreen,
    viewModel: CrmViewModel,
    allCustomers: List<com.example.data.model.Customer>,
    recentInteractions: List<com.example.data.model.Interaction>,
    allReminders: List<com.example.data.model.Reminder>,
    pendingReminders: List<com.example.data.model.Reminder>,
    segments: List<com.example.data.repository.SegmentInfo>,
    searchQuery: String,
    statusFilter: String,
    selectedTagFilter: String?
) {
    when (currentScreen) {
        is CrmScreen.Main -> {
            when (currentScreen.destination) {
                NavDestination.DASHBOARD -> {
                    DashboardScreen(
                        customers = allCustomers,
                        recentInteractions = recentInteractions,
                        pendingReminders = pendingReminders,
                        onAddCustomerClick = { viewModel.openAddCustomerDialog() },
                        onCustomerClick = { viewModel.viewCustomerProfile(it) },
                        onViewAllReminders = { viewModel.navigateTo(NavDestination.REMINDERS) },
                        onViewAllSegments = { viewModel.navigateTo(NavDestination.SEGMENTS) }
                    )
                }
                NavDestination.CUSTOMERS -> {
                    CustomerListScreen(
                        customers = allCustomers,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedStatusFilter = statusFilter,
                        onStatusFilterChange = { viewModel.setStatusFilter(it) },
                        selectedTagFilter = selectedTagFilter,
                        onClearTagFilter = { viewModel.clearTagFilter() },
                        onAddCustomerClick = { viewModel.openAddCustomerDialog() },
                        onCustomerClick = { viewModel.viewCustomerProfile(it) }
                    )
                }
                NavDestination.SEGMENTS -> {
                    SegmentsScreen(
                        segments = segments,
                        totalCustomersCount = allCustomers.size,
                        onSegmentClick = { tag -> viewModel.filterBySegment(tag) },
                        onAddCustomerWithTag = { viewModel.openAddCustomerDialog() }
                    )
                }
                NavDestination.REMINDERS -> {
                    RemindersScreen(
                        reminders = allReminders,
                        customers = allCustomers,
                        onToggleReminderDone = { viewModel.toggleReminder(it) },
                        onDeleteReminder = { viewModel.deleteReminder(it) },
                        onCustomerClick = { viewModel.viewCustomerProfile(it) },
                        onAddReminder = { viewModel.saveReminder(it) }
                    )
                }
            }
        }
        is CrmScreen.CustomerProfile -> {
            val customerFlow = viewModel.getCustomer(currentScreen.customerId)
            val customer by customerFlow.collectAsState(initial = null)
            val interactionsFlow = viewModel.getInteractionsForCustomer(currentScreen.customerId)
            val interactions by interactionsFlow.collectAsState(initial = emptyList())

            if (customer != null) {
                CustomerProfileScreen(
                    customer = customer!!,
                    interactions = interactions,
                    onBackClick = { viewModel.navigateBackToCustomers() },
                    onEditCustomerClick = { viewModel.openEditCustomerDialog(customer!!) },
                    onAddInteractionClick = { viewModel.openAddInteractionDialog(customer!!) },
                    onDeleteCustomerClick = { viewModel.deleteCustomer(customer!!) }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = IndigoPrimary)
                }
            }
        }
    }
}
