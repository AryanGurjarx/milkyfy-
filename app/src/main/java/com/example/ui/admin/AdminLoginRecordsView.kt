package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LoginRecordEntity
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyRed
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.MilkifyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminLoginRecordsView(viewModel: MilkifyViewModel) {
    val loginRecords by viewModel.loginRecords.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<LoginRecordEntity?>(null) }

    val filteredRecords = remember(loginRecords, searchQuery, selectedRoleFilter) {
        loginRecords.filter { record ->
            val matchesRole = when (selectedRoleFilter) {
                "CUSTOMER" -> record.role.equals("CUSTOMER", ignoreCase = true)
                "RIDER" -> record.role.equals("RIDER", ignoreCase = true)
                "ADMIN" -> record.role.equals("ADMIN", ignoreCase = true)
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                    record.name.lowercase().contains(q) ||
                    record.phoneOrEmail.lowercase().contains(q) ||
                    record.extraInfo.lowercase().contains(q) ||
                    record.role.lowercase().contains(q)

            matchesRole && matchesSearch
        }
    }

    val customerCount = loginRecords.count { it.role.equals("CUSTOMER", ignoreCase = true) }
    val riderCount = loginRecords.count { it.role.equals("RIDER", ignoreCase = true) }
    val adminCount = loginRecords.count { it.role.equals("ADMIN", ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_login_records_view")
    ) {
        // --- 1. HEADER SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MilkifyBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Login Records",
                                tint = MilkifyWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "User Logins / लॉगिन डेटा",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MilkifyBlack
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = MilkifyBlack,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${loginRecords.size} Total",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyWhite,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live login audit log: Customer, Rider & Admin activities",
                                fontSize = 12.sp,
                                color = MilkifyTextMuted
                            )
                        }
                    }

                    if (loginRecords.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_all_login_records_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All Records",
                                tint = MilkifyRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // --- 2. SUMMARY METRIC PILLS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Customers",
                        count = customerCount,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Riders",
                        count = riderCount,
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Admin",
                        count = adminCount,
                        color = MilkifyRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3. SEARCH & ROLE FILTERS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
            border = BorderStroke(1.dp, MilkifyBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_login_records"),
                    placeholder = { Text("Search by name, phone, email, village...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MilkifyTextMuted)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = MilkifyTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MilkifyBlack,
                        unfocusedBorderColor = MilkifyBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MilkifyTextMuted,
                        modifier = Modifier.size(16.dp)
                    )

                    listOf(
                        "ALL" to "All (${loginRecords.size})",
                        "CUSTOMER" to "Customers ($customerCount)",
                        "RIDER" to "Riders ($riderCount)",
                        "ADMIN" to "Admin ($adminCount)"
                    ).forEach { (roleKey, label) ->
                        val isSelected = selectedRoleFilter == roleKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRoleFilter = roleKey },
                            label = { Text(text = label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MilkifyBlack,
                                selectedLabelColor = MilkifyWhite,
                                containerColor = MilkifyLightGrey,
                                labelColor = MilkifyBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) MilkifyBlack else MilkifyBorder
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 4. LOGIN RECORDS LIST ---
        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    border = BorderStroke(1.dp, MilkifyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MilkifyLightGrey),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = MilkifyTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "कोई रिकॉर्ड नहीं मिला" else "अभी कोई लॉगिन रिकॉर्ड दर्ज नहीं है",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyBlack
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "Search term '$searchQuery' के लिए कोई परिणाम नहीं मिला।"
                            else
                                "जैसे ही कोई ग्राहक (Customer), डिलीवरी राइडर (Rider) या एडमिन लॉगिन करेगा, उसका नाम, फ़ोन और लॉगिन का सही समय यहाँ अपने आप सुरक्षित हो जाएगा।",
                            fontSize = 12.sp,
                            color = MilkifyTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = filteredRecords,
                    key = { it.id }
                ) { record ->
                    LoginRecordCard(
                        record = record,
                        onDelete = { recordToDelete = record }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // --- CONFIRM CLEAR ALL DIALOG ---
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear All Login Records?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("क्या आप सभी लॉगिन रिकॉर्ड्स हटाना चाहते हैं? यह क्रिया वापस नहीं की जा सकती।")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllLoginRecords()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("हाँ, साफ़ करें (Clear All)", color = MilkifyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("रद्द करें (Cancel)", color = MilkifyBlack)
                }
            }
        )
    }

    // --- CONFIRM SINGLE DELETE DIALOG ---
    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = {
                Text(
                    text = "Delete Login Entry?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("क्या आप ${record.name} (${record.role}) का लॉगिन रिकॉर्ड हटाना चाहते हैं?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLoginRecord(record.id)
                        recordToDelete = null
                    }
                ) {
                    Text("हटाएं (Delete)", color = MilkifyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("रद्द करें (Cancel)", color = MilkifyBlack)
                }
            }
        )
    }
}

@Composable
private fun MetricChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MilkifyTextSecondary
            )
        }
    }
}

@Composable
private fun LoginRecordCard(
    record: LoginRecordEntity,
    onDelete: () -> Unit
) {
    val roleColor = when (record.role.uppercase()) {
        "CUSTOMER" -> Color(0xFF1976D2) // Blue
        "RIDER" -> Color(0xFFE65100)    // Amber / Deep Orange
        "ADMIN" -> MilkifyRed           // Crimson Red
        else -> MilkifyBlack
    }

    val roleIcon = when (record.role.uppercase()) {
        "CUSTOMER" -> Icons.Default.Person
        "RIDER" -> Icons.Default.DirectionsBike
        "ADMIN" -> Icons.Default.AdminPanelSettings
        else -> Icons.Default.Shield
    }

    val roleHindi = when (record.role.uppercase()) {
        "CUSTOMER" -> "ग्राहक / Customer"
        "RIDER" -> "राइडर / Delivery Rider"
        "ADMIN" -> "एडमिन / Administrator"
        else -> record.role
    }

    val formattedTime = remember(record.loginTime) {
        val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(record.loginTime))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_record_item_${record.id}"),
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(roleColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = roleIcon,
                            contentDescription = record.role,
                            tint = roleColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = record.name.ifBlank { "User #${record.id}" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MilkifyBlack,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = roleColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.8.dp, roleColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = roleHindi,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = roleColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MilkifyTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = record.phoneOrEmail.ifBlank { "No contact recorded" },
                                fontSize = 12.sp,
                                color = MilkifyTextSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = MilkifyTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (record.extraInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MilkifyLightGrey,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MilkifyBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = record.extraInfo,
                            fontSize = 11.sp,
                            color = MilkifyBlack,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Timestamp + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MilkifyTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = MilkifyTextMuted
                    )
                }

                Surface(
                    color = MilkifyGreenContainer,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.6.dp, MilkifyDarkGreen)
                ) {
                    Text(
                        text = "● ${record.deviceStatus}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyDarkGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
