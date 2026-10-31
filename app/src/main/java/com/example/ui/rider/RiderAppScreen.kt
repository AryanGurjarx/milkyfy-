package com.example.ui.rider

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.RiderEntity
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
import com.example.ui.viewmodel.MilkifyViewModel

@Composable
fun RiderAppScreen(
    viewModel: MilkifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val riders by viewModel.riders.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val selectedRiderId by viewModel.selectedRiderId.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.syncFromCloud(showFeedback = false)
    }

    val currentRider = riders.firstOrNull { it.id == selectedRiderId } ?: riders.firstOrNull()

    var activeTab by remember { mutableStateOf(0) } // 0: My Deliveries, 1: All Hub Orders, 2: Completed, 3: Shift & Earnings
    var orderToDeliver by remember { mutableStateOf<OrderEntity?>(null) }
    var riderMenuExpanded by remember { mutableStateOf(false) }

    // Deliveries assigned to current rider (by ID, exact name, or matching phone digits)
    val myActiveDeliveries = orders.filter { order ->
        val cleanRiderPhone = currentRider?.phone?.filter { it.isDigit() }?.takeLast(10) ?: ""
        val cleanOrderRiderPhone = order.riderPhone?.filter { it.isDigit() }?.takeLast(10) ?: ""
        val phoneMatches = cleanRiderPhone.isNotEmpty() && cleanOrderRiderPhone.isNotEmpty() && cleanRiderPhone == cleanOrderRiderPhone
        val nameMatches = !order.riderName.isNullOrBlank() && currentRider != null && (
            order.riderName.trim().equals(currentRider.name.trim(), ignoreCase = true) ||
            currentRider.name.contains(order.riderName.trim(), ignoreCase = true) ||
            order.riderName.contains(currentRider.name.trim(), ignoreCase = true)
        )
        val idMatches = order.riderId != null && currentRider != null && order.riderId == currentRider.id
        (idMatches || nameMatches || phoneMatches) && order.orderStatus in listOf("PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY")
    }

    // All active orders waiting or out for delivery in Joura
    val allActiveDeliveries = orders.filter {
        it.orderStatus in listOf("PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY")
    }

    val completedDeliveries = orders.filter { order ->
        val cleanRiderPhone = currentRider?.phone?.filter { it.isDigit() }?.takeLast(10) ?: ""
        val cleanOrderRiderPhone = order.riderPhone?.filter { it.isDigit() }?.takeLast(10) ?: ""
        val phoneMatches = cleanRiderPhone.isNotEmpty() && cleanOrderRiderPhone.isNotEmpty() && cleanRiderPhone == cleanOrderRiderPhone
        val nameMatches = !order.riderName.isNullOrBlank() && currentRider != null && (
            order.riderName.trim().equals(currentRider.name.trim(), ignoreCase = true) ||
            currentRider.name.contains(order.riderName.trim(), ignoreCase = true) ||
            order.riderName.contains(currentRider.name.trim(), ignoreCase = true)
        )
        val idMatches = order.riderId != null && currentRider != null && order.riderId == currentRider.id
        (idMatches || nameMatches || phoneMatches) && order.orderStatus == "DELIVERED"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MilkifyWhite)
    ) {
        // Rider Header & Shift Controls
        Surface(
            color = MilkifyWhite,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, MilkifyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { riderMenuExpanded = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MilkifyBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = MilkifyWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentRider?.name ?: "Rider",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MilkifyBlack
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(Switch)",
                                    fontSize = 11.sp,
                                    color = MilkifyBlack,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Vehicle: ${currentRider?.vehicleNumber ?: "MP-06"} • Joura Hub",
                                fontSize = 11.sp,
                                color = MilkifyTextMuted
                            )
                        }

                        DropdownMenu(
                            expanded = riderMenuExpanded,
                            onDismissRequest = { riderMenuExpanded = false }
                        ) {
                            riders.forEach { r ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(r.name, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                                            Text("${r.vehicleNumber} • ${r.phone}", fontSize = 11.sp, color = MilkifyTextMuted)
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectRider(r.id)
                                        riderMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Shift On/Off Toggle & Live Cloud Sync Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isCloudSyncing) MilkifyGold.copy(alpha = 0.2f) else MilkifyGreenContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isCloudSyncing) MilkifyGold else MilkifyDarkGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.syncFromCloud(showFeedback = true) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Sync",
                                    tint = if (isCloudSyncing) MilkifyGold else MilkifyDarkGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCloudSyncing) "Syncing..." else "☁️ Sync",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCloudSyncing) MilkifyBlack else MilkifyDarkGreen
                                )
                            }
                        }

                        if (currentRider != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = currentRider.isShiftActive,
                                onCheckedChange = {
                                    viewModel.toggleRiderShift(currentRider.id, currentRider.isShiftActive)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MilkifyWhite,
                                    checkedTrackColor = MilkifyBlack,
                                    uncheckedThumbColor = MilkifyWhite,
                                    uncheckedTrackColor = MilkifyBorderDark
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick KPI Cards: Today's Deliveries, Pending, Today's Earnings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RiderMetricCard(
                        title = "My Pending",
                        value = myActiveDeliveries.size.toString(),
                        subtitle = "Assigned bottles",
                        color = MilkifyBlack,
                        modifier = Modifier.weight(1f)
                    )
                    RiderMetricCard(
                        title = "Hub Total",
                        value = allActiveDeliveries.size.toString(),
                        subtitle = "Total in Joura",
                        color = MilkifyBlack,
                        modifier = Modifier.weight(1f)
                    )
                    RiderMetricCard(
                        title = "Earnings",
                        value = "₹${currentRider?.todayEarnings?.toInt() ?: 450}",
                        subtitle = "Today's payout",
                        color = MilkifyBlack,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Rider Sub-Tabs: My Tasks, All Hub Orders, Delivered, Shift & Performance
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MilkifyWhite,
            contentColor = MilkifyBlack
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("My Tasks (${myActiveDeliveries.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MilkifyBlack) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("All Hub (${allActiveDeliveries.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MilkifyBlack) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Delivered (${completedDeliveries.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MilkifyBlack) }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = { Text("Performance", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MilkifyBlack) }
            )
        }

        // Tab Contents
        when (activeTab) {
            0 -> {
                if (myActiveDeliveries.isEmpty()) {
                    if (allActiveDeliveries.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = Color(0xFFFFF8E1),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFE082)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "⚡ Joura Hub Active Deliveries (${allActiveDeliveries.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = "No orders directly assigned to ${currentRider?.name ?: "you"} yet. You can accept and deliver any of the orders below:",
                                        fontSize = 12.sp,
                                        color = MilkifyTextPrimary
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(allActiveDeliveries) { order ->
                                    RiderDeliveryTaskCard(
                                        order = order,
                                        isAssignedToMe = (order.riderId == currentRider?.id || order.riderName == currentRider?.name),
                                        onAccept = {
                                            if (currentRider != null) {
                                                viewModel.riderAcceptOrder(order.id, currentRider)
                                            }
                                        },
                                        onNavigate = {
                                            val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(order.deliveryAddress + ", Joura, Morena")}")
                                            val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                            context.startActivity(intent)
                                        },
                                        onCall = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}"))
                                            context.startActivity(intent)
                                        },
                                        onStartDelivery = {
                                            viewModel.riderUpdateStatus(order.id, "OUT_FOR_DELIVERY")
                                        },
                                        onCompleteOtp = {
                                            orderToDeliver = order
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MilkifyPrimaryGreen,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "All morning deliveries completed!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "Great job! Waiting for next milk orders in Joura.",
                                    fontSize = 13.sp,
                                    color = MilkifyTextMuted
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = { viewModel.syncFromCloud(showFeedback = true) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Refresh Online Orders", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(myActiveDeliveries) { order ->
                            RiderDeliveryTaskCard(
                                order = order,
                                isAssignedToMe = true,
                                onAccept = null,
                                onNavigate = {
                                    val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(order.deliveryAddress + ", Joura, Morena")}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(intent)
                                },
                                onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}"))
                                    context.startActivity(intent)
                                },
                                onStartDelivery = {
                                    viewModel.riderUpdateStatus(order.id, "OUT_FOR_DELIVERY")
                                },
                                onCompleteOtp = {
                                    orderToDeliver = order
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                if (allActiveDeliveries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No Active Orders in Joura Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MilkifyTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = { viewModel.syncFromCloud(showFeedback = true) }) {
                                Text("Check for New Orders (Sync)")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allActiveDeliveries) { order ->
                            val isAssignedToMe = (order.riderId == currentRider?.id || order.riderName == currentRider?.name)
                            RiderDeliveryTaskCard(
                                order = order,
                                isAssignedToMe = isAssignedToMe,
                                onAccept = {
                                    if (currentRider != null) {
                                        viewModel.riderAcceptOrder(order.id, currentRider)
                                    }
                                },
                                onNavigate = {
                                    val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(order.deliveryAddress + ", Joura, Morena")}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(intent)
                                },
                                onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}"))
                                    context.startActivity(intent)
                                },
                                onStartDelivery = {
                                    viewModel.riderUpdateStatus(order.id, "OUT_FOR_DELIVERY")
                                },
                                onCompleteOtp = {
                                    orderToDeliver = order
                                }
                            )
                        }
                    }
                }
            }
            2 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(completedDeliveries) { order ->
                        RiderCompletedTaskCard(order = order)
                    }
                }
            }
            3 -> {
                RiderPerformanceView(rider = currentRider)
            }
        }
    }

    // OTP Confirmation Dialog
    if (orderToDeliver != null) {
        RiderOtpVerifyDialog(
            order = orderToDeliver!!,
            onDismiss = { orderToDeliver = null },
            onVerify = { enteredOtp ->
                viewModel.riderMarkDeliveredWithOtp(
                    orderId = orderToDeliver!!.id,
                    enteredOtp = enteredOtp,
                    expectedOtp = orderToDeliver!!.otpCode
                ) { success ->
                    if (success) {
                        orderToDeliver = null
                    }
                }
            }
        )
    }
}

@Composable
fun RiderMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MilkifyCreamCard,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MilkifyBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = MilkifyTextMuted)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 9.sp, color = MilkifyTextSecondary)
        }
    }
}

@Composable
fun RiderDeliveryTaskCard(
    order: OrderEntity,
    isAssignedToMe: Boolean = true,
    onAccept: (() -> Unit)? = null,
    onNavigate: () -> Unit,
    onCall: () -> Unit,
    onStartDelivery: () -> Unit,
    onCompleteOtp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MilkifyBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MilkifyTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MilkifyBadge(order.deliverySlot.take(7))
                }
                MilkifyStatusBadge(order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Info & Address
            Text(
                text = order.customerName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MilkifyDarkGreen
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MilkifyPrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.deliveryAddress,
                    fontSize = 13.sp,
                    color = MilkifyTextPrimary,
                    lineHeight = 17.sp
                )
            }

            if (!order.riderName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAssignedToMe) "Assigned to: YOU" else "Assigned Rider: ${order.riderName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAssignedToMe) MilkifyDarkGreen else Color(0xFFE65100)
                )
            }

            if (!order.customerNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Note: ${order.customerNotes}",
                        fontSize = 11.sp,
                        color = Color(0xFFF57F17),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Milk Items to Deliver & COD Amount
            Surface(
                color = MilkifyCreamCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MilkifyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("MILK TO DELIVER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MilkifyTextMuted)
                        Text(order.itemsSummary, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("COLLECT (COD)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MilkifyTextMuted)
                        Text("₹${order.totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Navigate, Call, Deliver / Accept
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigate,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Map", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 11.sp)
                }

                if (isAssignedToMe) {
                    Button(
                        onClick = onCompleteOtp,
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("rider_deliver_button_${order.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Deliver (OTP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (onAccept != null) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("rider_accept_button_${order.id}")
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept Ride", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RiderCompletedTaskCard(order: OrderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MilkifyPrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Text(order.deliveryAddress, fontSize = 11.sp, color = MilkifyTextMuted, maxLines = 1)
                Text(order.itemsSummary, fontSize = 12.sp, color = MilkifyDarkGreen)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Collected", fontSize = 10.sp, color = MilkifyTextMuted)
                Text("₹${order.totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                MilkifyStatusBadge("DELIVERED")
            }
        }
    }
}

@Composable
fun RiderPerformanceView(rider: RiderEntity?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lifetime Stats", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MilkifyTextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Deliveries", fontSize = 12.sp, color = MilkifyTextMuted)
                            Text("${rider?.totalDeliveries ?: 412}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                        }
                        Column {
                            Text("Total Earnings", fontSize = 12.sp, color = MilkifyTextMuted)
                            Text("₹${rider?.totalEarnings?.toInt() ?: 12500}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MilkifyDarkGreen)
                        }
                        Column {
                            Text("Rating", fontSize = 12.sp, color = MilkifyTextMuted)
                            Text("4.9 ★", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MilkifyGold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Joura Delivery Hub Guidelines", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Cold Chain: Keep insulated milk bags closed until doorstep delivery.", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Delivery Time: Morning milk must reach by 6:30 AM before sunrise.", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• OTP Mandate: Always enter the customer's 4-digit OTP to confirm delivery and register payment.", fontSize = 12.sp, color = MilkifyTextSecondary)
                    Text("• Customer Care: In case of unavailable customer or lock, call hub at +91 9343688757 (or WhatsApp 9343688757).", fontSize = 12.sp, color = MilkifyTextSecondary)
                }
            }
        }
    }
}

@Composable
fun RiderOtpVerifyDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit
) {
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Verify Customer OTP", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Customer: ${order.customerName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Order: ${order.orderNumber} • ${order.itemsSummary}",
                    fontSize = 12.sp,
                    color = MilkifyTextSecondary
                )
                Text(
                    text = "Collect: ₹${order.totalAmount.toInt()} (Cash on Delivery)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = enteredOtp,
                    onValueChange = {
                        if (it.length <= 4) {
                            enteredOtp = it
                            errorMessage = null
                        }
                    },
                    label = { Text("4-digit OTP from customer") },
                    placeholder = { Text("e.g. ${order.otpCode}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_input_field"),
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 11.sp)
                }

                Text(
                    text = "Customer has this OTP on their order tracking screen (${order.otpCode})",
                    fontSize = 10.sp,
                    color = MilkifyTextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredOtp.length == 4) {
                        if (enteredOtp == order.otpCode) {
                            onVerify(enteredOtp)
                        } else {
                            errorMessage = "Incorrect OTP. Please check with customer."
                        }
                    } else {
                        errorMessage = "Please enter all 4 digits"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                modifier = Modifier.testTag("confirm_otp_button")
            ) {
                Text("Confirm Delivery & Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
