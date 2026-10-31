package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.MilkifyCloudSyncManager
import com.example.data.local.MilkifySessionManager
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AppSettingEntity
import com.example.data.local.entities.BillboardBannerEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.GalleryPhotoEntity
import com.example.data.local.entities.HisaabEntryEntity
import com.example.data.local.entities.LabReportEntity
import com.example.data.local.entities.LoginRecordEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RiderEntity
import com.example.data.local.entities.SubscriptionEntity
import com.example.data.local.entities.SupportTicketEntity
import com.example.data.local.entities.UserEntity
import com.example.data.repository.MilkifyRepository
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

enum class AppRole(val displayName: String) {
    CUSTOMER("Customer App"),
    RIDER("Rider Partner App"),
    ADMIN("Admin Panel")
}

enum class CustomerTab(val title: String) {
    HOME("Home"),
    ORDERS("Orders"),
    SUBSCRIPTION("Subscription"),
    PROFILE("Profile")
}

enum class AdminSection(val title: String) {
    DASHBOARD("Dashboard"),
    ORDERS("Orders"),
    LOGIN_RECORDS("User Logins / लॉगिन डेटा"),
    HISAAB("Customer Hisaab / Khata"),
    CUSTOMERS("Customers"),
    PRODUCTS("Products"),
    RIDERS("Riders"),
    SUBSCRIPTIONS("Subscriptions"),
    PAYMENTS("Offline Cash Reconciliation"),
    REPORTS("Reports"),
    LAB_REPORTS("Lab Reports"),
    COUPONS("Coupons"),
    GALLERY("Dairy Photos & Billboard"),
    SETTINGS("Settings")
}

data class PhonePeOrderSuccessData(
    val orderId: Long,
    val orderNumber: String,
    val totalAmount: Double,
    val itemsSummary: String,
    val deliverySlot: String,
    val deliveryAddress: String,
    val otpCode: String,
    val customerName: String,
    val customerPhone: String,
    val paymentMode: String = "Cash on Delivery (COD)",
    val timestamp: Long = System.currentTimeMillis()
)

class MilkifyViewModel(
    private val repository: MilkifyRepository,
    private val context: Context? = null
) : ViewModel() {

    // Persistent login session manager (keeps user logged in across restarts)
    private val sessionManager = context?.let { MilkifySessionManager(it) }

    // Real-time online multi-device cloud synchronizer
    private val cloudSyncManager = MilkifyCloudSyncManager()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _lastCloudSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastCloudSyncTime: StateFlow<Long> = _lastCloudSyncTime.asStateFlow()

    private val _cloudDiagnostics = MutableStateFlow<MilkifyCloudSyncManager.CloudDiagnostics?>(null)
    val cloudDiagnostics: StateFlow<MilkifyCloudSyncManager.CloudDiagnostics?> = _cloudDiagnostics.asStateFlow()

    private val _cloudBackendConfig = MutableStateFlow(sessionManager?.getCloudBackendConfig() ?: MilkifySessionManager.CloudBackendConfig())
    val cloudBackendConfig: StateFlow<MilkifySessionManager.CloudBackendConfig> = _cloudBackendConfig.asStateFlow()

    // Real-time alert for Admin when a new online customer order is received anywhere in Joura
    private val knownOrderIds = mutableSetOf<String>()
    private val _incomingOrderAlert = MutableStateFlow<OrderEntity?>(null)
    val incomingOrderAlert: StateFlow<OrderEntity?> = _incomingOrderAlert.asStateFlow()

    fun dismissIncomingOrderAlert() {
        _incomingOrderAlert.value = null
    }

    private fun playOrderChimeAndVibrate() {
        context?.let { ctx ->
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(ctx, uri)
                ringtone?.play()
            } catch (e: Exception) {
                android.util.Log.e("MilkifyAlert", "Error playing notification chime: ${e.message}")
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        v?.vibrate(500)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MilkifyAlert", "Error vibrating: ${e.message}")
            }
        }
    }

    fun updateCloudBackendConfig(config: MilkifySessionManager.CloudBackendConfig, broadcastToAll: Boolean = true) {
        sessionManager?.saveCloudBackendConfig(config)
        _cloudBackendConfig.value = config
        cloudSyncManager.setActiveBackendConfig(config)
        _userFeedback.value = "Cloud Backend updated to ${config.backendType}!"

        if (broadcastToAll) {
            viewModelScope.launch {
                val currentProducts = repository.getProductsListDirect()
                val currentRates = currentProducts.associate { it.id to it.price }
                val currentRiders = repository.getAllRidersDirect()
                cloudSyncManager.pushProductsAndRatesToCloud(
                    products = currentProducts,
                    rates = currentRates,
                    announcement = _announcementBanner.value,
                    hubAddress = _hubAddress.value,
                    whatsapp = _whatsappNumber.value,
                    riders = currentRiders,
                    cloudConfig = config
                )
                _userFeedback.value = "🚀 Backend ${config.backendType} connected & broadcasted to all 1000+ mobiles!"
            }
        }
    }

    fun testCustomCloudConnection(config: MilkifySessionManager.CloudBackendConfig) {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val diag = cloudSyncManager.testCloudConnection(config)
            _cloudDiagnostics.value = diag
            _isCloudSyncing.value = false
            _userFeedback.value = diag.statusMessage
        }
    }

    fun migrateAllDataToActiveBackend() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val products = repository.getProductsListDirect()
            val rates = products.associate { it.id to it.price }
            val riders = repository.getAllRidersDirect()
            val orders = repository.getAllOrdersDirect()
            val users = repository.getAllUsersDirect()

            cloudSyncManager.pushProductsAndRatesToCloud(
                products = products,
                rates = rates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = riders,
                cloudConfig = _cloudBackendConfig.value
            )

            var orderCount = 0
            orders.forEach { order ->
                if (cloudSyncManager.pushOrderToCloud(order)) {
                    orderCount++
                }
            }

            var customerCount = 0
            users.forEach { user ->
                if (cloudSyncManager.pushCustomerToCloud(user)) {
                    customerCount++
                }
            }

            _isCloudSyncing.value = false
            _userFeedback.value = "✅ Migrated ${products.size} products, ${riders.size} riders, $customerCount customers, and $orderCount orders to ${_cloudBackendConfig.value.backendType}!"
        }
    }

    fun testCloudConnection() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val diag = cloudSyncManager.testCloudConnection()
            _cloudDiagnostics.value = diag
            _isCloudSyncing.value = false
            _userFeedback.value = diag.statusMessage
        }
    }

    fun repairAndResetCloudSlots() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val orders = repository.getAllOrdersDirect()
            val products = repository.getProductsListDirect()
            val rates = products.associate { it.id to it.price }
            val ridersList = repository.getAllRidersDirect()
            val success = cloudSyncManager.repairAndResetCloudSlots(
                allOrders = orders,
                allProducts = products,
                rates = rates,
                riders = ridersList,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value
            )
            _isCloudSyncing.value = false
            if (success) {
                _userFeedback.value = "✅ All 5 Cloud Slots cleaned & re-synced successfully!"
                syncFromCloud(showFeedback = true)
            } else {
                _userFeedback.value = "❌ Cloud reset encountered an issue. Check connection."
            }
        }
    }

    fun pushAllLocalOrdersToCloud() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val orders = repository.getAllOrdersDirect()
            var count = 0
            orders.forEach { order ->
                if (cloudSyncManager.pushOrderToCloud(order)) {
                    count++
                }
            }
            _isCloudSyncing.value = false
            _userFeedback.value = "☁️ Synced $count order(s) directly to Cloud!"
        }
    }

    fun syncAllRidersToCloud() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val products = repository.getProductsListDirect()
            val rates = products.associate { it.id to it.price }
            val ridersList = repository.getAllRidersDirect()
            val success = cloudSyncManager.pushProductsAndRatesToCloud(
                products = products,
                rates = rates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = ridersList
            )
            _isCloudSyncing.value = false
            if (success) {
                _userFeedback.value = "👥 All ${ridersList.size} rider(s) synced to Cloud!"
            } else {
                _userFeedback.value = "Failed to sync riders. Check internet connection."
            }
        }
    }

    // Admin Credentials (default: Aryanx0045@gmail.com / Aryan@123)
    private val _adminEmail = MutableStateFlow("Aryanx0045@gmail.com")
    val adminEmail: StateFlow<String> = _adminEmail.asStateFlow()

    private val _adminPassword = MutableStateFlow("Aryan@123")
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    // Support Contacts (default: 9343688757)
    private val _supportPhone = MutableStateFlow("9343688757")
    val supportPhone: StateFlow<String> = _supportPhone.asStateFlow()

    private val _whatsappNumber = MutableStateFlow("9343688757")
    val whatsappNumber: StateFlow<String> = _whatsappNumber.asStateFlow()

    // Announcement Banner & Hub Settings
    private val _announcementBanner = MutableStateFlow("Fresh Pure Buffalo Milk (FAT 7.2%), Desi Ghee & Cow Milk delivered daily 3:00 AM – 11:00 AM across Joura!")
    val announcementBanner: StateFlow<String> = _announcementBanner.asStateFlow()

    private val _hubAddress = MutableStateFlow("Station Road, Joura, Morena, Madhya Pradesh - 476221")
    val hubAddress: StateFlow<String> = _hubAddress.asStateFlow()

    // PhonePe Celebration Order Confirmation state
    private val _confirmedOrderDetails = MutableStateFlow<PhonePeOrderSuccessData?>(null)
    val confirmedOrderDetails: StateFlow<PhonePeOrderSuccessData?> = _confirmedOrderDetails.asStateFlow()

    fun clearConfirmedOrder() {
        _confirmedOrderDetails.value = null
    }

    fun validateAdminCredentials(email: String, pass: String): Boolean {
        val curEmail = _adminEmail.value.trim()
        val curPass = _adminPassword.value
        return (email.trim().equals(curEmail, ignoreCase = true) && pass == curPass) ||
               (email.trim().equals("Aryanx0045@gmail.com", ignoreCase = true) && pass == "Aryan@123")
    }

    fun updateAdminCredentials(newEmail: String, newPass: String) {
        if (newEmail.isNotBlank()) _adminEmail.value = newEmail.trim()
        if (newPass.isNotBlank()) _adminPassword.value = newPass
        viewModelScope.launch {
            repository.saveSetting("admin_email", _adminEmail.value)
            repository.saveSetting("admin_password", _adminPassword.value)
            _userFeedback.value = "Admin login credentials updated successfully!"
        }
    }

    fun updateHubSettings(
        phone: String = _supportPhone.value,
        whatsapp: String = _whatsappNumber.value,
        announcement: String = _announcementBanner.value,
        address: String = _hubAddress.value
    ) {
        if (phone.isNotBlank()) _supportPhone.value = phone.trim()
        if (whatsapp.isNotBlank()) _whatsappNumber.value = whatsapp.trim()
        if (announcement.isNotBlank()) _announcementBanner.value = announcement.trim()
        if (address.isNotBlank()) _hubAddress.value = address.trim()
        viewModelScope.launch {
            repository.saveSetting("support_phone", _supportPhone.value)
            repository.saveSetting("whatsapp_number", _whatsappNumber.value)
            repository.saveSetting("announcement_banner", _announcementBanner.value)
            repository.saveSetting("address", _hubAddress.value)
            _userFeedback.value = "All Hub & App Master Settings updated successfully!"
            val currentProducts = repository.getProductsListDirect()
            val currentRates = currentProducts.associate { it.id to it.price }
            val currentRiders = repository.getAllRidersDirect()
            cloudSyncManager.pushProductsAndRatesToCloud(
                products = currentProducts,
                rates = currentRates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = currentRiders
            )
        }
    }

    // --- Authentication & Session State ---
    private val _isLoggedIn = MutableStateFlow(false) // App starts at LoginScreen as requested
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _loginSelectedTabIndex = MutableStateFlow(0) // 0: Customer, 1: Rider, 2: Admin
    val loginSelectedTabIndex: StateFlow<Int> = _loginSelectedTabIndex.asStateFlow()

    fun setLoginTab(tabIndex: Int) {
        _loginSelectedTabIndex.value = tabIndex.coerceIn(0, 2)
    }

    fun switchRole(role: AppRole) {
        _currentRole.value = role
        _isLoggedIn.value = false // Require information entry for the chosen panel
        _loginSelectedTabIndex.value = when (role) {
            AppRole.CUSTOMER -> 0
            AppRole.RIDER -> 1
            AppRole.ADMIN -> 2
        }
    }

    fun loginAsCustomer(
        phone: String,
        name: String,
        village: String = "",
        email: String = ""
    ): Boolean {
        if (name.isBlank() || phone.isBlank() || village.isBlank() || email.isBlank()) {
            _userFeedback.value = "⚠️ Bina information no entry! Please fill all details."
            return false
        }
        val cleanPhone = phone.trim()
        val cleanName = name.trim()
        val cleanVillage = village.trim()
        val cleanEmail = email.trim()

        _currentCustomerPhone.value = cleanPhone
        _currentCustomerName.value = cleanName
        _currentCustomerVillage.value = cleanVillage
        _currentCustomerEmail.value = cleanEmail
        _currentRole.value = AppRole.CUSTOMER
        _isLoggedIn.value = true
        sessionManager?.saveCustomerSession(cleanName, cleanPhone, cleanVillage, cleanEmail)
        _userFeedback.value = "Welcome to Milkify, $cleanName!"
        viewModelScope.launch {
            val user = UserEntity(
                role = "CUSTOMER",
                name = cleanName,
                phone = cleanPhone,
                email = cleanEmail,
                address = cleanVillage,
                referralCode = "JOU-${cleanPhone.takeLast(4)}"
            )
            repository.saveUser(user)
            cloudSyncManager.pushCustomerToCloud(user)
            repository.recordUserLogin(
                role = "CUSTOMER",
                name = cleanName,
                phoneOrEmail = "$cleanPhone • $cleanEmail",
                extraInfo = "Locality/Village: $cleanVillage"
            )
        }
        return true
    }

    fun loginAsRider(riderId: Long) {
        _selectedRiderId.value = riderId
        _currentRole.value = AppRole.RIDER
        _isLoggedIn.value = true
        _userFeedback.value = "Delivery Partner logged in successfully!"
        viewModelScope.launch {
            val rider = riders.value.find { it.id == riderId }
            val rName = rider?.name ?: "Rider #$riderId"
            val rPhone = rider?.phone ?: "Delivery Partner"
            repository.recordUserLogin(
                role = "RIDER",
                name = rName,
                phoneOrEmail = rPhone,
                extraInfo = "Vehicle: ${rider?.vehicleNumber ?: "N/A"}"
            )
        }
    }

    fun loginAsRiderWithValidation(
        name: String,
        phone: String,
        vehicleNumber: String,
        pin: String
    ): Boolean {
        if (name.isBlank() || phone.isBlank() || vehicleNumber.isBlank() || pin.isBlank()) {
            _userFeedback.value = "⚠️ Bina information no entry! Fill rider details."
            return false
        }
        if (pin.trim() != "0001") {
            _userFeedback.value = "❌ Incorrect Delivery Hub PIN!"
            return false
        }
        viewModelScope.launch {
            val phoneClean = phone.trim()
            val nameClean = name.trim()
            val vehicleClean = vehicleNumber.trim()
            val match = riders.value.find {
                it.phone.replace(" ", "").endsWith(phoneClean.takeLast(10)) ||
                it.name.equals(nameClean, ignoreCase = true)
            }
            val rId = if (match != null) {
                match.id
            } else {
                repository.saveRider(
                    RiderEntity(
                        name = nameClean,
                        phone = phoneClean,
                        vehicleNumber = vehicleClean,
                        status = "AVAILABLE",
                        isShiftActive = true
                    )
                )
            }
            _selectedRiderId.value = rId
            _currentRole.value = AppRole.RIDER
            _isLoggedIn.value = true
            sessionManager?.saveRiderSession(rId, nameClean, phoneClean, vehicleClean)
            _userFeedback.value = "Rider $nameClean verified & duty started!"
            repository.recordUserLogin(
                role = "RIDER",
                name = nameClean,
                phoneOrEmail = phoneClean,
                extraInfo = "Vehicle: $vehicleClean • PIN Verified"
            )
            // Push updated riders list to cloud so Admin and other devices immediately see this rider
            val allCurrentRiders = repository.getAllRidersDirect()
            val currentProducts = repository.getProductsListDirect()
            val currentRates = currentProducts.associate { it.id to it.price }
            cloudSyncManager.pushProductsAndRatesToCloud(
                products = currentProducts,
                rates = currentRates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = allCurrentRiders
            )
            syncFromCloud(showFeedback = false)
        }
        return true
    }

    fun loginAsAdmin(): Boolean {
        _currentRole.value = AppRole.ADMIN
        _isLoggedIn.value = true
        _userFeedback.value = "Admin Web Panel unlocked."
        viewModelScope.launch {
            repository.recordUserLogin(
                role = "ADMIN",
                name = "Hub Administrator",
                phoneOrEmail = _adminEmail.value,
                extraInfo = "Master Admin Portal Session"
            )
        }
        return true
    }

    fun loginAsAdminWithValidation(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.isBlank()) {
            _userFeedback.value = "⚠️ Bina information no entry! Enter Admin Email & Password."
            return false
        }
        if (validateAdminCredentials(email.trim(), pass.trim())) {
            _currentRole.value = AppRole.ADMIN
            _isLoggedIn.value = true
            sessionManager?.saveAdminSession(email.trim())
            _userFeedback.value = "Admin Web Panel unlocked for ${email.trim()}!"
            viewModelScope.launch {
                repository.recordUserLogin(
                    role = "ADMIN",
                    name = "Hub Administrator",
                    phoneOrEmail = email.trim(),
                    extraInfo = "Admin Panel Authentication"
                )
            }
            return true
        } else {
            _userFeedback.value = "❌ Invalid Admin credentials!"
            return false
        }
    }

    fun logout() {
        sessionManager?.clearSession()
        _isLoggedIn.value = false
        _userFeedback.value = "Logged out successfully"
    }

    // --- Customer Navigation ---
    private val _customerTab = MutableStateFlow(CustomerTab.HOME)
    val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

    fun selectCustomerTab(tab: CustomerTab) {
        _customerTab.value = tab
    }

    // --- Customer Info ---
    private val _currentCustomerPhone = MutableStateFlow("")
    val currentCustomerPhone: StateFlow<String> = _currentCustomerPhone.asStateFlow()

    private val _currentCustomerName = MutableStateFlow("")
    val currentCustomerName: StateFlow<String> = _currentCustomerName.asStateFlow()

    private val _currentCustomerVillage = MutableStateFlow("")
    val currentCustomerVillage: StateFlow<String> = _currentCustomerVillage.asStateFlow()

    private val _currentCustomerEmail = MutableStateFlow("")
    val currentCustomerEmail: StateFlow<String> = _currentCustomerEmail.asStateFlow()

    // --- User Logins History (Admin Audit Log) ---
    val loginRecords: StateFlow<List<LoginRecordEntity>> = repository.allLoginRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteLoginRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteLoginRecord(id)
            _userFeedback.value = "Login record removed."
        }
    }

    fun clearAllLoginRecords() {
        viewModelScope.launch {
            repository.clearAllLoginRecords()
            _userFeedback.value = "All login records cleared."
        }
    }

    // --- Customer Hisaab (Khata Ledger) ---
    val allHisaabEntries: StateFlow<List<HisaabEntryEntity>> = repository.allHisaabEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordCustomerCashPayment(phone: String, name: String, amount: Double, note: String, date: String) {
        viewModelScope.launch {
            repository.recordHisaabEntry(
                HisaabEntryEntity(
                    customerPhone = phone,
                    customerName = name,
                    date = date,
                    entryType = "CASH_PAID",
                    amount = amount,
                    note = note
                )
            )
            _userFeedback.value = "Cash of ₹${amount.toInt()} recorded for $name"
        }
    }

    fun addCustomerBillAdjustment(phone: String, name: String, amount: Double, note: String, date: String) {
        viewModelScope.launch {
            repository.recordHisaabEntry(
                HisaabEntryEntity(
                    customerPhone = phone,
                    customerName = name,
                    date = date,
                    entryType = "BILL",
                    amount = amount,
                    note = note
                )
            )
            _userFeedback.value = "Bill entry of ₹${amount.toInt()} added for $name"
        }
    }

    fun deleteHisaabEntry(entry: HisaabEntryEntity) {
        viewModelScope.launch {
            repository.deleteHisaabEntry(entry)
            _userFeedback.value = "Ledger entry removed"
        }
    }

    // --- Data Flows ---
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val riders: StateFlow<List<RiderEntity>> = repository.allRiders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labReports: StateFlow<List<LabReportEntity>> = repository.visibleLabReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLabReportsAdmin: StateFlow<List<LabReportEntity>> = repository.allLabReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repository.activeCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCouponsAdmin: StateFlow<List<CouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerAddresses: StateFlow<List<AddressEntity>> = repository.getAddressesForCustomer(_currentCustomerPhone.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerNotifications: StateFlow<List<NotificationEntity>> = _currentCustomerPhone
        .flatMapLatest { phone -> repository.getNotifications(phone) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val galleryPhotos: StateFlow<List<GalleryPhotoEntity>> = repository.allGalleryPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val billboardBanners: StateFlow<List<BillboardBannerEntity>> = repository.allBillboardBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<List<AppSettingEntity>> = repository.allSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val users: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cart State ---
    // Map productId -> Pair(Product, quantity)
    private val _cartItems = MutableStateFlow<Map<Long, Pair<ProductEntity, Int>>>(emptyMap())
    val cartItems: StateFlow<Map<Long, Pair<ProductEntity, Int>>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _selectedDeliverySlot = MutableStateFlow("Morning (3:00 AM – 11:00 AM)")
    val selectedDeliverySlot: StateFlow<String> = _selectedDeliverySlot.asStateFlow()

    private val _selectedAddress = MutableStateFlow("42, Station Road, Civil Lines, Joura (476221)")
    val selectedAddress: StateFlow<String> = _selectedAddress.asStateFlow()

    // Detail Dialog State
    private val _selectedProductForDetail = MutableStateFlow<ProductEntity?>(null)
    val selectedProductForDetail: StateFlow<ProductEntity?> = _selectedProductForDetail.asStateFlow()

    // Tracking Order State
    private val _trackingOrderId = MutableStateFlow<Long?>(null)
    val trackingOrderId: StateFlow<Long?> = _trackingOrderId.asStateFlow()

    // Toast/Feedback message
    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    // Admin State
    private val _adminSection = MutableStateFlow(AdminSection.DASHBOARD)
    val adminSection: StateFlow<AdminSection> = _adminSection.asStateFlow()

    fun selectAdminSection(section: AdminSection) {
        _adminSection.value = section
    }

    // Active Rider selection
    private val _selectedRiderId = MutableStateFlow<Long>(1)
    val selectedRiderId: StateFlow<Long> = _selectedRiderId.asStateFlow()

    fun selectRider(riderId: Long) {
        _selectedRiderId.value = riderId
    }

    init {
        viewModelScope.launch {
            repository.ensureDefaultProductsExist()
        }
        viewModelScope.launch {
            repository.allSettings.collect { list ->
                list.forEach { s ->
                    when (s.key) {
                        "support_phone" -> _supportPhone.value = s.value
                        "whatsapp_number" -> _whatsappNumber.value = s.value
                        "admin_email" -> _adminEmail.value = s.value
                        "admin_password" -> _adminPassword.value = s.value
                        "announcement_banner" -> _announcementBanner.value = s.value
                        "address" -> _hubAddress.value = s.value
                    }
                }
            }
        }

        // Restore saved persistent session so user doesn't have to log in repeatedly
        sessionManager?.let { sm ->
            val cloudCfg = sm.getCloudBackendConfig()
            _cloudBackendConfig.value = cloudCfg
            cloudSyncManager.setActiveBackendConfig(cloudCfg)

            val session = sm.getSession()
            if (session.isLoggedIn) {
                _isLoggedIn.value = true
                _currentRole.value = session.role
                if (session.customerName.isNotBlank()) _currentCustomerName.value = session.customerName
                if (session.customerPhone.isNotBlank()) _currentCustomerPhone.value = session.customerPhone
                if (session.customerVillage.isNotBlank()) _currentCustomerVillage.value = session.customerVillage
                if (session.customerEmail.isNotBlank()) _currentCustomerEmail.value = session.customerEmail
                if (session.riderId > 0) _selectedRiderId.value = session.riderId
            } else {
                _isLoggedIn.value = false
                // Pre-fill previously entered details in login forms for convenience
                if (session.customerName.isNotBlank()) _currentCustomerName.value = session.customerName
                if (session.customerPhone.isNotBlank()) _currentCustomerPhone.value = session.customerPhone
                if (session.customerVillage.isNotBlank()) _currentCustomerVillage.value = session.customerVillage
                if (session.customerEmail.isNotBlank()) _currentCustomerEmail.value = session.customerEmail
            }
        }

        // Real-time Cloud Sync for online multi-device synchronization (2.5 seconds loop)
        viewModelScope.launch {
            while (isActive) {
                try {
                    syncFromCloud(showFeedback = false)
                } catch (e: Exception) {
                    android.util.Log.e("MilkifySync", "Background sync loop error: ${e.message}")
                }
                delay(2500) // Fast 2.5-second poll guarantees sub-3-second order delivery to Admin!
            }
        }
    }

    /**
     * Synchronizes customer orders, rates, hub settings, products, and riders from central cloud database
     * so changes made on any mobile (Customer orders, Admin rates/status/rider assignments)
     * are immediately reflected across all mobiles within 2-3 seconds.
     */
    fun syncFromCloud(showFeedback: Boolean = false) {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            var syncedSomething = false

            try {
                // 1. FAST PRIORITY: Fetch incoming customer orders FIRST so Admin receives them in < 3 seconds!
                val cloudOrders = cloudSyncManager.fetchOrdersFromCloud()
                if (cloudOrders.isNotEmpty()) {
                    syncedSomething = true
                    var newlyReceivedOrder: OrderEntity? = null
                    val now = System.currentTimeMillis()

                    cloudOrders.forEach { cloudOrder ->
                        repository.insertOrUpdateCloudOrder(cloudOrder)

                        if (!knownOrderIds.contains(cloudOrder.orderNumber)) {
                            knownOrderIds.add(cloudOrder.orderNumber)
                            // If order was placed within last 15 minutes and is in PLACED status, alert the admin!
                            val ageMs = now - cloudOrder.orderDate
                            if (cloudOrder.orderStatus == "PLACED" && ageMs < 15 * 60 * 1000) {
                                newlyReceivedOrder = cloudOrder
                            }
                        }
                    }

                    if (newlyReceivedOrder != null && _currentRole.value == AppRole.ADMIN) {
                        _incomingOrderAlert.value = newlyReceivedOrder
                        playOrderChimeAndVibrate()
                    }
                } else if (cloudSyncManager.lastHttpErrorCode == 401 && _currentRole.value == AppRole.ADMIN) {
                    _userFeedback.value = "⚠️ Firebase Rules 401: Firebase Console > Rules me .read: true aur .write: true karein"
                }

                // 2. Fetch catalog, rates, riders, announcements
                val cloudData = cloudSyncManager.fetchCloudData()
                if (cloudData != null) {
                    syncedSomething = true
                    val localProducts = repository.getProductsListDirect()

                    // Sync full products catalog from cloud
                    if (cloudData.products.isNotEmpty()) {
                        for (remoteProd in cloudData.products) {
                            val match = localProducts.find {
                                it.id == remoteProd.id ||
                                (it.name.trim().equals(remoteProd.name.trim(), ignoreCase = true) &&
                                 it.volumeLabel.trim().equals(remoteProd.volumeLabel.trim(), ignoreCase = true))
                            }
                            if (match != null) {
                                if (match.price != remoteProd.price || match.isAvailable != remoteProd.isAvailable || match.stock != remoteProd.stock) {
                                    repository.saveProduct(
                                        match.copy(
                                            price = remoteProd.price,
                                            isAvailable = remoteProd.isAvailable,
                                            stock = remoteProd.stock,
                                            badge = remoteProd.badge.ifBlank { match.badge },
                                            description = remoteProd.description.ifBlank { match.description }
                                        )
                                    )
                                }
                            } else {
                                repository.saveProduct(
                                    ProductEntity(
                                        name = remoteProd.name,
                                        volumeLabel = remoteProd.volumeLabel,
                                        volumeMl = remoteProd.volumeMl,
                                        price = remoteProd.price,
                                        description = remoteProd.description,
                                        freshnessInfo = "Milked fresh daily from Joura dairy farms.",
                                        sourceFarm = "Milkify Dairy Estate & Joura Hub, Morena (MP)",
                                        labTestingInfo = "Certified pure by Milkify QC Lab, Joura.",
                                        deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                                        isAvailable = remoteProd.isAvailable,
                                        stock = remoteProd.stock,
                                        category = remoteProd.category,
                                        badge = remoteProd.badge
                                    )
                                )
                            }
                        }
                    }

                    // Ensure all rates in numeric rates map are applied
                    cloudData.rates.forEach { (productId, price) ->
                        repository.updateProductPrice(productId, price)
                    }

                    // Apply named rates
                    if (cloudData.namedRates.isNotEmpty()) {
                        val currentList = repository.getProductsListDirect()
                        currentList.forEach { localProd ->
                            val keys = listOf(
                                "${localProd.category}_${localProd.volumeLabel}".lowercase(),
                                "${localProd.name}_${localProd.volumeLabel}".lowercase(),
                                localProd.name.trim().lowercase(),
                                "${localProd.category.filter { it.isLetter() }}_${localProd.volumeLabel.filter { it.isLetterOrDigit() }}".lowercase(),
                                if (localProd.name.contains("Buffalo", true) && localProd.volumeLabel.contains("1")) "buffalo_1l" else "",
                                if (localProd.name.contains("Buffalo", true) && localProd.volumeLabel.contains("500")) "buffalo_500ml" else "",
                                if (localProd.name.contains("Ghee", true) && localProd.volumeLabel.contains("1")) "ghee_1kg" else "",
                                if (localProd.name.contains("Ghee", true) && localProd.volumeLabel.contains("500")) "ghee_500g" else "",
                                if (localProd.name.contains("Cow", true) && localProd.volumeLabel.contains("2")) "cow_2l" else "",
                                if (localProd.name.contains("Cow", true) && localProd.volumeLabel.contains("1")) "cow_1l" else ""
                            ).filter { it.isNotBlank() }

                            for (k in keys) {
                                val namedPrice = cloudData.namedRates[k]
                                if (namedPrice != null && namedPrice > 0 && namedPrice != localProd.price) {
                                    repository.updateProductPrice(localProd.id, namedPrice)
                                    break
                                }
                            }
                        }
                    }

                    // Sync riders from cloud
                    if (cloudData.riders.isNotEmpty()) {
                        for (remoteRider in cloudData.riders) {
                            repository.insertOrUpdateRider(
                                RiderEntity(
                                    name = remoteRider.name,
                                    phone = remoteRider.phone,
                                    vehicleNumber = remoteRider.vehicleNumber,
                                    status = remoteRider.status,
                                    isShiftActive = remoteRider.isShiftActive
                                )
                            )
                        }
                    }

                    cloudData.announcement?.let { _announcementBanner.value = it }
                    cloudData.hubAddress?.let { _hubAddress.value = it }
                    cloudData.whatsapp?.let { _whatsappNumber.value = it }

                    cloudData.cloudConfig?.let { remoteCfg ->
                        val currentCfg = _cloudBackendConfig.value
                        if (remoteCfg.backendType != "AUTO" && remoteCfg != currentCfg) {
                            sessionManager?.saveCloudBackendConfig(remoteCfg)
                            _cloudBackendConfig.value = remoteCfg
                            cloudSyncManager.setActiveBackendConfig(remoteCfg)
                        }
                    }
                }

                // 3. Sync customer profiles from cloud
                val cloudCustomers = cloudSyncManager.fetchCustomersFromCloud()
                if (cloudCustomers.isNotEmpty()) {
                    syncedSomething = true
                    cloudCustomers.forEach { customer ->
                        repository.saveUser(customer)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MilkifySync", "syncFromCloud error: ${e.message}")
            } finally {
                _lastCloudSyncTime.value = System.currentTimeMillis()
                if (showFeedback) {
                    if (syncedSomething) {
                        _userFeedback.value = "☁️ Online Cloud Synced! Rates & orders updated live from server."
                    } else {
                        _userFeedback.value = "Offline mode. Using local saved data."
                    }
                }
                _isCloudSyncing.value = false
            }
        }
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    fun showFeedback(msg: String) {
        _userFeedback.value = msg
    }

    // Cart Actions
    fun addToCart(product: ProductEntity) {
        val current = _cartItems.value.toMutableMap()
        val currentQty = current[product.id]?.second ?: 0
        current[product.id] = Pair(product, currentQty + 1)
        _cartItems.value = current
        _userFeedback.value = "Added ${product.volumeLabel} milk to cart"
    }

    fun decrementCart(productId: Long) {
        val current = _cartItems.value.toMutableMap()
        val entry = current[productId] ?: return
        if (entry.second > 1) {
            current[productId] = Pair(entry.first, entry.second - 1)
        } else {
            current.remove(productId)
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: Long) {
        val current = _cartItems.value.toMutableMap()
        current.remove(productId)
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _appliedCoupon.value = null
    }

    fun applyCoupon(code: String) {
        val found = coupons.value.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }
        if (found != null) {
            _appliedCoupon.value = found
            _userFeedback.value = "Coupon '${found.code}' applied! Saved discount."
        } else {
            _userFeedback.value = "Invalid coupon code. Try FRESH20"
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun setDeliverySlot(slot: String) {
        _selectedDeliverySlot.value = slot
    }

    fun setDeliveryAddress(addr: String) {
        _selectedAddress.value = addr
    }

    fun openProductDetail(product: ProductEntity) {
        _selectedProductForDetail.value = product
    }

    fun closeProductDetail() {
        _selectedProductForDetail.value = null
    }

    fun openOrderTracking(orderId: Long) {
        _trackingOrderId.value = orderId
        _customerTab.value = CustomerTab.ORDERS
    }

    fun closeOrderTracking() {
        _trackingOrderId.value = null
    }

    // Place Order
    fun checkoutOrder(notes: String? = null, onComplete: (Long) -> Unit = {}) {
        val items = _cartItems.value.values.toList()
        if (items.isEmpty()) {
            _userFeedback.value = "Cart is empty"
            return
        }

        viewModelScope.launch {
            val subtotal = items.sumOf { it.first.price * it.second }
            val coupon = _appliedCoupon.value
            var discount = 0.0
            if (coupon != null) {
                if (coupon.discountType == "PERCENTAGE") {
                    discount = (subtotal * (coupon.discountValue / 100.0)).coerceAtMost(coupon.maxDiscount)
                } else {
                    discount = coupon.discountValue
                }
            }

            val orderId = repository.placeOrder(
                customerName = _currentCustomerName.value,
                customerPhone = _currentCustomerPhone.value,
                deliveryAddress = _selectedAddress.value,
                deliverySlot = _selectedDeliverySlot.value,
                items = items,
                discountAmount = discount,
                notes = notes
            )

            // Cash on Delivery is 100% Free
            val deliveryCharge = 0.0
            val totalAmount = (subtotal + deliveryCharge - discount).coerceAtLeast(0.0)
            val todayStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            repository.recordHisaabEntry(
                HisaabEntryEntity(
                    customerPhone = _currentCustomerPhone.value,
                    customerName = _currentCustomerName.value,
                    date = todayStr,
                    entryType = "BILL",
                    amount = totalAmount,
                    note = "Milk Order ($todayStr)"
                )
            )

            // Fetch actual placed order from database so we have its exact orderNumber and OTP
            val savedOrder = repository.getOrderByIdDirect(orderId)

            // Push order to central online cloud database so Admin Panel on any mobile receives it instantly
            if (savedOrder != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val pushed = cloudSyncManager.pushOrderToCloud(savedOrder)
                        android.util.Log.d("MilkifySync", "⚡ Direct Cloud Push to Firebase: $pushed for ${savedOrder.orderNumber}")
                    } catch (e: Exception) {
                        android.util.Log.e("MilkifySync", "Order push error: ${e.message}")
                    }
                }
            }

            clearCart()
            val itemsSummary = items.joinToString(", ") { "${it.first.name} (${it.first.volumeLabel}) x ${it.second}" }
            val orderNum = savedOrder?.orderNumber ?: "MLK-2026-${kotlin.random.Random.nextInt(1000, 9999)}"
            val otp = savedOrder?.otpCode ?: kotlin.random.Random.nextInt(1000, 9999).toString()
            _confirmedOrderDetails.value = PhonePeOrderSuccessData(
                orderId = orderId,
                orderNumber = orderNum,
                totalAmount = totalAmount,
                itemsSummary = itemsSummary,
                deliverySlot = _selectedDeliverySlot.value,
                deliveryAddress = _selectedAddress.value,
                otpCode = otp,
                customerName = _currentCustomerName.value,
                customerPhone = _currentCustomerPhone.value
            )
            _userFeedback.value = "Order placed successfully! Delivery slot: ${_selectedDeliverySlot.value} (Cash on Delivery Free)"
            _trackingOrderId.value = orderId
            onComplete(orderId)
        }
    }

    // Reorder an existing order
    fun reorder(order: OrderEntity) {
        // Find matching product
        val matchedProduct = products.value.firstOrNull { it.volumeLabel in order.itemsSummary }
            ?: products.value.firstOrNull()
        if (matchedProduct != null) {
            addToCart(matchedProduct)
            _userFeedback.value = "Items re-added to cart! Ready for checkout."
        }
    }

    // Subscriptions
    fun createSubscription(
        productName: String,
        quantityLiters: Double,
        frequency: String,
        slot: String,
        startDate: String,
        endDate: String,
        pricePerDay: Double
    ) {
        viewModelScope.launch {
            val sub = SubscriptionEntity(
                customerName = _currentCustomerName.value,
                customerPhone = _currentCustomerPhone.value,
                productName = productName,
                quantityLiters = quantityLiters,
                frequency = frequency,
                deliverySlot = slot,
                deliveryAddress = _selectedAddress.value,
                startDate = startDate,
                endDate = endDate,
                pricePerDay = pricePerDay,
                status = "ACTIVE"
            )
            repository.createSubscription(sub)
            _userFeedback.value = "Milk subscription started successfully!"
            _customerTab.value = CustomerTab.SUBSCRIPTION
        }
    }

    fun pauseSubscription(id: Long) {
        viewModelScope.launch {
            repository.updateSubscriptionStatus(id, "PAUSED")
            _userFeedback.value = "Subscription paused"
        }
    }

    fun resumeSubscription(id: Long) {
        viewModelScope.launch {
            repository.updateSubscriptionStatus(id, "ACTIVE")
            _userFeedback.value = "Subscription resumed"
        }
    }

    fun cancelSubscription(id: Long) {
        viewModelScope.launch {
            repository.updateSubscriptionStatus(id, "CANCELLED")
            _userFeedback.value = "Subscription cancelled"
        }
    }

    // Support ticket
    fun submitSupportTicket(issueType: String, description: String) {
        viewModelScope.launch {
            val ticketNum = "TCK-${Random.nextInt(10000, 99999)}"
            val ticket = SupportTicketEntity(
                ticketNumber = ticketNum,
                customerPhone = _currentCustomerPhone.value,
                customerName = _currentCustomerName.value,
                issueType = issueType,
                description = description,
                status = "OPEN"
            )
            repository.createTicket(ticket)
            _userFeedback.value = "Ticket #$ticketNum logged. Our Joura support team will call you within 30 mins."
        }
    }

    // Saved Address
    fun addNewAddress(label: String, house: String, street: String, area: String, landmark: String) {
        viewModelScope.launch {
            val newAddr = AddressEntity(
                customerPhone = _currentCustomerPhone.value,
                label = label,
                houseNumber = house,
                street = street,
                area = area,
                city = "Joura",
                pincode = "476221",
                landmark = landmark,
                isDefault = false
            )
            repository.addAddress(newAddr)
            _selectedAddress.value = "$house, $street, $area, Joura - 476221"
            _userFeedback.value = "Address '$label' saved"
        }
    }

    // Rider Actions
    fun riderMarkDeliveredWithOtp(orderId: Long, enteredOtp: String, expectedOtp: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.verifyOtpAndCompleteDelivery(orderId, enteredOtp, expectedOtp)
            if (success) {
                _userFeedback.value = "Delivery verified and marked completed!"
                val order = repository.getOrderByIdDirect(orderId)
                if (order != null) {
                    cloudSyncManager.updateCloudOrderStatus(
                        orderNumber = order.orderNumber,
                        newStatus = "DELIVERED",
                        isDelivered = true,
                        existingOrder = order
                    )
                }
            } else {
                _userFeedback.value = "Invalid OTP. Please check with the customer."
            }
            onResult(success)
        }
    }

    fun riderUpdateStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            _userFeedback.value = "Order #$orderId marked as $newStatus"
            val order = repository.getOrderByIdDirect(orderId)
            if (order != null) {
                cloudSyncManager.updateCloudOrderStatus(
                    orderNumber = order.orderNumber,
                    newStatus = newStatus,
                    existingOrder = order
                )
            }
        }
    }

    fun riderAcceptOrder(orderId: Long, rider: RiderEntity) {
        viewModelScope.launch {
            repository.assignRider(orderId, rider)
            _userFeedback.value = "Order accepted! Assigned to ${rider.name} for delivery."
            val order = repository.getOrderByIdDirect(orderId)
            if (order != null) {
                cloudSyncManager.updateCloudOrderStatus(
                    orderNumber = order.orderNumber,
                    newStatus = "OUT_FOR_DELIVERY",
                    riderId = rider.id,
                    riderName = rider.name,
                    riderPhone = rider.phone,
                    existingOrder = order
                )
            }
        }
    }

    fun toggleRiderShift(riderId: Long, currentShift: Boolean) {
        viewModelScope.launch {
            repository.toggleRiderShift(riderId, !currentShift)
            _userFeedback.value = if (!currentShift) "Shift started! Ready for milk deliveries." else "Shift ended. Great work today!"
        }
    }

    // Admin Actions
    fun adminConfirmOrderByNumber(orderNumber: String) {
        viewModelScope.launch {
            val order = repository.getOrderByOrderNumber(orderNumber)
            if (order != null) {
                adminUpdateOrderStatus(order.id, "CONFIRMED")
            } else {
                cloudSyncManager.updateCloudOrderStatus(orderNumber = orderNumber, newStatus = "CONFIRMED")
            }
            _userFeedback.value = "Order $orderNumber Confirmed!"
        }
    }

    fun adminUpdateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            _userFeedback.value = "Order #$orderId updated to $status"
            val order = repository.getOrderByIdDirect(orderId)
            if (order != null) {
                cloudSyncManager.updateCloudOrderStatus(
                    orderNumber = order.orderNumber,
                    newStatus = status,
                    existingOrder = order
                )
            }
        }
    }

    fun adminAssignRider(orderId: Long, rider: RiderEntity) {
        viewModelScope.launch {
            repository.assignRider(orderId, rider)
            _userFeedback.value = "Rider ${rider.name} assigned to order #$orderId"
            val order = repository.getOrderByIdDirect(orderId)
            if (order != null) {
                val pushed = cloudSyncManager.updateCloudOrderStatus(
                    orderNumber = order.orderNumber,
                    newStatus = "OUT_FOR_DELIVERY",
                    riderId = rider.id,
                    riderName = rider.name,
                    riderPhone = rider.phone,
                    existingOrder = order
                )
                if (pushed) {
                    _userFeedback.value = "Rider ${rider.name} assigned & synced online to Rider Mobile!"
                }
            }
        }
    }

    fun adminUpdateProductPrice(productId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.updateProductPrice(productId, newPrice)
            _userFeedback.value = "Product price updated to ₹$newPrice"
            // Push updated full product catalog and rates to Cloud so other phones receive the new rate immediately
            val allProducts = repository.getProductsListDirect().map {
                if (it.id == productId) it.copy(price = newPrice) else it
            }
            val updatedMap = allProducts.associate { it.id to it.price }.toMutableMap()
            updatedMap[productId] = newPrice
            val currentRiders = repository.getAllRidersDirect()
            val pushed = cloudSyncManager.pushProductsAndRatesToCloud(
                products = allProducts,
                rates = updatedMap,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = currentRiders
            )
            if (pushed) {
                _userFeedback.value = "Product price updated to ₹$newPrice & synced online to all devices!"
            }
        }
    }

    fun adminSaveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
            _userFeedback.value = "Product saved successfully"
            val currentProducts = repository.getProductsListDirect()
            val currentRates = currentProducts.associate { it.id to it.price }
            val currentRiders = repository.getAllRidersDirect()
            cloudSyncManager.pushProductsAndRatesToCloud(
                products = currentProducts,
                rates = currentRates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = currentRiders
            )
        }
    }

    fun adminDeleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _userFeedback.value = "Product removed"
            val currentProducts = repository.getProductsListDirect()
            val currentRates = currentProducts.associate { it.id to it.price }
            val currentRiders = repository.getAllRidersDirect()
            cloudSyncManager.pushProductsAndRatesToCloud(
                products = currentProducts,
                rates = currentRates,
                announcement = _announcementBanner.value,
                hubAddress = _hubAddress.value,
                whatsapp = _whatsappNumber.value,
                riders = currentRiders
            )
        }
    }

    fun adminAddLabReport(report: LabReportEntity) {
        viewModelScope.launch {
            repository.addLabReport(report)
            _userFeedback.value = "Lab report published to Trust section"
        }
    }

    fun adminAddCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
            _userFeedback.value = "Coupon ${coupon.code} created"
        }
    }

    fun adminSaveSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.saveSetting(key, value)
            _userFeedback.value = "Settings updated"
        }
    }

    // Customer Profile Update / Login Mock
    fun loginCustomer(
        phone: String,
        name: String,
        village: String = _currentCustomerVillage.value,
        email: String = _currentCustomerEmail.value
    ) {
        _currentCustomerPhone.value = phone
        _currentCustomerName.value = name
        _currentCustomerVillage.value = village
        _currentCustomerEmail.value = email
        viewModelScope.launch {
            repository.saveUser(
                UserEntity(
                    role = "CUSTOMER",
                    name = name,
                    phone = phone,
                    email = email,
                    address = village,
                    referralCode = "JOU-${phone.takeLast(4)}"
                )
            )
            _userFeedback.value = "Profile updated for $name!"
        }
    }

    // Notification Management
    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead(_currentCustomerPhone.value)
            _userFeedback.value = "All notifications marked as read"
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications(_currentCustomerPhone.value)
            _userFeedback.value = "Notifications cleared"
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun adminBroadcastNotification(title: String, message: String, type: String = "PROMO") {
        viewModelScope.launch {
            repository.sendNotification("ALL", title, message, type)
            _userFeedback.value = "Broadcast notification sent to all customers!"
        }
    }

    // Photo Gallery Management
    fun adminAddGalleryPhoto(title: String, description: String, imageUrl: String, category: String) {
        viewModelScope.launch {
            repository.saveGalleryPhoto(
                GalleryPhotoEntity(
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    category = category
                )
            )
            _userFeedback.value = "Photo added to Dairy Gallery"
        }
    }

    fun adminDeleteGalleryPhoto(photo: GalleryPhotoEntity) {
        viewModelScope.launch {
            repository.deleteGalleryPhoto(photo)
            _userFeedback.value = "Photo removed from Gallery"
        }
    }

    // Billboard Carousel Management
    fun adminAddOrUpdateBillboardBanner(
        id: Long = 0,
        title: String,
        subtitle: String,
        imageUrl: String,
        badge: String,
        orderIndex: Int
    ) {
        viewModelScope.launch {
            repository.saveBillboardBanner(
                BillboardBannerEntity(
                    id = id,
                    title = title,
                    subtitle = subtitle,
                    imageUrl = imageUrl,
                    badge = badge,
                    orderIndex = orderIndex
                )
            )
            _userFeedback.value = "Billboard banner saved"
        }
    }

    fun adminDeleteBillboardBanner(id: Long) {
        viewModelScope.launch {
            repository.deleteBillboardBannerById(id)
            _userFeedback.value = "Billboard banner removed"
        }
    }

    // Product Image Update
    fun adminUpdateProductImage(productId: Long, imageUrl: String) {
        viewModelScope.launch {
            repository.updateProductImageUrl(productId, imageUrl)
            _userFeedback.value = "Product image updated successfully!"
        }
    }
}

class MilkifyViewModelFactory(
    private val repository: MilkifyRepository,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MilkifyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MilkifyViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
