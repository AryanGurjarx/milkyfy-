package com.example.ui.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entities.BillboardBannerEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.GalleryPhotoEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SubscriptionEntity
import com.example.ui.components.MilkifyBadge
import com.example.ui.components.MilkifyStatusBadge
import com.example.ui.components.PhonePeOrderSuccessDialog
import com.example.ui.theme.MilkifyAmber
import com.example.ui.theme.MilkifyBackground
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBlue
import com.example.ui.theme.MilkifyBlueContainer
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyCream
import com.example.ui.theme.MilkifyCreamCard
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGold
import com.example.ui.theme.MilkifyGreen
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyLightGreen
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyOnGreenContainer
import com.example.ui.theme.MilkifyPrimaryGreen
import com.example.ui.theme.MilkifyRed
import com.example.ui.theme.MilkifyRedLight
import com.example.ui.theme.MilkifySurface
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.theme.MilkifyYellow
import com.example.ui.theme.MilkifyYellowContainer
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.MilkifyViewModel
import kotlinx.coroutines.delay

@Composable
fun CustomerAppScreen(
    viewModel: MilkifyViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.customerTab.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedProductForDetail by viewModel.selectedProductForDetail.collectAsState()
    val trackingOrderId by viewModel.trackingOrderId.collectAsState()
    val confirmedOrderDetails by viewModel.confirmedOrderDetails.collectAsState()

    var showCartCheckoutSheet by remember { mutableStateOf(false) }
    var showNewAddressDialog by remember { mutableStateOf(false) }
    var showNewSubscriptionDialog by remember { mutableStateOf(false) }
    var showSupportTicketDialog by remember { mutableStateOf(false) }
    var showLabReportsDialog by remember { mutableStateOf(false) }
    var showFarmInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == CustomerTab.HOME,
                    onClick = { viewModel.selectCustomerTab(CustomerTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MilkifyPrimaryGreen,
                        selectedTextColor = MilkifyPrimaryGreen,
                        indicatorColor = MilkifyGreenContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    selected = currentTab == CustomerTab.ORDERS,
                    onClick = { viewModel.selectCustomerTab(CustomerTab.ORDERS) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Orders") },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MilkifyPrimaryGreen,
                        selectedTextColor = MilkifyPrimaryGreen,
                        indicatorColor = MilkifyGreenContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_orders")
                )
                NavigationBarItem(
                    selected = currentTab == CustomerTab.SUBSCRIPTION,
                    onClick = { viewModel.selectCustomerTab(CustomerTab.SUBSCRIPTION) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Subscription") },
                    label = { Text("Subscription") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MilkifyPrimaryGreen,
                        selectedTextColor = MilkifyPrimaryGreen,
                        indicatorColor = MilkifyGreenContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_subscription")
                )
                NavigationBarItem(
                    selected = currentTab == CustomerTab.PROFILE,
                    onClick = { viewModel.selectCustomerTab(CustomerTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MilkifyPrimaryGreen,
                        selectedTextColor = MilkifyPrimaryGreen,
                        indicatorColor = MilkifyGreenContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MilkifyBackground)
        ) {
            when (currentTab) {
                CustomerTab.HOME -> {
                    CustomerHomeScreen(
                        viewModel = viewModel,
                        onOpenCart = { showCartCheckoutSheet = true },
                        onOpenLabReports = { showLabReportsDialog = true },
                        onOpenFarmInfo = { showFarmInfoDialog = true }
                    )
                }
                CustomerTab.ORDERS -> {
                    if (trackingOrderId != null) {
                        OrderTrackingScreen(
                            orderId = trackingOrderId!!,
                            viewModel = viewModel,
                            onBack = { viewModel.closeOrderTracking() }
                        )
                    } else {
                        CustomerOrdersHistoryScreen(
                            viewModel = viewModel,
                            onTrackOrder = { id -> viewModel.openOrderTracking(id) }
                        )
                    }
                }
                CustomerTab.SUBSCRIPTION -> {
                    CustomerSubscriptionScreen(
                        viewModel = viewModel,
                        onStartNewSubscription = { showNewSubscriptionDialog = true }
                    )
                }
                CustomerTab.PROFILE -> {
                    CustomerProfileScreen(
                        viewModel = viewModel,
                        onAddNewAddress = { showNewAddressDialog = true },
                        onOpenSupport = { showSupportTicketDialog = true },
                        onOpenLabReports = { showLabReportsDialog = true },
                        onOpenFarmInfo = { showFarmInfoDialog = true }
                    )
                }
            }

            // Bottom Floating Cart Bar if cart is not empty and not on cart sheet
            if (cartItems.isNotEmpty() && !showCartCheckoutSheet) {
                val totalQty = cartItems.values.sumOf { it.second }
                val subtotal = cartItems.values.sumOf { it.first.price * it.second }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MilkifyPrimaryGreen,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$totalQty Item(s) in Cart",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Total: ₹${subtotal.toInt()} (Free Cash on Delivery)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { showCartCheckoutSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("view_cart_button")
                        ) {
                            Text(
                                text = "VIEW CART",
                                color = MilkifyPrimaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Product Detail Dialog
    if (selectedProductForDetail != null) {
        ProductDetailDialog(
            product = selectedProductForDetail!!,
            onDismiss = { viewModel.closeProductDetail() },
            onAddToCart = { prod ->
                viewModel.addToCart(prod)
                viewModel.closeProductDetail()
            }
        )
    }

    // Cart & Checkout Modal Sheet
    if (showCartCheckoutSheet) {
        CartCheckoutModalSheet(
            viewModel = viewModel,
            onDismiss = { showCartCheckoutSheet = false },
            onAddNewAddress = { showNewAddressDialog = true }
        )
    }

    // Add New Address Dialog
    if (showNewAddressDialog) {
        AddNewAddressDialog(
            onDismiss = { showNewAddressDialog = false },
            onSave = { label, house, street, area, landmark ->
                viewModel.addNewAddress(label, house, street, area, landmark)
                showNewAddressDialog = false
            }
        )
    }

    // Start New Subscription Dialog
    if (showNewSubscriptionDialog) {
        NewSubscriptionDialog(
            viewModel = viewModel,
            onDismiss = { showNewSubscriptionDialog = false },
            onCreated = { prodName, qty, freq, slot, start, end, price ->
                viewModel.createSubscription(prodName, qty, freq, slot, start, end, price)
                showNewSubscriptionDialog = false
            }
        )
    }

    // Support Ticket Dialog
    if (showSupportTicketDialog) {
        CreateSupportTicketDialog(
            onDismiss = { showSupportTicketDialog = false },
            onSubmit = { issue, desc ->
                viewModel.submitSupportTicket(issue, desc)
                showSupportTicketDialog = false
            }
        )
    }

    // Lab Reports Dialog
    if (showLabReportsDialog) {
        LabReportsDialog(
            viewModel = viewModel,
            onDismiss = { showLabReportsDialog = false }
        )
    }

    // Farm Info Dialog ("Know Your Milk")
    if (showFarmInfoDialog) {
        FarmInfoDialog(
            onDismiss = { showFarmInfoDialog = false }
        )
    }

    // PhonePe Celebration Order Confirmation Dialog
    if (confirmedOrderDetails != null) {
        PhonePeOrderSuccessDialog(
            data = confirmedOrderDetails!!,
            onDismiss = { viewModel.clearConfirmedOrder() },
            onTrackOrder = { orderId ->
                viewModel.clearConfirmedOrder()
                viewModel.openOrderTracking(orderId)
            }
        )
    }
}

@Composable
fun CustomerHomeScreen(
    viewModel: MilkifyViewModel,
    onOpenCart: () -> Unit,
    onOpenLabReports: () -> Unit,
    onOpenFarmInfo: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val customerName by viewModel.currentCustomerName.collectAsState()
    val announcement by viewModel.announcementBanner.collectAsState()
    val billboardBanners by viewModel.billboardBanners.collectAsState()
    val galleryPhotos by viewModel.galleryPhotos.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedGalleryPhoto by remember { mutableStateOf<GalleryPhotoEntity?>(null) }

    val categories = listOf("All", "Buffalo Milk", "Desi Ghee", "Cow Milk")
    val displayedProducts = if (selectedCategoryFilter == "All") {
        products
    } else {
        products.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Greeting & Live Milking Status
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Namaste, $customerName 👋",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = "Your daily fresh morning & evening milk in Joura",
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary
                    )
                }

                Surface(
                    color = MilkifyGreenContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MilkifyGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Milking Live",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MilkifyGreen
                        )
                    }
                }
            }
        }

        // Announcement banner if present
        if (announcement.isNotBlank()) {
            item {
                Surface(
                    color = MilkifyYellowContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MilkifyAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MilkifyAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Joura Hub Announcement",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MilkifyAmber
                            )
                            Text(
                                text = announcement,
                                fontSize = 12.sp,
                                color = MilkifyTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // 2. Billboard Carousel with at least 5 sliding images
        item {
            CustomerBillboardCarousel(banners = billboardBanners)
        }

        // 3. MOVED TO TOP: DOODH & DAIRY PRODUCTS (QUICK ORDER SECTION!)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🥛 Taaza Doodh & Desi Ghee",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MilkifyTextPrimary
                            )
                        }
                        Text(
                            text = "Fresh Buffalo Milk, Vedic Ghee & Cow Milk • Select & Order Below",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = MilkifyBlueContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MilkifyBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { viewModel.syncFromCloud(showFeedback = true) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Online Synced",
                                    tint = MilkifyBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Live Rates",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlue
                                )
                            }
                        }

                        Surface(
                            color = MilkifyRedLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${displayedProducts.size} Items",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MilkifyRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MilkifyRed else MilkifyWhite,
                            border = BorderStroke(1.dp, if (isSelected) MilkifyRed else MilkifyBorder),
                            shadowElevation = if (isSelected) 2.dp else 0.dp,
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MilkifyTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Products Cards (High Quality Images + 1-Tap Add to Cart)
        items(displayedProducts) { product ->
            val cartEntry = cartItems[product.id]
            val quantityInCart = cartEntry?.second ?: 0

            CustomerProductCard(
                product = product,
                quantityInCart = quantityInCart,
                onAddToCart = { viewModel.addToCart(product) },
                onDecrementCart = { viewModel.decrementCart(product.id) },
                onViewDetails = { viewModel.openProductDetail(product) }
            )
        }

        // 5. NEW SECTION: Hamari Dairy & Farm Gallery (Photo Gallery Section)
        item {
            CustomerDairyGallerySection(
                photos = galleryPhotos,
                onPhotoClick = { selectedGalleryPhoto = it }
            )
        }

        // 6. Trust Pillars Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrustBadgeCard(
                    icon = Icons.Default.Eco,
                    title = "Raw & Fresh",
                    subtitle = "No chemicals",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenFarmInfo
                )
                TrustBadgeCard(
                    icon = Icons.Default.Security,
                    title = "Lab Tested",
                    subtitle = "18 Purity tests",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenLabReports
                )
                TrustBadgeCard(
                    icon = Icons.Default.LocalShipping,
                    title = "Doorstep",
                    subtitle = "Every morning",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCart
                )
            }
        }

        // 7. Section: "Know Your Milk" / Farm Information Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenFarmInfo() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
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
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MilkifyGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = MilkifyGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Know Your Milk",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MilkifyTextPrimary
                        )
                        Text(
                            text = "Learn about our healthy cows, organic fodder feeding, automated milking and chilling process in Joura.",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Farm Info",
                        tint = MilkifyGreen
                    )
                }
            }
        }

        // 8. Section: "Milkify Trust" / Lab Report Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenLabReports() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MilkifyGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Milk Quality Tested Regularly",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MilkifyGreen
                        )
                        Text(
                            text = "Latest report: FAT 7.2% | SNF 9.2% | Zero Adulteration. Certified by MP State Dairy Lab.",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Lab Reports",
                        tint = MilkifyGreen
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Photo Enlarged Dialog
    if (selectedGalleryPhoto != null) {
        PhotoEnlargedDialog(
            photo = selectedGalleryPhoto!!,
            onDismiss = { selectedGalleryPhoto = null }
        )
    }
}

@Composable
fun CustomerBillboardCarousel(
    banners: List<BillboardBannerEntity>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    var activeIndex by remember { mutableStateOf(0) }

    // Auto rotate every 4.5 seconds
    LaunchedEffect(banners.size) {
        while (true) {
            delay(4500L)
            if (banners.isNotEmpty()) {
                activeIndex = (activeIndex + 1) % banners.size
            }
        }
    }

    val currentBanner = banners[activeIndex.coerceIn(0, banners.size - 1)]

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
        ) {
            // High quality photo background
            AsyncImage(
                model = currentBanner.imageUrl,
                contentDescription = currentBanner.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Deep gradient overlay for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.65f),
                                Color.Black.copy(alpha = 0.90f)
                            )
                        )
                    )
            )

            // Foreground Text & Controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Badge & Slide Index
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MilkifyRed,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentBanner.badge,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${activeIndex + 1} / ${banners.size}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Bottom Content
                Column {
                    Text(
                        text = currentBanner.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentBanner.subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Carousel Dots & Prev/Next Arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            banners.forEachIndexed { index, _ ->
                                val isSelected = index == activeIndex
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(if (isSelected) 22.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MilkifyRed else Color.White.copy(alpha = 0.5f))
                                        .clickable { activeIndex = index }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable {
                                        activeIndex = (activeIndex - 1 + banners.size) % banners.size
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Slide",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable {
                                        activeIndex = (activeIndex + 1) % banners.size
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Slide",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerDairyGallerySection(
    photos: List<GalleryPhotoEntity>,
    onPhotoClick: (GalleryPhotoEntity) -> Unit
) {
    if (photos.isEmpty()) return

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Farm & Cattle", "Milking Process", "Cold Chain", "Ghee Making", "Quality Lab", "Doorstep Delivery")

    val filtered = if (selectedCategory == "All") photos else photos.filter { it.category.equals(selectedCategory, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = MilkifyRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hamari Dairy & Farm Gallery",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyTextPrimary
                    )
                }
                Text(
                    text = "Asli tasveerein: Farm, pashu, automated milking & testing lab",
                    fontSize = 11.sp,
                    color = MilkifyTextMuted
                )
            }

            Surface(
                color = MilkifyGreenContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${photos.size} Photos",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MilkifyGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MilkifyBlue else MilkifyLightGrey,
                    border = BorderStroke(1.dp, if (isSelected) MilkifyBlue else MilkifyBorder),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MilkifyTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Gallery Cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filtered, key = { it.id }) { photo ->
                CustomerGalleryCard(photo = photo, onClick = { onPhotoClick(photo) })
            }
        }
    }
}

@Composable
fun CustomerGalleryCard(
    photo: GalleryPhotoEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
        border = BorderStroke(1.dp, MilkifyBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    color = MilkifyRed,
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = photo.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = photo.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MilkifyTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = photo.description,
                    fontSize = 11.sp,
                    color = MilkifyTextSecondary,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun PhotoEnlargedDialog(
    photo: GalleryPhotoEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(photo.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MilkifyGreenContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = photo.category,
                        color = MilkifyGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = photo.description,
                    fontSize = 13.sp,
                    color = MilkifyTextSecondary,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyRed)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CustomerProductCard(
    product: ProductEntity,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    onDecrementCart: () -> Unit,
    onViewDetails: () -> Unit
) {
    val isGhee = product.category.contains("Ghee", ignoreCase = true) || product.name.contains("Ghee", ignoreCase = true)
    val isBuffalo = product.category.contains("Buffalo", ignoreCase = true) || product.name.contains("भैंस", ignoreCase = true)

    val fallbackRes = when {
        isGhee -> R.drawable.ic_desi_ghee_art
        isBuffalo -> R.drawable.ic_buffalo_milk_art
        else -> R.drawable.ic_cow_milk_art
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
        border = BorderStroke(1.dp, MilkifyBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // High Quality Product Image
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MilkifyLightGrey)
                    .border(1.dp, MilkifyBorder, RoundedCornerShape(14.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl.ifBlank { null },
                    placeholder = painterResource(id = fallbackRes),
                    error = painterResource(id = fallbackRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    color = if (isGhee) MilkifyAmber else if (isBuffalo) MilkifyBlue else MilkifyGreen,
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (isGhee) "GHEE" else if (isBuffalo) "BUFFALO" else "COW",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyTextPrimary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                val subtitle = when {
                    isGhee -> "100% शुद्ध बिलोना दानेदार देशी घी • Joura Hub"
                    isBuffalo -> "मुर्रा भैंस का गाढ़ा दूध • FAT 7.2% • Thick Malai"
                    else -> "Pure Cow Milk • Daily Morning & Evening Fresh"
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MilkifyTextMuted,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MilkifyRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "/ ${product.volumeLabel}",
                            fontSize = 12.sp,
                            color = MilkifyTextSecondary
                        )
                    }

                    // Quantity Controls or Add Button
                    if (quantityInCart == 0) {
                        Button(
                            onClick = onAddToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_product_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADD", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MilkifyRedLight, RoundedCornerShape(10.dp))
                                .border(1.dp, MilkifyRed, RoundedCornerShape(10.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = onDecrementCart,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = MilkifyRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = quantityInCart.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MilkifyRed,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = onAddToCart,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = MilkifyRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrustBadgeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = MilkifySurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MilkifyBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MilkifyPrimaryGreen,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MilkifyTextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MilkifyTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onAddToCart: (ProductEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MilkifyTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Size and price banner
                    Surface(
                        color = MilkifyGreenContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pack Size",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                                Text(
                                    text = product.volumeLabel,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyDarkGreen
                                )
                            }
                            Text(
                                text = "₹${product.price.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MilkifyPrimaryGreen
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Description",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MilkifyTextPrimary
                    )
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = MilkifyTextSecondary,
                        lineHeight = 17.sp
                    )
                }

                item {
                    DetailInfoRow(
                        title = "Freshness Info",
                        content = product.freshnessInfo
                    )
                }

                item {
                    DetailInfoRow(
                        title = "Source / Farm Origin",
                        content = product.sourceFarm
                    )
                }

                item {
                    DetailInfoRow(
                        title = "Lab Testing Information",
                        content = product.labTestingInfo
                    )
                }

                item {
                    DetailInfoRow(
                        title = "Delivery Timing",
                        content = product.deliveryTiming
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddToCart(product) },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ADD TO CART (₹${product.price.toInt()})", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailInfoRow(title: String, content: String) {
    Surface(
        color = MilkifyCreamCard,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MilkifyBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MilkifyPrimaryGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = MilkifyTextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}
