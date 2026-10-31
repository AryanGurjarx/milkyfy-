package com.example.ui.customer

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.ui.components.MilkifyStatusBadge
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyCream
import com.example.ui.theme.MilkifyCreamCard
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGold
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyOnGreenContainer
import com.example.ui.theme.MilkifyPrimaryGreen
import com.example.ui.theme.MilkifySurface
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.viewmodel.MilkifyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerOrdersHistoryScreen(
    viewModel: MilkifyViewModel,
    onTrackOrder: (Long) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val customerPhone by viewModel.currentCustomerPhone.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    val filterTabs = listOf("ALL" to "All Orders", "CURRENT" to "Current", "DELIVERED" to "Delivered", "CANCELLED" to "Cancelled")

    // Filter customer's orders
    val myOrders = orders.filter { it.customerPhone == customerPhone }
    val filteredOrders = myOrders.filter { order ->
        when (selectedFilter) {
            "CURRENT" -> order.orderStatus in listOf("PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY")
            "DELIVERED" -> order.orderStatus == "DELIVERED"
            "CANCELLED" -> order.orderStatus == "CANCELLED"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Milk Orders",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MilkifyTextPrimary
        )
        Text(
            text = "Track today's milk delivery & order history",
            fontSize = 12.sp,
            color = MilkifyTextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips row
        ScrollableTabRow(
            selectedTabIndex = filterTabs.indexOfFirst { it.first == selectedFilter }.coerceAtLeast(0),
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                val index = filterTabs.indexOfFirst { it.first == selectedFilter }.coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = MilkifyPrimaryGreen
                    )
                }
            },
            edgePadding = 0.dp
        ) {
            filterTabs.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                Tab(
                    selected = isSelected,
                    onClick = { selectedFilter = key },
                    text = {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MilkifyPrimaryGreen else MilkifyTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = MilkifyTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No $selectedFilter orders found",
                        fontWeight = FontWeight.SemiBold,
                        color = MilkifyTextSecondary
                    )
                    Text(
                        text = "Place your first morning milk order today!",
                        fontSize = 12.sp,
                        color = MilkifyTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    CustomerOrderItemCard(
                        order = order,
                        onTrackClick = { onTrackOrder(order.id) },
                        onReorderClick = { viewModel.reorder(order) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerOrderItemCard(
    order: OrderEntity,
    onTrackClick: () -> Unit,
    onReorderClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.orderDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MilkifySurface),
        border = BorderStroke(1.dp, MilkifyBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: Order Number and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = MilkifyTextMuted
                    )
                }
                MilkifyStatusBadge(order.orderStatus)
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MilkifyBorder)

            // Items Summary
            Text(
                text = order.itemsSummary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MilkifyDarkGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Slot & Address
            Text(
                text = "Slot: ${order.deliverySlot}",
                fontSize = 12.sp,
                color = MilkifyTextSecondary
            )
            Text(
                text = order.deliveryAddress,
                fontSize = 11.sp,
                color = MilkifyTextMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        fontSize = 10.sp,
                        color = MilkifyTextMuted
                    )
                    Text(
                        text = "₹${order.totalAmount.toInt()} (${order.paymentMethod.take(3)})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyPrimaryGreen
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReorderClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reorder", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onTrackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingScreen(
    orderId: Long,
    viewModel: MilkifyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val order = orders.firstOrNull { it.id == orderId }

    if (order == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Order not found")
            Button(onClick = onBack) { Text("Go Back") }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Order Tracking",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = order.orderNumber,
                        fontSize = 12.sp,
                        color = MilkifyTextMuted
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                MilkifyStatusBadge(order.orderStatus)
            }
        }

        // Live Delivery Card / OTP Display
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Delivery Time",
                                fontSize = 12.sp,
                                color = MilkifyTextSecondary
                            )
                            Text(
                                text = order.estimatedDeliveryTime ?: "6:30 AM",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MilkifyDarkGreen
                            )
                        }

                        // OTP Code highlight
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, MilkifyPrimaryGreen)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DELIVERY OTP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyTextMuted
                                )
                                Text(
                                    text = order.otpCode,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MilkifyPrimaryGreen,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Share this 4-digit OTP with your rider at delivery to confirm pure milk handover.",
                        fontSize = 11.sp,
                        color = MilkifyTextSecondary
                    )
                }
            }
        }

        // Visual Timeline: 5 Status Steps
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifySurface),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery Status Timeline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MilkifyTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val statusRank = when (order.orderStatus.uppercase()) {
                        "PLACED" -> 1
                        "CONFIRMED" -> 2
                        "PACKED" -> 3
                        "OUT_FOR_DELIVERY" -> 4
                        "DELIVERED" -> 5
                        "CANCELLED" -> -1
                        else -> 1
                    }

                    TrackingStepRow(
                        stepNumber = 1,
                        title = "Order Placed",
                        subtitle = "Received by Milkify Joura Farm",
                        isCompleted = statusRank >= 1,
                        isCurrent = statusRank == 1,
                        isLast = false
                    )

                    TrackingStepRow(
                        stepNumber = 2,
                        title = "Order Confirmed",
                        subtitle = "Fresh batch allocated for your address",
                        isCompleted = statusRank >= 2,
                        isCurrent = statusRank == 2,
                        isLast = false
                    )

                    TrackingStepRow(
                        stepNumber = 3,
                        title = "Milk Packed",
                        subtitle = "Sealed at 4°C inside sanitized milk pouches",
                        isCompleted = statusRank >= 3,
                        isCurrent = statusRank == 3,
                        isLast = false
                    )

                    TrackingStepRow(
                        stepNumber = 4,
                        title = "Out for Delivery",
                        subtitle = if (order.riderName != null) "Assigned to rider ${order.riderName}" else "Rider on the way in Joura",
                        isCompleted = statusRank >= 4,
                        isCurrent = statusRank == 4,
                        isLast = false
                    )

                    TrackingStepRow(
                        stepNumber = 5,
                        title = "Delivered",
                        subtitle = if (order.orderStatus == "DELIVERED") "Delivered to your doorstep" else "Pending OTP delivery confirmation",
                        isCompleted = statusRank >= 5,
                        isCurrent = statusRank == 5,
                        isLast = true
                    )
                }
            }
        }

        // Rider Contact Card if Out for Delivery or Delivered
        if (order.riderName != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MilkifyCreamCard),
                    border = BorderStroke(1.dp, MilkifyBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MilkifyGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = MilkifyPrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = order.riderName!!,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "Delivery Partner • Joura Ward",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val phone = order.riderPhone ?: "+919826145011"
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Rider")
                        }
                    }
                }
            }
        }

        // Order Details Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifySurface),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Delivery Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MilkifyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Address: ${order.deliveryAddress}",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                    Text(
                        text = "Slot: ${order.deliverySlot}",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                    Text(
                        text = "Items: ${order.itemsSummary}",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                    Text(
                        text = "Amount: ₹${order.totalAmount.toInt()} (${order.paymentMethod})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MilkifyPrimaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun TrackingStepRow(
    stepNumber: Int,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) MilkifyPrimaryGreen else Color(0xFFE0E0E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = stepNumber.toString(),
                        color = MilkifyTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(if (isCompleted) MilkifyPrimaryGreen else Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isCompleted) MilkifyTextPrimary else MilkifyTextMuted
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isCompleted) MilkifyTextSecondary else MilkifyTextMuted
            )
        }
    }
}
