package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ClientBaseDatabase
import com.example.data.model.Customer
import com.example.data.model.Interaction
import com.example.data.model.Reminder
import com.example.data.model.UserSession
import com.example.data.repository.CrmRepository
import com.example.data.repository.SegmentInfo
import com.example.ui.components.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class CrmScreen {
    data class Main(val destination: NavDestination) : CrmScreen()
    data class CustomerProfile(val customerId: Long) : CrmScreen()
}

class CrmViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ClientBaseDatabase.getDatabase(application, viewModelScope)
    private val repository = CrmRepository(
        database.customerDao(),
        database.interactionDao(),
        database.reminderDao()
    )

    // User session (starts with business demo account)
    private val _userSession = MutableStateFlow(UserSession.DemoUser)
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    // Screen navigation
    private val _currentScreen = MutableStateFlow<CrmScreen>(CrmScreen.Main(NavDestination.DASHBOARD))
    val currentScreen: StateFlow<CrmScreen> = _currentScreen.asStateFlow()

    // Filters and search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _selectedTagFilter = MutableStateFlow<String?>(null)
    val selectedTagFilter: StateFlow<String?> = _selectedTagFilter.asStateFlow()

    // Dialog states
    private val _showAddEditCustomerDialog = MutableStateFlow(false)
    val showAddEditCustomerDialog: StateFlow<Boolean> = _showAddEditCustomerDialog.asStateFlow()

    private val _customerToEdit = MutableStateFlow<Customer?>(null)
    val customerToEdit: StateFlow<Customer?> = _customerToEdit.asStateFlow()

    private val _showAddInteractionDialog = MutableStateFlow(false)
    val showAddInteractionDialog: StateFlow<Boolean> = _showAddInteractionDialog.asStateFlow()

    private val _customerForInteraction = MutableStateFlow<Customer?>(null)
    val customerForInteraction: StateFlow<Customer?> = _customerForInteraction.asStateFlow()

    // Repository Data Streams
    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentInteractions: StateFlow<List<Interaction>> = repository.recentInteractions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<Reminder>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReminders: StateFlow<List<Reminder>> = repository.pendingReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val segments: StateFlow<List<SegmentInfo>> = repository.segments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auth actions
    fun login(session: UserSession) {
        _userSession.value = session.copy(isLoggedIn = true)
        _currentScreen.value = CrmScreen.Main(NavDestination.DASHBOARD)
    }

    fun logout() {
        _userSession.value = _userSession.value.copy(isLoggedIn = false)
    }

    // Navigation actions
    fun navigateTo(destination: NavDestination) {
        _currentScreen.value = CrmScreen.Main(destination)
    }

    fun viewCustomerProfile(customerId: Long) {
        _currentScreen.value = CrmScreen.CustomerProfile(customerId)
    }

    fun navigateBackToCustomers() {
        _currentScreen.value = CrmScreen.Main(NavDestination.CUSTOMERS)
    }

    fun filterBySegment(tag: String) {
        _selectedTagFilter.value = tag
        _currentScreen.value = CrmScreen.Main(NavDestination.CUSTOMERS)
    }

    fun clearTagFilter() {
        _selectedTagFilter.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    // Customer CRUD
    fun openAddCustomerDialog() {
        _customerToEdit.value = null
        _showAddEditCustomerDialog.value = true
    }

    fun openEditCustomerDialog(customer: Customer) {
        _customerToEdit.value = customer
        _showAddEditCustomerDialog.value = true
    }

    fun dismissAddEditCustomerDialog() {
        _showAddEditCustomerDialog.value = false
        _customerToEdit.value = null
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.saveCustomer(customer)
            dismissAddEditCustomerDialog()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_currentScreen.value is CrmScreen.CustomerProfile) {
                navigateBackToCustomers()
            }
        }
    }

    // Interactions
    fun openAddInteractionDialog(customer: Customer) {
        _customerForInteraction.value = customer
        _showAddInteractionDialog.value = true
    }

    fun dismissAddInteractionDialog() {
        _showAddInteractionDialog.value = false
        _customerForInteraction.value = null
    }

    fun saveInteraction(interaction: Interaction, reminder: Reminder?) {
        viewModelScope.launch {
            repository.addInteraction(interaction)
            if (reminder != null) {
                repository.saveReminder(reminder)
            }
            dismissAddInteractionDialog()
        }
    }

    // Reminders
    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.toggleReminderDone(reminder)
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.saveReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun getInteractionsForCustomer(customerId: Long) = repository.getInteractionsForCustomer(customerId)
    fun getCustomer(customerId: Long) = repository.getCustomer(customerId)
}
