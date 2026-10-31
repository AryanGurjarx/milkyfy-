package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.data.local.entities.HisaabEntryEntity
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.data.local.MilkifySessionManager
import com.example.data.local.entities.BillboardBannerEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.GalleryPhotoEntity
import com.example.data.local.entities.LabReportEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RiderEntity
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.ui.theme.MilkifyAmber
import com.example.ui.theme.MilkifyBlue
import com.example.ui.theme.MilkifyBlueContainer
import com.example.ui.theme.MilkifyRed
import com.example.ui.theme.MilkifyRedLight
import com.example.ui.theme.MilkifyYellowContainer
import com.example.ui.components.MilkifyBadge
import com.example.ui.components.MilkifyStatusBadge
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyBorderDark
import com.example.ui.theme.MilkifyCream
import com.example.ui.theme.MilkifyCreamCard
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGold
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyOnGreenContainer
import com.example.ui.theme.MilkifyPrimaryGreen
import com.example.ui.theme.MilkifySurface
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.AdminSection
import com.example.ui.viewmodel.MilkifyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminAppScreen(
    viewModel: MilkifyViewModel,
    modifier: Modifier = Modifier
) {
    val activeSection by viewModel.adminSection.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val incomingOrderAlert by viewModel.incomingOrderAlert.collectAsState()

    LaunchedEffect(activeSection) {
        viewModel.syncFromCloud(showFeedback = false)
    }

    val sections = listOf(
        AdminSection.DASHBOARD,
        AdminSection.ORDERS,
        AdminSection.LOGIN_RECORDS,
        AdminSection.HISAAB,
        AdminSection.CUSTOMERS,
        AdminSection.PRODUCTS,
        AdminSection.RIDERS,
        AdminSection.SUBSCRIPTIONS,
        AdminSection.PAYMENTS,
        AdminSection.REPORTS,
        AdminSection.LAB_REPORTS,
        AdminSection.COUPONS,
        AdminSection.GALLERY,
        AdminSection.SETTINGS
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MilkifyWhite)
    ) {
        // Admin Top Bar
        Surface(
            color = MilkifyWhite,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, MilkifyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MilkifyBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MilkifyWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Milkify Admin Web Panel",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MilkifyBlack
                            )
                            Text(
                                text = "Joura Hub Operations & Khata Ledger",
                                fontSize = 11.sp,
                                color = MilkifyTextMuted
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isCloudSyncing) MilkifyGold.copy(alpha = 0.2f) else MilkifyGreenContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isCloudSyncing) MilkifyGold else MilkifyDarkGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.syncFromCloud(showFeedback = true) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isCloudSyncing) "☁️ Syncing..." else "☁️ Online Cloud",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCloudSyncing) MilkifyBlack else MilkifyDarkGreen
                                )
                            }
                        }
                    }
                }

                // Section Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = sections.indexOf(activeSection).coerceAtLeast(0),
                    containerColor = MilkifyWhite,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        val index = sections.indexOf(activeSection).coerceAtLeast(0)
                        if (index < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = MilkifyBlack
                            )
                        }
                    }
                ) {
                    sections.forEach { section ->
                        val isSelected = activeSection == section
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectAdminSection(section) },
                            text = {
                                Text(
                                    text = section.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MilkifyBlack else MilkifyTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_${section.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Live Incoming Order Alert for Admin (Rings, Vibrates, and displays Instant Action Card)
        incomingOrderAlert?.let { newOrder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "NAYA ONLINE ORDER AAYA!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(onClick = { viewModel.dismissIncomingOrderAlert() }) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Order: ${newOrder.orderNumber} • ₹${newOrder.totalAmount.toInt()} (Cash on Delivery)",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        "Customer: ${newOrder.customerName} (${newOrder.customerPhone})",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        "Address: ${newOrder.deliveryAddress} • Slot: ${newOrder.deliverySlot}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                    Text(
                        "Items: ${newOrder.itemsSummary}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.adminConfirmOrderByNumber(newOrder.orderNumber)
                                viewModel.dismissIncomingOrderAlert()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF1B5E20)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("CONFIRM (स्वीकारें)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.selectAdminSection(AdminSection.ORDERS)
                                viewModel.dismissIncomingOrderAlert()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("VIEW ALL ORDERS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activeSection) {
                AdminSection.DASHBOARD -> AdminDashboardView(viewModel = viewModel)
                AdminSection.ORDERS -> AdminOrdersView(viewModel = viewModel)
                AdminSection.LOGIN_RECORDS -> AdminLoginRecordsView(viewModel = viewModel)
                AdminSection.HISAAB -> AdminHisaabView(viewModel = viewModel)
                AdminSection.CUSTOMERS -> AdminCustomersView(viewModel = viewModel)
                AdminSection.PRODUCTS -> AdminProductsView(viewModel = viewModel)
                AdminSection.RIDERS -> AdminRidersView(viewModel = viewModel)
                AdminSection.SUBSCRIPTIONS -> AdminSubscriptionsView(viewModel = viewModel)
                AdminSection.PAYMENTS -> AdminPaymentsView(viewModel = viewModel)
                AdminSection.REPORTS -> AdminReportsView(viewModel = viewModel)
                AdminSection.LAB_REPORTS -> AdminLabReportsView(viewModel = viewModel)
                AdminSection.COUPONS -> AdminCouponsView(viewModel = viewModel)
                AdminSection.GALLERY -> AdminGalleryAndBillboardView(viewModel = viewModel)
                AdminSection.SETTINGS -> AdminSettingsView(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdminDashboardView(viewModel: MilkifyViewModel) {
    val orders by viewModel.orders.collectAsState()
    val riders by viewModel.riders.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val users by viewModel.users.collectAsState()
    val loginRecords by viewModel.loginRecords.collectAsState()

    val totalRevenue = orders.filter { it.orderStatus == "DELIVERED" }.sumOf { it.totalAmount }
    val deliveredCount = orders.count { it.orderStatus == "DELIVERED" }
    val pendingCount = orders.count { it.orderStatus in listOf("PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY") }
    val activeSubsCount = subscriptions.count { it.status == "ACTIVE" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Operations Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MilkifyTextPrimary)
            Text("Real-time metrics for Joura dairy operations", fontSize = 12.sp, color = MilkifyTextMuted)
        }

        // Top KPI Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    title = "Delivered Orders",
                    value = deliveredCount.toString(),
                    subtitle = "Morning & evening batches",
                    color = MilkifyPrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    title = "Pending Deliveries",
                    value = pendingCount.toString(),
                    subtitle = "Currently out in Joura",
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    title = "Total Revenue",
                    value = "₹${totalRevenue.toInt()}",
                    subtitle = "Delivered COD + Online",
                    color = MilkifyDarkGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    title = "Daily Subscribers",
                    value = activeSubsCount.toString(),
                    subtitle = "Active milk plans",
                    color = Color(0xFF512DA8),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dispatch Status
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Joura Hub Live Dispatch", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Current Batch: Morning 3:00 AM – 11:00 AM Slot", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Active Riders on Duty: ${riders.count { it.isShiftActive }} of ${riders.size}", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Farm Location: Station Road, Ward 4, Joura, Morena (MP)", fontSize = 12.sp, color = MilkifyTextSecondary)
                }
            }
        }

        // User Login Activity Card (Quick shortcut to User Logins tab)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectAdminSection(AdminSection.LOGIN_RECORDS) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MilkifyBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MilkifyWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "User Logins / हालिया लॉगिन",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MilkifyBlack
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MilkifyBlack,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${loginRecords.size} Total",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyWhite,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live audit trail of Customer, Rider & Admin logins",
                                fontSize = 11.sp,
                                color = MilkifyTextSecondary
                            )
                        }
                    }

                    Surface(
                        color = MilkifyLightGrey,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MilkifyBorder)
                    ) {
                        Text(
                            text = "View Logs →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyBlack,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Recent Orders List Preview
        item {
            Text("Recent Customer Orders", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(orders.take(5)) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${order.orderNumber} • ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${order.itemsSummary} • ₹${order.totalAmount.toInt()}", fontSize = 11.sp, color = MilkifyTextSecondary)
                        Text(order.deliveryAddress, fontSize = 10.sp, color = MilkifyTextMuted, maxLines = 1)
                    }
                    MilkifyStatusBadge(order.orderStatus)
                }
            }
        }
    }
}

@Composable
fun AdminKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MilkifyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MilkifyTextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = MilkifyTextSecondary)
        }
    }
}

@Composable
fun AdminOrdersView(viewModel: MilkifyViewModel) {
    val orders by viewModel.orders.collectAsState()
    val riders by viewModel.riders.collectAsState()

    var statusFilter by remember { mutableStateOf("ALL") }
    var orderSearchQuery by remember { mutableStateOf("") }
    var selectedOrderForRiderAssign by remember { mutableStateOf<OrderEntity?>(null) }
    var selectedOrderForStatusChange by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = orders.filter {
        val matchesStatus = if (statusFilter == "ALL") true else it.orderStatus == statusFilter
        val matchesQuery = orderSearchQuery.isBlank() ||
                it.orderNumber.contains(orderSearchQuery, ignoreCase = true) ||
                it.customerName.contains(orderSearchQuery, ignoreCase = true) ||
                it.customerPhone.contains(orderSearchQuery) ||
                it.deliveryAddress.contains(orderSearchQuery, ignoreCase = true)
        matchesStatus && matchesQuery
    }

    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Order Management", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MilkifyGreenContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "LIVE CLOUD SYNC",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyDarkGreen,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text("${filteredOrders.size} orders found • Auto-syncing from customers", fontSize = 11.sp, color = MilkifyTextMuted)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Refresh Button
                IconButton(
                    onClick = { viewModel.syncFromCloud(showFeedback = true) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync Cloud Orders",
                        tint = if (isCloudSyncing) MilkifyGold else MilkifyBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Status Filter Dropdown
                var filterMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { filterMenuExpanded = true }, shape = RoundedCornerShape(8.dp)) {
                        Text("Filter: $statusFilter", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = filterMenuExpanded, onDismissRequest = { filterMenuExpanded = false }) {
                        listOf("ALL", "PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    statusFilter = s
                                    filterMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar for 1000+ orders
        OutlinedTextField(
            value = orderSearchQuery,
            onValueChange = { orderSearchQuery = it },
            placeholder = { Text("🔍 Search orders by name, phone, order #, or village...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredOrders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MilkifyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            MilkifyStatusBadge(order.orderStatus)
                        }

                        Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Address: ${order.deliveryAddress}", fontSize = 11.sp, color = MilkifyTextSecondary)
                        Text("Items: ${order.itemsSummary}", fontSize = 12.sp, color = MilkifyDarkGreen)
                        Text("Slot: ${order.deliverySlot} • Total: ₹${order.totalAmount.toInt()} (${order.paymentMethod})", fontSize = 11.sp, color = MilkifyTextMuted)

                        if (order.riderName != null) {
                            Text("Rider: ${order.riderName} (${order.riderPhone})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { selectedOrderForRiderAssign = order },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (order.riderName == null) "Assign Rider" else "Change Rider", fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { selectedOrderForStatusChange = order },
                                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Update Status", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Assign Rider Dialog
    if (selectedOrderForRiderAssign != null) {
        AlertDialog(
            onDismissRequest = { selectedOrderForRiderAssign = null },
            title = { Text("Assign Delivery Rider") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a delivery partner for order ${selectedOrderForRiderAssign!!.orderNumber}:")
                    riders.forEach { rider ->
                        Surface(
                            color = MilkifyCreamCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.adminAssignRider(selectedOrderForRiderAssign!!.id, rider)
                                    selectedOrderForRiderAssign = null
                                }
                                .padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${rider.vehicleNumber} • ${rider.phone}", fontSize = 11.sp, color = MilkifyTextMuted)
                                }
                                MilkifyBadge(if (rider.isShiftActive) "On Duty" else "Off Duty")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForRiderAssign = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Change Status Dialog
    if (selectedOrderForStatusChange != null) {
        AlertDialog(
            onDismissRequest = { selectedOrderForStatusChange = null },
            title = { Text("Update Order Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED").forEach { status ->
                        Surface(
                            color = if (selectedOrderForStatusChange!!.orderStatus == status) MilkifyGreenContainer else MilkifyCreamCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.adminUpdateOrderStatus(selectedOrderForStatusChange!!.id, status)
                                    selectedOrderForStatusChange = null
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = status,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForStatusChange = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminCustomersView(viewModel: MilkifyViewModel) {
    val users by viewModel.users.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    var customerSearchQuery by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        customerSearchQuery.isBlank() ||
                it.name.contains(customerSearchQuery, ignoreCase = true) ||
                it.phone.contains(customerSearchQuery) ||
                it.address.contains(customerSearchQuery, ignoreCase = true) ||
                (it.referralCode?.contains(customerSearchQuery, ignoreCase = true) == true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Registered Customers (${users.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Live directory scaling to 1000+ customers in Joura", fontSize = 11.sp, color = MilkifyTextMuted)
                }
                IconButton(
                    onClick = { viewModel.syncFromCloud(showFeedback = true) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync Cloud Customers",
                        tint = if (isCloudSyncing) MilkifyGold else MilkifyBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerSearchQuery,
                onValueChange = { customerSearchQuery = it },
                placeholder = { Text("🔍 Search 1000+ customers by name, mobile, address...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }

        if (filteredUsers.isEmpty()) {
            item {
                Surface(
                    color = MilkifyLightGrey,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No customers found", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Customers who login or place orders will appear here automatically.", fontSize = 11.sp, color = MilkifyTextSecondary)
                    }
                }
            }
        }

        items(filteredUsers) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Phone: ${user.phone}", fontSize = 12.sp, color = MilkifyTextSecondary)
                        Text("Address: ${user.address}", fontSize = 11.sp, color = MilkifyTextMuted)
                        Text("Referral Code: ${user.referralCode ?: "None"}", fontSize = 11.sp, color = MilkifyPrimaryGreen)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Wallet", fontSize = 10.sp, color = MilkifyTextMuted)
                        Text("₹${user.walletBalance.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MilkifyDarkGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductsView(viewModel: MilkifyViewModel) {
    val products by viewModel.products.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var productToEditPrice by remember { mutableStateOf<ProductEntity?>(null) }
    var productToEditDetails by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Buffalo Milk", "Desi Ghee", "Cow Milk")
    val filteredProducts = if (selectedCategoryFilter == "All") {
        products
    } else {
        products.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with "+ Add New Product" Action
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Product & Rate Management",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MilkifyBlack
                            )
                            Text(
                                text = "Add new dairy products & change live market selling rates. Available exclusively in Admin Panel.",
                                fontSize = 11.sp,
                                color = MilkifyTextSecondary,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MilkifyBlack,
                                contentColor = MilkifyWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_add_product_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) MilkifyBlack else MilkifyWhite,
                                border = BorderStroke(1.dp, if (isSelected) MilkifyBlack else MilkifyBorder),
                                modifier = Modifier.clickable { selectedCategoryFilter = cat }
                            ) {
                                val count = if (cat == "All") products.size else products.count { it.category.equals(cat, ignoreCase = true) }
                                Text(
                                    text = "$cat ($count)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MilkifyWhite else MilkifyBlack,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Product Items
        if (filteredProducts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    border = BorderStroke(1.dp, MilkifyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = MilkifyTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No products in $selectedCategoryFilter", fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        Text("Click '+ Add Product' to create one", fontSize = 12.sp, color = MilkifyTextMuted)
                    }
                }
            }
        }

        items(filteredProducts) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MilkifyLightGrey,
                                    border = BorderStroke(1.dp, MilkifyBorder)
                                ) {
                                    Text(
                                        text = product.category.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyBlack,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (product.badge.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MilkifyBlack
                                    ) {
                                        Text(
                                            text = product.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MilkifyWhite,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MilkifyBlack
                            )

                            Text(
                                text = "Pack Size: ${product.volumeLabel} (${product.volumeMl} ml/g) • Stock: ${product.stock} units",
                                fontSize = 12.sp,
                                color = MilkifyTextSecondary
                            )

                            if (product.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.description,
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted,
                                    maxLines = 2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Price Display Block
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current Rate", fontSize = 10.sp, color = MilkifyTextMuted)
                            Text(
                                text = "₹${product.price.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MilkifyBlack
                            )
                            Text(
                                text = "per ${product.volumeLabel}",
                                fontSize = 10.sp,
                                color = MilkifyTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MilkifyBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Controls: Rate Change, Edit Details, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (product.isAvailable) "Active" else "Inactive",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MilkifyBlack
                            )
                            Switch(
                                checked = product.isAvailable,
                                onCheckedChange = { isChecked ->
                                    viewModel.adminSaveProduct(product.copy(isAvailable = isChecked))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MilkifyWhite,
                                    checkedTrackColor = MilkifyBlack,
                                    uncheckedThumbColor = MilkifyWhite,
                                    uncheckedTrackColor = MilkifyBorderDark
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Rate Change Button
                            Button(
                                onClick = { productToEditPrice = product },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MilkifyBlack,
                                    contentColor = MilkifyWhite
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("admin_rate_change_${product.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Edit Details Button
                            OutlinedButton(
                                onClick = { productToEditDetails = product },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MilkifyBorderDark)
                            ) {
                                Text("Edit Details", fontSize = 11.sp, color = MilkifyBlack, fontWeight = FontWeight.SemiBold)
                            }

                            // Delete Button
                            IconButton(
                                onClick = { productToDelete = product },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Product",
                                    tint = MilkifyBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- RATE CHANGE DIALOG (रेट संशोधन) ---
    if (productToEditPrice != null) {
        val prod = productToEditPrice!!
        var priceInput by remember(prod) { mutableStateOf(prod.price.toInt().toString()) }

        AlertDialog(
            onDismissRequest = { productToEditPrice = null },
            title = {
                Text(
                    text = "Change Rate: ${prod.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MilkifyBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pack: ${prod.volumeLabel} • Current Selling Rate: ₹${prod.price.toInt()}",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )

                    // Quick Increment Chips
                    Text("Quick Adjust Rate:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(+1, +2, +5, -1, -5).forEach { delta ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MilkifyLightGrey,
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier.clickable {
                                    val current = priceInput.toDoubleOrNull() ?: prod.price
                                    val adjusted = (current + delta).coerceAtLeast(1.0)
                                    priceInput = adjusted.toInt().toString()
                                }
                            ) {
                                Text(
                                    text = if (delta > 0) "+₹$delta" else "-₹${-delta}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("New Selling Rate (₹)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_rate_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorder,
                            focusedLabelColor = MilkifyBlack
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MilkifyLightGrey,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MilkifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = MilkifyBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🌐 Real-Time Online Cloud Sync: यह रेट सर्वर पर तुरंत अपडेट होगा और सभी ग्राहकों के मोबाइल में लाइव बदल जाएगा।",
                                fontSize = 11.sp,
                                color = MilkifyBlack,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = priceInput.toDoubleOrNull()
                        if (newPrice != null && newPrice > 0) {
                            viewModel.adminUpdateProductPrice(prod.id, newPrice)
                            productToEditPrice = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MilkifyBlack,
                        contentColor = MilkifyWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save & Sync Online", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToEditPrice = null }) {
                    Text("Cancel", color = MilkifyBlack)
                }
            }
        )
    }

    // --- ADD NEW PRODUCT DIALOG (नया प्रोडक्ट जोड़ें) ---
    if (showAddProductDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Buffalo Milk") }
        var volumeLabel by remember { mutableStateOf("1 L") }
        var volumeMl by remember { mutableStateOf("1000") }
        var price by remember { mutableStateOf("") }
        var stock by remember { mutableStateOf("150") }
        var badge by remember { mutableStateOf("Farm Fresh") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = {
                Text(
                    text = "Add New Dairy Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MilkifyBlack
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Buffalo Milk", "Desi Ghee", "Cow Milk", "Dairy").forEach { cat ->
                                val isSelected = category == cat
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) MilkifyBlack else MilkifyWhite,
                                    border = BorderStroke(1.dp, if (isSelected) MilkifyBlack else MilkifyBorder),
                                    modifier = Modifier.clickable {
                                        category = cat
                                        if (cat == "Desi Ghee" && name.isBlank()) {
                                            name = "Pure Desi Danedar Ghee"
                                            volumeLabel = "1 kg"
                                            volumeMl = "1000"
                                            price = "700"
                                            badge = "Traditional Bilona"
                                        } else if (cat == "Buffalo Milk" && name.isBlank()) {
                                            name = "Farm Fresh Buffalo Milk"
                                            volumeLabel = "1 L"
                                            volumeMl = "1000"
                                            price = "69"
                                            badge = "FAT 7.2%"
                                        }
                                    }
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MilkifyWhite else MilkifyBlack,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Product Name") },
                            placeholder = { Text("e.g. Pure Fresh Buffalo Milk") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MilkifyBlack,
                                unfocusedBorderColor = MilkifyBorder
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = volumeLabel,
                                onValueChange = { volumeLabel = it },
                                label = { Text("Pack Size") },
                                placeholder = { Text("1 L / 500 g") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MilkifyBlack,
                                    unfocusedBorderColor = MilkifyBorder
                                )
                            )
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Rate (₹)") },
                                placeholder = { Text("69 / 700") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MilkifyBlack,
                                    unfocusedBorderColor = MilkifyBorder
                                )
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = volumeMl,
                                onValueChange = { volumeMl = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Qty (ml / g)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MilkifyBlack,
                                    unfocusedBorderColor = MilkifyBorder
                                )
                            )
                            OutlinedTextField(
                                value = stock,
                                onValueChange = { stock = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Stock (Units)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MilkifyBlack,
                                    unfocusedBorderColor = MilkifyBorder
                                )
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = badge,
                            onValueChange = { badge = it },
                            label = { Text("Badge / Tag") },
                            placeholder = { Text("e.g. Rich FAT 7.2%") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MilkifyBlack,
                                unfocusedBorderColor = MilkifyBorder
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            placeholder = { Text("Fresh from Joura dairy farms...") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MilkifyBlack,
                                unfocusedBorderColor = MilkifyBorder
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedPrice = price.toDoubleOrNull() ?: 0.0
                        val parsedMl = volumeMl.toIntOrNull() ?: 1000
                        val parsedStock = stock.toIntOrNull() ?: 100
                        if (name.isNotBlank() && parsedPrice > 0) {
                            val newProduct = ProductEntity(
                                name = name.trim(),
                                category = category,
                                volumeLabel = volumeLabel.trim().ifBlank { "1 L" },
                                volumeMl = parsedMl,
                                price = parsedPrice,
                                stock = parsedStock,
                                badge = badge.trim(),
                                description = description.trim().ifBlank { "Farm-fresh quality dairy product from Joura hub." },
                                freshnessInfo = "Chilled immediately at Joura central facility.",
                                sourceFarm = "Milkify Joura Hub & Dairy Partners, Morena (MP)",
                                labTestingInfo = "Batch-tested daily for purity and zero adulteration.",
                                deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                                isAvailable = true
                            )
                            viewModel.adminSaveProduct(newProduct)
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MilkifyBlack,
                        contentColor = MilkifyWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add Product", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel", color = MilkifyBlack)
                }
            }
        )
    }

    // --- EDIT PRODUCT DETAILS DIALOG ---
    if (productToEditDetails != null) {
        val prod = productToEditDetails!!
        var name by remember(prod) { mutableStateOf(prod.name) }
        var category by remember(prod) { mutableStateOf(prod.category) }
        var volumeLabel by remember(prod) { mutableStateOf(prod.volumeLabel) }
        var stock by remember(prod) { mutableStateOf(prod.stock.toString()) }
        var badge by remember(prod) { mutableStateOf(prod.badge) }
        var description by remember(prod) { mutableStateOf(prod.description) }

        AlertDialog(
            onDismissRequest = { productToEditDetails = null },
            title = {
                Text(
                    text = "Edit Product Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MilkifyBlack
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Product Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = volumeLabel,
                                onValueChange = { volumeLabel = it },
                                label = { Text("Pack Size") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = stock,
                                onValueChange = { stock = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Stock (Units)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = badge,
                            onValueChange = { badge = it },
                            label = { Text("Badge / Tag") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedStock = stock.toIntOrNull() ?: prod.stock
                        viewModel.adminSaveProduct(
                            prod.copy(
                                name = name.trim().ifBlank { prod.name },
                                volumeLabel = volumeLabel.trim().ifBlank { prod.volumeLabel },
                                stock = parsedStock,
                                badge = badge.trim(),
                                description = description.trim()
                            )
                        )
                        productToEditDetails = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MilkifyBlack,
                        contentColor = MilkifyWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToEditDetails = null }) {
                    Text("Cancel", color = MilkifyBlack)
                }
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product?", fontWeight = FontWeight.Bold, color = MilkifyBlack) },
            text = {
                Text("Are you sure you want to remove '${prod.name} (${prod.volumeLabel})' from the catalog?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteProduct(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MilkifyBlack,
                        contentColor = MilkifyWhite
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = MilkifyBlack)
                }
            }
        )
    }
}

@Composable
fun AdminRidersView(viewModel: MilkifyViewModel) {
    val riders by viewModel.riders.collectAsState()
    var showAddRiderDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Delivery Fleet (Joura Hub)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Manage active riders, shifts, and assigned vehicles", fontSize = 11.sp, color = MilkifyTextMuted)
                }
                Button(
                    onClick = { showAddRiderDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Rider", fontSize = 11.sp)
                }
            }
        }

        items(riders) { rider ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        MilkifyBadge(if (rider.isShiftActive) "On Duty" else "Off Duty")
                    }
                    Text("Phone: ${rider.phone} • Vehicle: ${rider.vehicleNumber}", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("Today's Deliveries: ${rider.todayDeliveries} • Earnings: ₹${rider.todayEarnings.toInt()}", fontSize = 11.sp, color = MilkifyDarkGreen)
                    Text("Lifetime Deliveries: ${rider.totalDeliveries} • Total Paid: ₹${rider.totalEarnings.toInt()}", fontSize = 11.sp, color = MilkifyTextMuted)
                }
            }
        }
    }

    if (showAddRiderDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var vehicle by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddRiderDialog = false },
            title = { Text("Register New Delivery Rider") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Rider Name") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, singleLine = true)
                    OutlinedTextField(value = vehicle, onValueChange = { vehicle = it }, label = { Text("Vehicle Number (e.g. MP-06-...)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            viewModel.adminAssignRider(
                                0,
                                RiderEntity(name = name, phone = phone, vehicleNumber = vehicle)
                            )
                            showAddRiderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8))
                ) {
                    Text("Save Rider")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRiderDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminSubscriptionsView(viewModel: MilkifyViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Customer Milk Subscriptions (${subscriptions.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Manage recurring daily and alternate day milk deliveries", fontSize = 11.sp, color = MilkifyTextMuted)
        }

        items(subscriptions) { sub ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sub.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        MilkifyStatusBadge(sub.status)
                    }
                    Text("${sub.productName} • ${sub.quantityLiters}L/day", fontSize = 12.sp, color = MilkifyDarkGreen, fontWeight = FontWeight.SemiBold)
                    Text("Schedule: ${sub.frequency} • ${sub.deliverySlot}", fontSize = 11.sp, color = MilkifyTextSecondary)
                    Text("Address: ${sub.deliveryAddress}", fontSize = 11.sp, color = MilkifyTextMuted)
                    Text("Active From: ${sub.startDate} to ${sub.endDate} • ₹${sub.pricePerDay.toInt()}/day", fontSize = 11.sp, color = MilkifyPrimaryGreen)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (sub.status == "ACTIVE") {
                            OutlinedButton(
                                onClick = { viewModel.pauseSubscription(sub.id) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Pause", fontSize = 10.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.resumeSubscription(sub.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Resume", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPaymentsView(viewModel: MilkifyViewModel) {
    val orders by viewModel.orders.collectAsState()
    val hisaabEntries by viewModel.allHisaabEntries.collectAsState()

    val codTotal = orders.filter { it.orderStatus == "DELIVERED" }.sumOf { it.totalAmount }
    val codPending = orders.filter { it.orderStatus != "DELIVERED" && it.orderStatus != "CANCELLED" }.sumOf { it.totalAmount }
    val totalCashLogged = hisaabEntries.filter { it.entryType == "CASH_PAID" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Offline Cash Reconciliation", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MilkifyBlack)
            Text("Doorstep Cash On Delivery (COD) collection and reconciliation in Joura", fontSize = 11.sp, color = MilkifyTextMuted)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    title = "COD Delivered",
                    value = "₹${codTotal.toInt()}",
                    subtitle = "Delivered orders",
                    color = MilkifyBlack,
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    title = "Pending with Riders",
                    value = "₹${codPending.toInt()}",
                    subtitle = "Active deliveries",
                    color = MilkifyBlack,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.5.dp, MilkifyBlack)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Strictly Offline Deal Policy (Joura Hub)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MilkifyBlack)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Cash on Delivery (COD): Only physical cash collected at customer doorstep.", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Zero Online Gateways: All transactions are settled in cash to avoid internet/gateway errors in rural Joura.", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Hub Ledger Audit: Riders deposit physical cash at hub counter daily by 11:30 AM (morning) and 8:30 PM (evening).", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Customer Khata: All milk bills and cash received are tracked in the 'Customer Hisaab / Khata' tab.", fontSize = 12.sp, color = MilkifyTextSecondary)
                }
            }
        }
    }
}

@Composable
fun AdminHisaabView(viewModel: MilkifyViewModel) {
    val hisaabEntries by viewModel.allHisaabEntries.collectAsState()
    val users by viewModel.users.collectAsState()
    val orders by viewModel.orders.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var selectedCustomerForDialog by remember { mutableStateOf<String?>(null) }
    var paymentAmountInput by remember { mutableStateOf("") }
    var paymentNoteInput by remember { mutableStateOf("Cash handed at Joura hub counter") }
    var paymentDateInput by remember {
        mutableStateOf(java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
    }

    var viewingCustomerEntriesPhone by remember { mutableStateOf<String?>(null) }

    // Aggregate totals across all customers
    val totalBilled = hisaabEntries.filter { it.entryType == "BILL" }.sumOf { it.amount }
    val totalCashReceived = hisaabEntries.filter { it.entryType == "CASH_PAID" }.sumOf { it.amount }
    val netOutstandingDue = (totalBilled - totalCashReceived).coerceAtLeast(0.0)

    // Distinct customer phones from hisaab and users
    val customerPhones = (users.map { it.phone } + hisaabEntries.map { it.customerPhone } + orders.map { it.customerPhone }).distinct()

    val filteredCustomers = customerPhones.filter { phone ->
        val customerName = users.firstOrNull { it.phone == phone }?.name
            ?: hisaabEntries.firstOrNull { it.customerPhone == phone }?.customerName
            ?: orders.firstOrNull { it.customerPhone == phone }?.customerName
            ?: "Customer"
        searchQuery.isBlank() || customerName.contains(searchQuery, ignoreCase = true) || phone.contains(searchQuery)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Customer Hisaab / Khata (खाता बही)", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MilkifyBlack)
                    Text("Central Joura offline milk accounts, cash received & market ledger", fontSize = 11.sp, color = MilkifyTextMuted)
                }
                Button(
                    onClick = {
                        selectedCustomerForDialog = null
                        paymentAmountInput = ""
                        paymentNoteInput = "Cash handed at hub counter"
                        showRecordPaymentDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Cash", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary KPI Cards in Monochrome Two-Color
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    border = BorderStroke(1.5.dp, MilkifyBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Milk Billed", fontSize = 11.sp, color = MilkifyTextMuted)
                        Text("₹${totalBilled.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MilkifyBlack)
                        Text("Offline Doorstep Sales", fontSize = 10.sp, color = MilkifyTextSecondary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    border = BorderStroke(1.5.dp, MilkifyBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Cash Received", fontSize = 11.sp, color = MilkifyTextMuted)
                        Text("₹${totalCashReceived.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MilkifyBlack)
                        Text("Collected in Hand", fontSize = 10.sp, color = MilkifyTextSecondary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    border = BorderStroke(1.5.dp, MilkifyBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Market Due", fontSize = 11.sp, color = MilkifyTextMuted)
                        Text("₹${netOutstandingDue.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MilkifyBlack)
                        Text("Outstanding Khata", fontSize = 10.sp, color = MilkifyTextSecondary)
                    }
                }
            }
        }

        // Search Filter Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search customer by name or phone...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MilkifyBlack,
                    unfocusedBorderColor = MilkifyBorderDark,
                    focusedLabelColor = MilkifyBlack
                )
            )
        }

        // Customer Ledger List
        items(filteredCustomers) { phone ->
            val custEntries = hisaabEntries.filter { it.customerPhone == phone }
            val custBilled = custEntries.filter { it.entryType == "BILL" }.sumOf { it.amount }
            val custPaid = custEntries.filter { it.entryType == "CASH_PAID" }.sumOf { it.amount }
            val custDue = (custBilled - custPaid).coerceAtLeast(0.0)

            val custName = users.firstOrNull { it.phone == phone }?.name
                ?: custEntries.firstOrNull()?.customerName
                ?: orders.firstOrNull { it.customerPhone == phone }?.customerName
                ?: "Customer"

            val custAddress = users.firstOrNull { it.phone == phone }?.address
                ?: orders.firstOrNull { it.customerPhone == phone }?.deliveryAddress
                ?: "Joura, Morena"

            val isViewingEntries = viewingCustomerEntriesPhone == phone

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBlack),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = custName, fontWeight = FontWeight.Black, fontSize = 15.sp, color = MilkifyBlack)
                            Text(text = "$phone • $custAddress", fontSize = 11.sp, color = MilkifyTextSecondary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (custDue > 0) MilkifyBlack else MilkifyLightGrey,
                            border = BorderStroke(1.dp, MilkifyBlack)
                        ) {
                            Text(
                                text = if (custDue > 0) "Due: ₹${custDue.toInt()}" else "Settled (₹0)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (custDue > 0) MilkifyWhite else MilkifyBlack,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Milk Billed: ₹${custBilled.toInt()}", fontSize = 12.sp, color = MilkifyTextSecondary)
                        Text(text = "Cash Paid: ₹${custPaid.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MilkifyBlack)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MilkifyBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewingCustomerEntriesPhone = if (isViewingEntries) null else phone
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyBlack),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isViewingEntries) "Hide Parchi" else "View Parchi (${custEntries.size})", color = MilkifyBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                selectedCustomerForDialog = phone
                                paymentAmountInput = ""
                                paymentNoteInput = "Cash collected from $custName"
                                showRecordPaymentDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Cash Paid (जमा)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Expanded Parchi / Ledger Transactions List
                    if (isViewingEntries) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Khata Transactions (खाता पर्ची):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (custEntries.isEmpty()) {
                            Text("No offline hisaab transactions recorded yet.", fontSize = 11.sp, color = MilkifyTextMuted)
                        } else {
                            custEntries.forEach { entry ->
                                Surface(
                                    color = MilkifyLightGrey,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, MilkifyBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (entry.entryType == "BILL") "BILL (दूध पर्ची)" else "CASH (जमा रसीद)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = MilkifyBlack
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(text = entry.date, fontSize = 10.sp, color = MilkifyTextMuted)
                                            }
                                            Text(text = entry.note, fontSize = 11.sp, color = MilkifyTextSecondary)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (entry.entryType == "BILL") "+₹${entry.amount.toInt()}" else "-₹${entry.amount.toInt()}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = MilkifyBlack
                                            )
                                            IconButton(
                                                onClick = { viewModel.deleteHisaabEntry(entry) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MilkifyBlack, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Record Cash Payment Dialog
    if (showRecordPaymentDialog) {
        val targetPhone = selectedCustomerForDialog ?: (customerPhones.firstOrNull() ?: "+91 98765 43210")
        val targetName = users.firstOrNull { it.phone == targetPhone }?.name
            ?: hisaabEntries.firstOrNull { it.customerPhone == targetPhone }?.customerName
            ?: "Customer"

        var localSelectedPhone by remember { mutableStateOf(targetPhone) }

        AlertDialog(
            onDismissRequest = { showRecordPaymentDialog = false },
            title = {
                Text("Record Cash Received / नकद जमा", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MilkifyBlack)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Customer: $targetName ($localSelectedPhone)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)

                    OutlinedTextField(
                        value = paymentAmountInput,
                        onValueChange = { paymentAmountInput = it },
                        label = { Text("Amount Received (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark,
                            focusedLabelColor = MilkifyBlack
                        )
                    )

                    OutlinedTextField(
                        value = paymentDateInput,
                        onValueChange = { paymentDateInput = it },
                        label = { Text("Date (e.g. 24 Sep 2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark,
                            focusedLabelColor = MilkifyBlack
                        )
                    )

                    OutlinedTextField(
                        value = paymentNoteInput,
                        onValueChange = { paymentNoteInput = it },
                        label = { Text("Note / Receipt Reference") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark,
                            focusedLabelColor = MilkifyBlack
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = paymentAmountInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.recordCustomerCashPayment(
                                phone = localSelectedPhone,
                                name = targetName,
                                amount = amount,
                                note = paymentNoteInput,
                                date = paymentDateInput
                            )
                            showRecordPaymentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite)
                ) {
                    Text("Record Cash", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRecordPaymentDialog = false },
                    border = BorderStroke(1.dp, MilkifyBlack)
                ) {
                    Text("Cancel", color = MilkifyBlack)
                }
            },
            containerColor = MilkifyWhite
        )
    }
}

@Composable
fun AdminReportsView(viewModel: MilkifyViewModel) {
    val orders by viewModel.orders.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Sales & Volume Reports", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Analysis of milk sold across Joura city", fontSize = 11.sp, color = MilkifyTextMuted)
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Volume Dispatched Today", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("620 Litres Pure Cow Milk", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MilkifyPrimaryGreen)
                    Text("• Morning Batch (3 AM – 11 AM): 480 L", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Evening Batch (3 PM – 8 PM): 140 L", fontSize = 12.sp, color = MilkifyTextSecondary)
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.showFeedback("Report exported as CSV successfully.") },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Export Monthly Sales Summary (CSV)")
            }
        }
    }
}

@Composable
fun AdminLabReportsView(viewModel: MilkifyViewModel) {
    val labReports by viewModel.allLabReportsAdmin.collectAsState()
    var showAddReportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Milk Quality & Lab Testing", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Official test certificates displayed to customers", fontSize = 11.sp, color = MilkifyTextMuted)
                }
                Button(
                    onClick = { showAddReportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Lab Report", fontSize = 11.sp)
                }
            }
        }

        items(labReports) { r ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(r.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (r.isLatest) MilkifyBadge("Latest")
                    }
                    Text("Date: ${r.reportDate} • Lab: ${r.laboratoryName}", fontSize = 11.sp, color = MilkifyTextMuted)
                    Text("FAT: ${r.fatPercentage}% | SNF: ${r.snfPercentage}% | Chilled: ${r.temperatureAtCollection}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyDarkGreen)
                    Text("Adulteration: ${r.adulterationStatus}", fontSize = 11.sp, color = MilkifyTextSecondary)
                    Text("Certified By: ${r.certifiedBy}", fontSize = 11.sp, color = MilkifyTextMuted)
                }
            }
        }
    }

    if (showAddReportDialog) {
        var labName by remember { mutableStateOf("MP State Dairy Quality Control Lab, Morena") }
        var fat by remember { mutableStateOf("4.8") }
        var snf by remember { mutableStateOf("8.9") }
        var certifiedBy by remember { mutableStateOf("Dr. S. K. Verma, Quality Assurance Officer") }

        AlertDialog(
            onDismissRequest = { showAddReportDialog = false },
            title = { Text("Publish Daily Lab Purity Report") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("FAT % (e.g. 4.8)") })
                    OutlinedTextField(value = snf, onValueChange = { snf = it }, label = { Text("SNF % (e.g. 8.9)") })
                    OutlinedTextField(value = labName, onValueChange = { labName = it }, label = { Text("Testing Laboratory") })
                    OutlinedTextField(value = certifiedBy, onValueChange = { certifiedBy = it }, label = { Text("Certified Analyst") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val report = LabReportEntity(
                            reportDate = "19 Sep 2026",
                            title = "Daily Batch Purity & Microbiological Test Report",
                            laboratoryName = labName,
                            fatPercentage = fat.toDoubleOrNull() ?: 4.8,
                            snfPercentage = snf.toDoubleOrNull() ?: 8.9,
                            microbialCount = "< 10,000 CFU/ml (Grade A - Premium Quality)",
                            adulterationStatus = "Zero Adulterants Detected (Urea, Starch, Detergent, Neutralizers: NEGATIVE)",
                            temperatureAtCollection = "3.8°C Instant Chilling",
                            certifiedBy = certifiedBy,
                            isVisibleToCustomer = true,
                            isLatest = true
                        )
                        viewModel.adminAddLabReport(report)
                        showAddReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
                ) {
                    Text("Publish to App")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReportDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminCouponsView(viewModel: MilkifyViewModel) {
    val coupons by viewModel.allCouponsAdmin.collectAsState()
    var showAddCouponDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Promotions & Coupons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Discount codes for customers in Joura", fontSize = 11.sp, color = MilkifyTextMuted)
                }
                Button(
                    onClick = { showAddCouponDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Coupon", fontSize = 11.sp)
                }
            }
        }

        items(coupons) { c ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(c.code, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MilkifyPrimaryGreen)
                        Text(c.description, fontSize = 12.sp, color = MilkifyTextSecondary)
                        Text("Min order: ₹${c.minOrder.toInt()} • Max discount: ₹${c.maxDiscount.toInt()}", fontSize = 11.sp, color = MilkifyTextMuted)
                    }
                    MilkifyBadge(if (c.isActive) "Active" else "Expired")
                }
            }
        }
    }

    if (showAddCouponDialog) {
        var code by remember { mutableStateOf("") }
        var discount by remember { mutableStateOf("20") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("Create Discount Coupon") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Coupon Code (e.g. MILK25)") })
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Discount % (e.g. 20)") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank()) {
                            val cp = CouponEntity(
                                code = code,
                                discountType = "PERCENTAGE",
                                discountValue = discount.toDoubleOrNull() ?: 20.0,
                                minOrder = 80.0,
                                maxDiscount = 50.0,
                                description = desc.ifBlank { "Get $discount% off on fresh milk" },
                                isActive = true
                            )
                            viewModel.adminAddCoupon(cp)
                            showAddCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
                ) {
                    Text("Save Coupon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCouponDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminSettingsView(viewModel: MilkifyViewModel) {
    val adminEmail by viewModel.adminEmail.collectAsState()
    val adminPassword by viewModel.adminPassword.collectAsState()
    val supportPhone by viewModel.supportPhone.collectAsState()
    val whatsappNumber by viewModel.whatsappNumber.collectAsState()
    val announcement by viewModel.announcementBanner.collectAsState()
    val hubAddress by viewModel.hubAddress.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val cloudDiag by viewModel.cloudDiagnostics.collectAsState()
    val cloudBackendConfig by viewModel.cloudBackendConfig.collectAsState()

    var emailInput by remember(adminEmail) { mutableStateOf(adminEmail) }
    var passwordInput by remember(adminPassword) { mutableStateOf(adminPassword) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var phoneInput by remember(supportPhone) { mutableStateOf(supportPhone) }
    var whatsappInput by remember(whatsappNumber) { mutableStateOf(whatsappNumber) }

    var announcementInput by remember(announcement) { mutableStateOf(announcement) }
    var hubAddressInput by remember(hubAddress) { mutableStateOf(hubAddress) }

    var backendTypeInput by remember(cloudBackendConfig) { mutableStateOf(cloudBackendConfig.backendType) }
    var firebaseUrlInput by remember(cloudBackendConfig) { mutableStateOf(cloudBackendConfig.firebaseUrl) }
    var firebaseAuthInput by remember(cloudBackendConfig) { mutableStateOf(cloudBackendConfig.firebaseAuth) }
    var supabaseUrlInput by remember(cloudBackendConfig) { mutableStateOf(cloudBackendConfig.supabaseUrl) }
    var supabaseKeyInput by remember(cloudBackendConfig) { mutableStateOf(cloudBackendConfig.supabaseKey) }
    var showFirebaseGuide by remember { mutableStateOf(false) }

    var saveFeedback by remember { mutableStateOf<String?>(null) }

    if (showFirebaseGuide) {
        AlertDialog(
            onDismissRequest = { showFirebaseGuide = false },
            title = {
                Text("🔥 Firebase 1000+ Customers Setup (2 Mins)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Google Firebase का फ्री टियर (Spark Plan) 100% मुफ़्त है और 50,000 सक्रिय ग्राहकों तथा लाखों ऑर्डर्स को संभाल सकता है:",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                    Surface(color = MilkifyLightGrey, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("1️⃣ console.firebase.google.com पर जाएं और 'Create a project' दबाएं।", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("2️⃣ 'Build' ➔ 'Realtime Database' पर क्लिक करके 'Create Database' करें।", fontSize = 11.sp)
                            Text("3️⃣ Rules टैब में जाएं और read व write को true कर दें:\n   {\".read\": true, \".write\": true}", fontSize = 11.sp, color = MilkifyDarkGreen, fontWeight = FontWeight.Bold)
                            Text("4️⃣ डेटाबेस का URL कॉपी करें (जैसे: https://your-app-default-rtdb.firebaseio.com) और यहाँ पेस्ट करें।", fontSize = 11.sp)
                            Text("5️⃣ 'SAVE & BROADCAST' दबाएं। सभी 1000 ग्राहकों के फोन तुरंत कनेक्ट हो जाएंगे!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFirebaseGuide = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
                ) {
                    Text("OK, समझ गया", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Admin Master Control Center", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MilkifyBlack)
            Text("Full administrative rights: edit admin credentials, customer helplines, store notices, and hub logistics.", fontSize = 12.sp, color = MilkifyTextSecondary)

            if (saveFeedback != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MilkifyGreenContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MilkifyPrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MilkifyDarkGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(saveFeedback!!, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MilkifyDarkGreen)
                    }
                }
            }
        }

        // Section 0: Online Cloud Setting & Enterprise Scalability
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.5.dp, if (cloudBackendConfig.backendType == "FIREBASE") MilkifyGold else MilkifyPrimaryGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (cloudBackendConfig.backendType == "FIREBASE") MilkifyGold.copy(alpha = 0.2f) else MilkifyGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (cloudBackendConfig.backendType == "FIREBASE") MilkifyGold else MilkifyDarkGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Enterprise Cloud Backend (1000+ Scale)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MilkifyBlack
                                )
                                Text(
                                    "1000+ ग्राहकों के लिए अनलिमिटेड क्लाउड डेटाबेस",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                            }
                        }
                        Surface(
                            color = when (cloudBackendConfig.backendType) {
                                "FIREBASE" -> MilkifyGold.copy(alpha = 0.25f)
                                "SUPABASE" -> MilkifyBlueContainer
                                else -> MilkifyGreenContainer
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, when (cloudBackendConfig.backendType) {
                                "FIREBASE" -> MilkifyGold
                                "SUPABASE" -> MilkifyBlue
                                else -> MilkifyPrimaryGreen
                            })
                        ) {
                            Text(
                                when (cloudBackendConfig.backendType) {
                                    "FIREBASE" -> "🔥 Firebase (10k+ Scale)"
                                    "SUPABASE" -> "⚡ Supabase Active"
                                    else -> "🟢 Built-in Hub"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (cloudBackendConfig.backendType) {
                                    "FIREBASE" -> MilkifyBlack
                                    "SUPABASE" -> MilkifyBlue
                                    else -> MilkifyDarkGreen
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Backend Engine Selector
                    Text("Select Cloud Backend Engine / बैकएंड इंजन चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (backendTypeInput == "FIREBASE") MilkifyGold.copy(alpha = 0.2f) else MilkifyLightGrey,
                            border = BorderStroke(1.5.dp, if (backendTypeInput == "FIREBASE") MilkifyGold else Color.Transparent),
                            modifier = Modifier
                                .weight(1.3f)
                                .clickable { backendTypeInput = "FIREBASE" }
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 Firebase", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                Text("1000+ Customers", fontSize = 9.sp, color = MilkifyTextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (backendTypeInput == "AUTO") MilkifyGreenContainer else MilkifyLightGrey,
                            border = BorderStroke(1.5.dp, if (backendTypeInput == "AUTO") MilkifyDarkGreen else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { backendTypeInput = "AUTO" }
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌐 Built-in", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                Text("Auto Hub", fontSize = 9.sp, color = MilkifyTextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (backendTypeInput == "SUPABASE") MilkifyBlueContainer else MilkifyLightGrey,
                            border = BorderStroke(1.5.dp, if (backendTypeInput == "SUPABASE") MilkifyBlue else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { backendTypeInput = "SUPABASE" }
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚡ Supabase", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                Text("PostgreSQL", fontSize = 9.sp, color = MilkifyTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (backendTypeInput == "FIREBASE") {
                        Surface(
                            color = MilkifyGold.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyGold.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔥 Google Firebase Configuration", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                    Text(
                                        "📖 फ्री गाइड देखें",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyDarkGreen,
                                        modifier = Modifier.clickable { showFirebaseGuide = true }
                                    )
                                }
                                OutlinedTextField(
                                    value = firebaseUrlInput,
                                    onValueChange = { firebaseUrlInput = it },
                                    label = { Text("Firebase Realtime Database URL") },
                                    placeholder = { Text("https://milkyfi-default-rtdb.firebaseio.com") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MilkifyLightGrey,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            firebaseUrlInput = "https://milkyfi-default-rtdb.firebaseio.com"
                                        }
                                    ) {
                                        Text(
                                            "📋 Auto-fill Joura Hub standard URL",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MilkifyDarkGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                OutlinedTextField(
                                    value = firebaseAuthInput,
                                    onValueChange = { firebaseAuthInput = it },
                                    label = { Text("Database Secret / Auth Key (Optional)") },
                                    placeholder = { Text("Public test rules allow blank") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Text(
                                    "✨ 1000+ ग्राहक, दर्जनों राइडर्स और एडमिन का सारा डेटा रियल-टाइम में सुरक्षित रहेगा।",
                                    fontSize = 10.sp,
                                    color = MilkifyTextSecondary
                                )
                            }
                        }
                    } else if (backendTypeInput == "SUPABASE") {
                        Surface(
                            color = MilkifyBlueContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("⚡ Supabase Configuration", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                OutlinedTextField(
                                    value = supabaseUrlInput,
                                    onValueChange = { supabaseUrlInput = it },
                                    label = { Text("Supabase Project URL") },
                                    placeholder = { Text("https://xxxx.supabase.co") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = supabaseKeyInput,
                                    onValueChange = { supabaseKeyInput = it },
                                    label = { Text("Supabase Anon Public Key") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = MilkifyLightGrey,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🌐 Built-in Auto Cloud Hub Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyBlack)
                                Text(
                                    "5 एक्टिव स्लॉट्स के साथ काम करता है। अधिक क्षमता (1000+ ग्राहकों) के लिए ऊपर दिए गए 'Firebase' विकल्प को चुनें।",
                                    fontSize = 11.sp,
                                    color = MilkifyTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons Row 1: Save & Broadcast + Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val newCfg = MilkifySessionManager.CloudBackendConfig(
                                    backendType = backendTypeInput,
                                    firebaseUrl = firebaseUrlInput.trim(),
                                    firebaseAuth = firebaseAuthInput.trim(),
                                    supabaseUrl = supabaseUrlInput.trim(),
                                    supabaseKey = supabaseKeyInput.trim()
                                )
                                viewModel.updateCloudBackendConfig(newCfg, broadcastToAll = true)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen, contentColor = MilkifyWhite)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE & BROADCAST", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val testCfg = MilkifySessionManager.CloudBackendConfig(
                                    backendType = backendTypeInput,
                                    firebaseUrl = firebaseUrlInput.trim(),
                                    firebaseAuth = firebaseAuthInput.trim(),
                                    supabaseUrl = supabaseUrlInput.trim(),
                                    supabaseKey = supabaseKeyInput.trim()
                                )
                                viewModel.testCustomCloudConnection(testCfg)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyBlack)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp), tint = MilkifyBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TEST SERVER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Migrate Data Button
                    OutlinedButton(
                        onClick = { viewModel.migrateAllDataToActiveBackend() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MilkifyGold)
                    ) {
                        Text("🚀 MIGRATE ALL LOCAL DATA / सारा डेटा ट्रांसफर करें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                    }

                    if (cloudDiag != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = if (cloudDiag!!.isOnline) MilkifyGreenContainer else MilkifyRedLight.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                cloudDiag!!.statusMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (cloudDiag!!.isOnline) MilkifyDarkGreen else MilkifyRed,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auxiliary Operations: Force Sync, Repair Slots & Push Local
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.syncFromCloud(showFeedback = true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyBorderDark)
                        ) {
                            Text("FORCE SYNC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }

                        OutlinedButton(
                            onClick = { viewModel.repairAndResetCloudSlots() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyDarkGreen)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = MilkifyDarkGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("REPAIR SLOTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MilkifyDarkGreen)
                        }

                        OutlinedButton(
                            onClick = { viewModel.pushAllLocalOrdersToCloud() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyBorderDark)
                        ) {
                            Text("PUSH ORDERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                    }
                }
            }
        }

        // Section 1: Admin Login Credentials (Email & Password)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = MilkifyBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Admin Login Credentials", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyBlack)
                        }
                        Surface(
                            color = MilkifyLightGrey,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Super Admin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MilkifyBlack,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text("Control email and password used to unlock admin panel.", fontSize = 11.sp, color = MilkifyTextMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Admin Email ID") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MilkifyBlack) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Admin Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MilkifyBlack) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Text(
                                    if (isPasswordVisible) "Hide" else "Show",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlack
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                                viewModel.updateAdminCredentials(emailInput.trim(), passwordInput.trim())
                                saveFeedback = "Admin login updated! New credentials saved."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_admin_credentials_button")
                    ) {
                        Text("SAVE ADMIN CREDENTIALS / सेव करें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 2: Customer Support Helplines (Call & WhatsApp)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MilkifyPrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Customer Support Helplines", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyBlack)
                    }

                    Text("Updated numbers immediately reflect across all customer & rider app screens.", fontSize = 11.sp, color = MilkifyTextMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Call Support Number (Default: 9343688757)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MilkifyPrimaryGreen) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyPrimaryGreen,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatsappInput,
                        onValueChange = { whatsappInput = it },
                        label = { Text("WhatsApp Support Number (Default: 9343688757)") },
                        leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = MilkifyPrimaryGreen) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_whatsapp_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyPrimaryGreen,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (phoneInput.isNotBlank() && whatsappInput.isNotBlank()) {
                                viewModel.updateHubSettings(phone = phoneInput.trim(), whatsapp = whatsappInput.trim())
                                saveFeedback = "Customer support numbers updated to $phoneInput!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen, contentColor = MilkifyWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_support_numbers_button")
                    ) {
                        Text("UPDATE SUPPORT NUMBERS / हेल्पलाइन नंबर बदलें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 3: Broadcast Announcement Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MilkifyBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Live Customer Announcement Banner", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyBlack)
                    }

                    Text("Broadcast important dairy messages, festival greetings, or delivery updates.", fontSize = 11.sp, color = MilkifyTextMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = announcementInput,
                        onValueChange = { announcementInput = it },
                        label = { Text("Announcement Text") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_settings_announcement_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.updateHubSettings(announcement = announcementInput.trim())
                            saveFeedback = "Live customer announcement broadcasted!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("broadcast_announcement_button")
                    ) {
                        Text("BROADCAST ANNOUNCEMENT / सूचना प्रसारित करें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 4: Hub Profile & Delivery Rules
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MilkifyBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dairy Hub & Logistics Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyBlack)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = hubAddressInput,
                        onValueChange = { hubAddressInput = it },
                        label = { Text("Hub Physical Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MilkifyBlack,
                            unfocusedBorderColor = MilkifyBorderDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MilkifyLightGrey,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MilkifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Delivery Slots & Policy Rules:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Morning Slot: 3:00 AM – 11:00 AM (Early sunrise delivery)", fontSize = 11.sp, color = MilkifyTextSecondary)
                            Text("• Evening Slot: 3:00 PM – 8:00 PM (Fresh sunset delivery)", fontSize = 11.sp, color = MilkifyTextSecondary)
                            Text("• Free Delivery: Orders ₹100 or above", fontSize = 11.sp, color = MilkifyTextSecondary)
                            Text("• Standard Fee: ₹10 for orders under ₹100", fontSize = 11.sp, color = MilkifyTextSecondary)
                            Text("• Coverage: Joura Town, Station Road, Kailaras Mor, Pagara Road, Civil Lines", fontSize = 11.sp, color = MilkifyTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.updateHubSettings(address = hubAddressInput.trim())
                            saveFeedback = "Hub address and profile updated!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlack, contentColor = MilkifyWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_hub_address_button")
                    ) {
                        Text("SAVE HUB PROFILE / हब जानकारी सेव करें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}



