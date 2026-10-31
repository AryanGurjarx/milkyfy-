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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LabReportEntity
import com.example.ui.components.MilkifyBadge
import com.example.ui.components.MilkifyStatusBadge
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
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
fun CustomerProfileScreen(
    viewModel: MilkifyViewModel,
    onAddNewAddress: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenLabReports: () -> Unit,
    onOpenFarmInfo: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val customerName by viewModel.currentCustomerName.collectAsState()
    val customerPhone by viewModel.currentCustomerPhone.collectAsState()
    val customerVillage by viewModel.currentCustomerVillage.collectAsState()
    val customerEmail by viewModel.currentCustomerEmail.collectAsState()
    val addresses by viewModel.customerAddresses.collectAsState()
    val tickets by viewModel.supportTickets.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyCreamCard),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MilkifyPrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customerName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customerName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyTextPrimary
                        )
                        Text(
                            text = "Phone: $customerPhone",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary
                        )
                        Text(
                            text = "Village: $customerVillage",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary
                        )
                        if (customerEmail.isNotBlank()) {
                            Text(
                                text = "Gmail: $customerEmail",
                                fontSize = 11.sp,
                                color = MilkifyTextMuted
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Edit", fontSize = 11.sp)
                    }
                }
            }
        }

        // Customer Hisaab (Khata Ledger) - Purely Offline Cash Deals
        item {
            val allHisaab by viewModel.allHisaabEntries.collectAsState()
            val myHisaab = allHisaab.filter { it.customerPhone == customerPhone }
            val totalBilled = myHisaab.filter { it.entryType == "BILL" }.sumOf { it.amount }
            val totalPaid = myHisaab.filter { it.entryType == "CASH_PAID" }.sumOf { it.amount }
            val netDue = (totalBilled - totalPaid).coerceAtLeast(0.0)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.5.dp, MilkifyBlack)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MilkifyBlack, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Customer Hisaab / खाता", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (netDue > 0) MilkifyBlack else MilkifyLightGrey,
                            border = BorderStroke(1.dp, MilkifyBlack)
                        ) {
                            Text(
                                text = if (netDue > 0) "Pending Due: ₹${netDue.toInt()}" else "All Cleared (₹0)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netDue > 0) MilkifyWhite else MilkifyBlack,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Milk Delivered", fontSize = 11.sp, color = MilkifyTextMuted)
                            Text("₹${totalBilled.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cash Paid Offline", fontSize = 11.sp, color = MilkifyTextMuted)
                            Text("₹${totalPaid.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current Due", fontSize = 11.sp, color = MilkifyTextMuted)
                            Text("₹${netDue.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MilkifyBlack)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MilkifyBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Only Offline Cash deals accepted. No wallet credit/recharge required. Complete hisaab-kitab is audited and maintained by Joura Admin Hub.",
                        fontSize = 11.sp,
                        color = MilkifyTextSecondary
                    )
                }
            }
        }

        // Trust & Origin Section
        item {
            Text("Trust & Quality Assurance", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MilkifyCreamCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MilkifyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenFarmInfo() }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = MilkifyPrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Know Your Milk", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Our Joura dairy farm", fontSize = 11.sp, color = MilkifyTextMuted)
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenLabReports() }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = MilkifyDarkGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Lab Reports", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MilkifyDarkGreen)
                            Text("Daily purity results", fontSize = 11.sp, color = MilkifyTextSecondary)
                        }
                    }
                }
            }
        }

        // Saved Delivery Addresses
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Addresses", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyTextPrimary)
                TextButton(onClick = onAddNewAddress) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, color = MilkifyPrimaryGreen)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                addresses.forEach { addr ->
                    Surface(
                        color = MilkifySurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MilkifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MilkifyPrimaryGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (addr.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        MilkifyBadge("Default")
                                    }
                                }
                                Text("${addr.houseNumber}, ${addr.street}, ${addr.area}, Joura (${addr.pincode})", fontSize = 12.sp, color = MilkifyTextSecondary)
                                if (addr.landmark.isNotBlank()) {
                                    Text("Landmark: ${addr.landmark}", fontSize = 11.sp, color = MilkifyTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Support & Help Desk Section
        item {
            Text("Help & Support", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MilkifyTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifySurface),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                val supportPhone by viewModel.supportPhone.collectAsState()
                val whatsappNumber by viewModel.whatsappNumber.collectAsState()

                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Have an issue with your morning milk?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Joura Hub Helpline: $supportPhone • WhatsApp: $whatsappNumber", fontSize = 12.sp, color = MilkifyTextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val cleanPhone = if (supportPhone.startsWith("+")) supportPhone else "+91$supportPhone"
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call 9343688757", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                val cleanDigits = whatsappNumber.filter { it.isDigit() }
                                val finalDigits = if (cleanDigits.length == 10) "91$cleanDigits" else cleanDigits
                                val url = "https://api.whatsapp.com/send?phone=$finalDigits&text=Hi%20Milkify%20Joura,%20I%20need%20support%20with%20my%20milk%20delivery"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onOpenSupport,
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Report Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (tickets.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("My Support Tickets (${tickets.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        tickets.take(3).forEach { ticket ->
                            Surface(
                                color = MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${ticket.ticketNumber} • ${ticket.issueType}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(ticket.description, fontSize = 11.sp, color = MilkifyTextSecondary, maxLines = 1)
                                    }
                                    MilkifyStatusBadge(ticket.status)
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

    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(customerName) }
        var newPhone by remember { mutableStateOf(customerPhone) }
        var newVillage by remember { mutableStateOf(customerVillage) }
        var newEmail by remember { mutableStateOf(customerEmail) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Update Customer Profile / प्रोफ़ाइल बदलें") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name / पूरा नाम") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Phone Number / मोबाइल नंबर") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newVillage,
                        onValueChange = { newVillage = it },
                        label = { Text("Village / गाँव / मोहल्ला") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Gmail ID / ईमेल आईडी") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.loginCustomer(
                            phone = newPhone,
                            name = newName,
                            village = newVillage,
                            email = newEmail
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
                ) {
                    Text("Save / सुरक्षित करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateSupportTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (issueType: String, description: String) -> Unit
) {
    var selectedIssue by remember { mutableStateOf("Missing Milk Delivery") }
    var description by remember { mutableStateOf("") }

    val issues = listOf(
        "Missing Milk Delivery",
        "Late Delivery (After 7 AM)",
        "Wrong Milk Quantity / Size",
        "Damaged Milk Bag / Leakage",
        "Payment / COD Issue",
        "Vacation Pause Request"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report a Milk Delivery Issue", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Select Issue Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column {
                        issues.forEach { issue ->
                            val isSelected = selectedIssue == issue
                            Surface(
                                color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedIssue = issue }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = issue,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MilkifyDarkGreen else MilkifyTextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Details of the issue") },
                        placeholder = { Text("Explain briefly so we can resolve quickly...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSubmit(selectedIssue, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
            ) {
                Text("Submit Ticket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LabReportsDialog(
    viewModel: MilkifyViewModel,
    onDismiss: () -> Unit
) {
    val labReports by viewModel.labReports.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = MilkifyPrimaryGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Milk Quality & Lab Tests", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text(
                        text = "Every batch of raw milk is tested for purity, fat, SNF, and 18 chemical adulterants before being dispatched across Joura.",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                }

                items(labReports) { report ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MilkifyCreamCard),
                        border = BorderStroke(1.dp, MilkifyBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(report.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MilkifyDarkGreen)
                                if (report.isLatest) {
                                    MilkifyBadge("Latest Batch")
                                }
                            }

                            Text("Date: ${report.reportDate}", fontSize = 11.sp, color = MilkifyTextMuted)
                            Text("Tested At: ${report.laboratoryName}", fontSize = 11.sp, color = MilkifyTextSecondary)

                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = MilkifyBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("FAT %", fontSize = 10.sp, color = MilkifyTextMuted)
                                    Text("${report.fatPercentage}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MilkifyPrimaryGreen)
                                }
                                Column {
                                    Text("SNF %", fontSize = 10.sp, color = MilkifyTextMuted)
                                    Text("${report.snfPercentage}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MilkifyPrimaryGreen)
                                }
                                Column {
                                    Text("Temperature", fontSize = 10.sp, color = MilkifyTextMuted)
                                    Text(report.temperatureAtCollection, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MilkifyDarkGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Adulteration Check:", fontSize = 10.sp, color = MilkifyTextMuted)
                            Text(report.adulterationStatus, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MilkifyDarkGreen)

                            Text("Certified By: ${report.certifiedBy}", fontSize = 10.sp, color = MilkifyTextMuted)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun FarmInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = MilkifyPrimaryGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Know Your Milk", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "Farm-to-Home Promise in Joura, Morena (MP)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyDarkGreen
                    )
                    Text(
                        text = "Milkify was started with one pure mission: bringing unadulterated, raw milk from healthy cows directly to families in Joura without middlemen or harmful preservatives.",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                item {
                    FarmPillarItem(
                        title = "1. Grass-Fed Indigenous Cows",
                        desc = "Our cows feed on fresh green fodder (Berseem, Maize, Sorghum) grown without synthetic pesticides on our Joura farm."
                    )
                }

                item {
                    FarmPillarItem(
                        title = "2. Untouched Machine Milking",
                        desc = "Automated hygienic milking clusters ensure milk goes straight from udder to stainless steel chilling tanks untouched by human hands."
                    )
                }

                item {
                    FarmPillarItem(
                        title = "3. Instant 4°C Chilling",
                        desc = "Rapid bulk cooling within 45 minutes inhibits bacterial proliferation and locks in natural sweetness, probiotics, and calcium."
                    )
                }

                item {
                    FarmPillarItem(
                        title = "4. Fresh Morning Delivery",
                        desc = "Delivered to your doorstep between 3:00 AM and 11:00 AM in Joura by our dedicated delivery riders."
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun FarmPillarItem(title: String, desc: String) {
    Surface(
        color = MilkifyCreamCard,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MilkifyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MilkifyPrimaryGreen)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 11.sp, color = MilkifyTextPrimary, lineHeight = 15.sp)
        }
    }
}
