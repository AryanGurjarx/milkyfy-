package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.local.MilkifyDatabase
import com.example.data.repository.MilkifyRepository
import com.example.ui.admin.AdminAppScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.components.MilkifyTopBar
import com.example.ui.components.NotificationsModalSheet
import com.example.ui.customer.CustomerAppScreen
import com.example.ui.rider.RiderAppScreen
import com.example.ui.theme.MilkifyTheme
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.MilkifyViewModel
import com.example.ui.viewmodel.MilkifyViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MilkifyViewModel by viewModels {
        val database = MilkifyDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = MilkifyRepository(database.milkifyDao())
        MilkifyViewModelFactory(repository, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MilkifyTheme {
                MilkifyMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MilkifyMainApp(viewModel: MilkifyViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val notifications by viewModel.customerNotifications.collectAsState()
    val userFeedback by viewModel.userFeedback.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearFeedback()
            }
        }
    }

    val totalCartCount = cartItems.values.sumOf { it.second }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LoginScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MilkifyTopBar(
                    currentRole = currentRole,
                    onRoleSelected = { role -> viewModel.switchRole(role) },
                    cartItemCount = totalCartCount,
                    notificationCount = notifications.size,
                    isSyncing = isCloudSyncing,
                    onCloudSyncClick = {
                        viewModel.syncFromCloud(showFeedback = true)
                    },
                    onCartClick = {
                        viewModel.selectCustomerTab(CustomerTab.HOME)
                    },
                    onNotificationsClick = {
                        showNotificationsSheet = true
                    },
                    onLogoutClick = {
                        viewModel.logout()
                    },
                    customerLocation = "Civil Lines, Joura (476221)"
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRole) {
                    AppRole.CUSTOMER -> {
                        CustomerAppScreen(viewModel = viewModel)
                    }
                    AppRole.RIDER -> {
                        RiderAppScreen(viewModel = viewModel)
                    }
                    AppRole.ADMIN -> {
                        AdminAppScreen(viewModel = viewModel)
                    }
                }
            }
        }

        if (showNotificationsSheet) {
            NotificationsModalSheet(
                notifications = notifications,
                onDismiss = { showNotificationsSheet = false },
                onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                onClearAll = { viewModel.clearAllNotifications() },
                onDeleteNotification = { id: Long -> viewModel.deleteNotification(id) },
                onSendSampleNotification = {
                    viewModel.adminBroadcastNotification(
                        title = "🌅 Morning Dispatch Live in Joura",
                        message = "Fresh chilled milk (FAT 7.2%) dispatched for delivery! Rider Ramesh Kumar is in your sector.",
                        type = "ORDER"
                    )
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

