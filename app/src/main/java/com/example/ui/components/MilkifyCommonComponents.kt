package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.NotificationEntity
import com.example.ui.theme.MilkifyAmber
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBlue
import com.example.ui.theme.MilkifyBlueContainer
import com.example.ui.theme.MilkifyBlueDark
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyBorderDark
import com.example.ui.theme.MilkifyGreen
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyGreenDark
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyOffWhite
import com.example.ui.theme.MilkifyRed
import com.example.ui.theme.MilkifyRedDark
import com.example.ui.theme.MilkifyRedLight
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.theme.MilkifyYellow
import com.example.ui.theme.MilkifyYellowContainer
import com.example.ui.viewmodel.AppRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkifyTopBar(
    currentRole: AppRole,
    onRoleSelected: (AppRole) -> Unit,
    cartItemCount: Int = 0,
    notificationCount: Int = 0,
    isSyncing: Boolean = false,
    onCloudSyncClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    customerLocation: String = "Joura, Morena (MP)"
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MilkifyWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MilkifyBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { roleMenuExpanded = true }
                    ) {
                        // Official Milkify App Logo
                        Image(
                            painter = painterResource(id = R.drawable.ic_milkify_logo),
                            contentDescription = "Milkify Logo",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MILKIFY",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MilkifyBlack,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "•",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MilkifyRed,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                // Role selector pill with colorful branding
                                Surface(
                                    color = when (currentRole) {
                                        AppRole.CUSTOMER -> MilkifyGreenContainer
                                        AppRole.RIDER -> MilkifyYellowContainer
                                        AppRole.ADMIN -> MilkifyRedLight
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        when (currentRole) {
                                            AppRole.CUSTOMER -> MilkifyGreen
                                            AppRole.RIDER -> MilkifyAmber
                                            AppRole.ADMIN -> MilkifyRed
                                        }
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = when (currentRole) {
                                                AppRole.CUSTOMER -> "Customer"
                                                AppRole.RIDER -> "Rider"
                                                AppRole.ADMIN -> "Admin"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (currentRole) {
                                                AppRole.CUSTOMER -> MilkifyGreenDark
                                                AppRole.RIDER -> MilkifyBlack
                                                AppRole.ADMIN -> MilkifyRedDark
                                            }
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Switch Interface",
                                            modifier = Modifier.size(16.dp),
                                            tint = MilkifyBlack
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Fresh Milk • Offline Doorstep Dairy",
                                fontSize = 11.sp,
                                color = MilkifyTextMuted
                            )
                        }

                        // Role Dropdown Menu
                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Store, contentDescription = null, tint = MilkifyGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "1. Customer App (ग्राहक ऐप)",
                                            fontWeight = if (currentRole == AppRole.CUSTOMER) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onRoleSelected(AppRole.CUSTOMER)
                                    roleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MilkifyAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "2. Rider App (डिलीवरी पार्टनर)",
                                            fontWeight = if (currentRole == AppRole.RIDER) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onRoleSelected(AppRole.RIDER)
                                    roleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = MilkifyRed)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "3. Admin Web Panel (एडमिन पैनल)",
                                            fontWeight = if (currentRole == AppRole.ADMIN) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onRoleSelected(AppRole.ADMIN)
                                    roleMenuExpanded = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MilkifyBlack)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Logout / Switch Account (लॉगआउट)", fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onLogoutClick()
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCloudSyncClick,
                        modifier = Modifier.testTag("cloud_sync_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MilkifyBlue
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Online Cloud Sync",
                                tint = MilkifyBlue
                            )
                        }
                    }

                    if (currentRole == AppRole.CUSTOMER) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier.testTag("notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (notificationCount > 0) {
                                        Badge(containerColor = MilkifyRed) {
                                            Text(notificationCount.toString(), color = MilkifyWhite, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (notificationCount > 0) MilkifyRed else MilkifyBlack
                                )
                            }
                        }

                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.testTag("cart_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge(containerColor = MilkifyGreen) {
                                            Text(cartItemCount.toString(), color = MilkifyWhite, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = MilkifyBlack
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MilkifyBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MilkifyWhite)
            )

            // Location Bar in Customer mode
            if (currentRole == AppRole.CUSTOMER) {
                Surface(
                    color = MilkifyLightGrey,
                    border = BorderStroke(1.dp, MilkifyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MilkifyRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Delivering in: ",
                            fontSize = 11.sp,
                            color = MilkifyTextMuted
                        )
                        Text(
                            text = customerLocation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyBlack,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            color = MilkifyYellowContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🌅 Morning: 3 AM-11 AM",
                                fontSize = 11.sp,
                                color = MilkifyBlack,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilkifyBadge(
    text: String,
    containerColor: Color = MilkifyLightGrey,
    contentColor: Color = MilkifyBlack
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MilkifyStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "PLACED" -> Triple(MilkifyBlueContainer, MilkifyBlueDark, "Placed")
        "CONFIRMED" -> Triple(MilkifyBlueContainer, MilkifyBlueDark, "Confirmed")
        "PACKED" -> Triple(MilkifyLightGrey, MilkifyBlack, "Packed")
        "OUT_FOR_DELIVERY" -> Triple(MilkifyYellowContainer, MilkifyBlack, "Out For Delivery")
        "DELIVERED" -> Triple(MilkifyGreenContainer, MilkifyGreenDark, "Delivered")
        "CANCELLED" -> Triple(MilkifyRedLight, MilkifyRedDark, "Cancelled")
        "ACTIVE" -> Triple(MilkifyGreenContainer, MilkifyGreenDark, "Active")
        "PAUSED" -> Triple(MilkifyYellowContainer, MilkifyAmber, "Paused")
        else -> Triple(MilkifyLightGrey, MilkifyBlack, status)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

