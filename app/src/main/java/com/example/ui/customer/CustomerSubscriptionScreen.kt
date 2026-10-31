package com.example.ui.customer

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.local.entities.SubscriptionEntity
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

@Composable
fun CustomerSubscriptionScreen(
    viewModel: MilkifyViewModel,
    onStartNewSubscription: () -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val customerPhone by viewModel.currentCustomerPhone.collectAsState()

    val mySubscriptions = subscriptions.filter { it.customerPhone == customerPhone }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Milk Subscription",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = "Never run out of pure farm milk in Joura",
                        fontSize = 12.sp,
                        color = MilkifyTextMuted
                    )
                }

                Button(
                    onClick = onStartNewSubscription,
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("start_subscription_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Subscribe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Subscription Highlights Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyCreamCard),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Why Subscribe to Milkify?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MilkifyPrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SubFeatureItem(icon = Icons.Default.Schedule, title = "Guaranteed Slot", subtitle = "Before 6:30 AM")
                        SubFeatureItem(icon = Icons.Default.Pause, title = "Pause Anytime", subtitle = "When out of town")
                        SubFeatureItem(icon = Icons.Default.LocalShipping, title = "Zero Delivery Fee", subtitle = "Every day free")
                    }
                }
            }
        }

        // Active / Paused Subscriptions List
        item {
            Text(
                text = "My Active Subscriptions (${mySubscriptions.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MilkifyTextPrimary
            )
        }

        if (mySubscriptions.isEmpty()) {
            item {
                Surface(
                    color = MilkifySurface,
                    shape = RoundedCornerShape(14.dp),
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
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MilkifyTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active subscriptions yet",
                            fontWeight = FontWeight.SemiBold,
                            color = MilkifyTextSecondary
                        )
                        Text(
                            text = "Set up a daily 1L or 2L milk plan to get fresh farm milk every morning.",
                            fontSize = 12.sp,
                            color = MilkifyTextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onStartNewSubscription,
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
                        ) {
                            Text("Set Up Daily Milk")
                        }
                    }
                }
            }
        } else {
            items(mySubscriptions) { sub ->
                SubscriptionCard(
                    sub = sub,
                    onPause = { viewModel.pauseSubscription(sub.id) },
                    onResume = { viewModel.resumeSubscription(sub.id) },
                    onCancel = { viewModel.cancelSubscription(sub.id) }
                )
            }
        }

        // Upcoming Deliveries Calendar Preview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifySurface),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "This Week's Scheduled Deliveries",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MilkifyTextPrimary
                        )
                        Text(
                            text = "Morning Batch",
                            fontSize = 11.sp,
                            color = MilkifyPrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val daysOfWeek = listOf(
                        Triple("Mon", "19 Sep", "1 L"),
                        Triple("Tue", "20 Sep", "1 L"),
                        Triple("Wed", "21 Sep", "1 L"),
                        Triple("Thu", "22 Sep", "1 L"),
                        Triple("Fri", "23 Sep", "1 L"),
                        Triple("Sat", "24 Sep", "1 L"),
                        Triple("Sun", "25 Sep", "1 L")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { (day, date, qty) ->
                            Surface(
                                color = MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier.weight(1f).padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyTextPrimary)
                                    Text(qty, fontSize = 10.sp, color = MilkifyPrimaryGreen, fontWeight = FontWeight.SemiBold)
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MilkifyPrimaryGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun SubscriptionCard(
    sub: SubscriptionEntity,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MilkifySurface),
        border = BorderStroke(1.dp, MilkifyBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = sub.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = "${sub.frequency} Delivery • ${sub.deliverySlot}",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                }
                MilkifyStatusBadge(sub.status)
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MilkifyBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Daily Quantity", fontSize = 11.sp, color = MilkifyTextMuted)
                    Text("${sub.quantityLiters} Litre / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MilkifyDarkGreen)
                }
                Column {
                    Text("Price per Day", fontSize = 11.sp, color = MilkifyTextMuted)
                    Text("₹${sub.pricePerDay.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                }
                Column {
                    Text("Valid Through", fontSize = 11.sp, color = MilkifyTextMuted)
                    Text(sub.endDate, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MilkifyTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Pause / Resume, Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sub.status == "ACTIVE") {
                    OutlinedButton(
                        onClick = onPause,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause Plan", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onResume,
                        colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume Plan", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Cancel", color = Color.Red, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SubFeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MilkifyGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MilkifyPrimaryGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MilkifyTextPrimary)
        Text(subtitle, fontSize = 10.sp, color = MilkifyTextMuted)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubscriptionDialog(
    viewModel: MilkifyViewModel,
    onDismiss: () -> Unit,
    onCreated: (prodName: String, qty: Double, freq: String, slot: String, start: String, end: String, price: Double) -> Unit
) {
    var selectedMilkType by remember { mutableStateOf("Buffalo Milk") }
    var selectedSize by remember { mutableStateOf("1 L") }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var selectedSlot by remember { mutableStateOf("Morning (3:00 AM – 11:00 AM)") }

    val milkTypes = listOf("Buffalo Milk", "Cow Milk")
    val sizes = listOf("500 ml" to 35.0, "1 L" to 69.0, "2 L" to 135.0)
    val frequencies = listOf("Daily", "Alternate Days", "Weekly", "Custom Schedule")
    val slots = listOf("Morning (3:00 AM – 11:00 AM)", "Evening (3:00 PM – 8:00 PM)")

    val selectedPrice = sizes.firstOrNull { it.first == selectedSize }?.second ?: 69.0
    val quantityLiters = when (selectedSize) {
        "500 ml" -> 0.5
        "1 L" -> 1.0
        "2 L" -> 2.0
        else -> 1.0
    }

    val fullProductName = if (selectedMilkType == "Buffalo Milk") {
        "Farm Fresh Buffalo Milk ($selectedSize)"
    } else {
        "Farm Fresh Pure Cow Milk ($selectedSize)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start Milk Subscription", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Select Milk Type", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        milkTypes.forEach { type ->
                            val isSelected = selectedMilkType == type
                            Surface(
                                color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) MilkifyPrimaryGreen else MilkifyBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMilkType = type }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (type == "Buffalo Milk") "Buffalo Milk (भैंस)" else "Cow Milk (गाय)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) MilkifyDarkGreen else MilkifyTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Select Pack Size & Rate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sizes.forEach { (size, price) ->
                            val isSelected = selectedSize == size
                            Surface(
                                color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) MilkifyPrimaryGreen else MilkifyBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedSize = size }
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(size, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("₹${price.toInt()}", fontSize = 10.sp, color = MilkifyPrimaryGreen)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Delivery Frequency", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        frequencies.take(2).forEach { freq ->
                            val isSelected = selectedFrequency == freq
                            Surface(
                                color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) MilkifyPrimaryGreen else MilkifyBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFrequency = freq }
                            ) {
                                Text(
                                    text = freq,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp),
                                    color = if (isSelected) MilkifyDarkGreen else MilkifyTextPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Delivery Slot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        slots.forEach { slot ->
                            val isSelected = selectedSlot == slot
                            Surface(
                                color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) MilkifyPrimaryGreen else MilkifyBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSlot = slot }
                            ) {
                                Text(
                                    text = slot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp),
                                    color = if (isSelected) MilkifyDarkGreen else MilkifyTextPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    Surface(
                        color = MilkifyCreamCard,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Summary: $fullProductName", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Delivery: $selectedFrequency • $selectedSlot", fontSize = 11.sp, color = MilkifyTextSecondary)
                            Text("Daily Cost: ₹${selectedPrice.toInt()} (Free doorstep delivery in Joura)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyPrimaryGreen)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreated(
                        fullProductName,
                        quantityLiters,
                        selectedFrequency,
                        selectedSlot,
                        "19 Sep 2026",
                        "19 Oct 2026",
                        selectedPrice
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
            ) {
                Text("Confirm Subscription")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
