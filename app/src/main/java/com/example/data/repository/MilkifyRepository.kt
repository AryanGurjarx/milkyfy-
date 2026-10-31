package com.example.data.repository

import com.example.data.local.dao.MilkifyDao
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
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RiderEntity
import com.example.data.local.entities.SubscriptionEntity
import com.example.data.local.entities.SupportTicketEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class MilkifyRepository(private val dao: MilkifyDao) {

    // --- Products ---
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()

    suspend fun ensureDefaultProductsExist() {
        val existing = dao.getProductsList()
        val hasBuffalo1L = existing.any { it.name.contains("Buffalo", ignoreCase = true) && it.volumeLabel == "1 L" }
        val hasBuffalo500ml = existing.any { it.name.contains("Buffalo", ignoreCase = true) && (it.volumeLabel == "500 ml" || it.volumeLabel == "500 g") }
        val hasGhee1kg = existing.any { it.name.contains("Ghee", ignoreCase = true) && (it.volumeLabel == "1 kg" || it.volumeLabel == "1000 g" || it.volumeLabel == "1 L") }
        val hasGhee500g = existing.any { it.name.contains("Ghee", ignoreCase = true) && (it.volumeLabel == "500 g" || it.volumeLabel == "500 ml") }

        val toInsert = mutableListOf<ProductEntity>()
        if (!hasBuffalo1L) {
            toInsert.add(
                ProductEntity(
                    name = "Farm Fresh Buffalo Milk (भैंस का दूध)",
                    volumeLabel = "1 L",
                    volumeMl = 1000,
                    price = 69.0,
                    description = "100% शुद्ध और ताजा मुर्रा भैंस का दूध। Rich natural sweetness, thick malai layer (FAT 7.2%), ideal for rich tea, kheer & mawa in Joura.",
                    freshnessInfo = "Milked fresh early morning & evening. Chilled immediately to 4°C.",
                    sourceFarm = "Milkify Dairy Estate & Joura Hub, Morena (MP)",
                    labTestingInfo = "Daily batch tested: FAT 7.2%, SNF 9.2%, certified pure.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 250,
                    category = "Buffalo Milk",
                    badge = "Rich FAT 7.2%"
                )
            )
        }
        if (!hasBuffalo500ml) {
            toInsert.add(
                ProductEntity(
                    name = "Farm Fresh Buffalo Milk (भैंस का दूध)",
                    volumeLabel = "500 ml",
                    volumeMl = 500,
                    price = 35.0,
                    description = "500 ml daily pack of rich, thick Murrah buffalo milk fresh from Joura rural dairy farms.",
                    freshnessInfo = "Milked within hours of delivery. Sealed food-grade hygienic pouch.",
                    sourceFarm = "Milkify Dairy Estate & Joura Hub, Morena (MP)",
                    labTestingInfo = "Certified pure: high calcium, FAT 7.2%, 0% adulteration.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 300,
                    category = "Buffalo Milk",
                    badge = "Daily Pack"
                )
            )
        }
        if (!hasGhee1kg) {
            toInsert.add(
                ProductEntity(
                    name = "Pure Traditional Desi Ghee (शुद्ध देशी घी)",
                    volumeLabel = "1 kg",
                    volumeMl = 1000,
                    price = 700.0,
                    description = "100% शुद्ध बिलोना दानेदार देशी घी। Made from traditional slow-simmering of cultured butter at Joura dairy hub. Heavenly aroma, zero preservatives.",
                    freshnessInfo = "Handcrafted in small batches using traditional methods. Sealed airtight jar.",
                    sourceFarm = "Milkify Traditional Bilona Hub, Station Road, Joura (MP)",
                    labTestingInfo = "Zero vegetable oil, zero paraffin, pure natural milk fat certified.",
                    deliveryTiming = "Same-day doorstep delivery across Joura",
                    isAvailable = true,
                    stock = 80,
                    category = "Desi Ghee",
                    badge = "Bilona Danedar"
                )
            )
        }
        if (!hasGhee500g) {
            toInsert.add(
                ProductEntity(
                    name = "Pure Traditional Desi Ghee (शुद्ध देशी घी)",
                    volumeLabel = "500 g",
                    volumeMl = 500,
                    price = 350.0,
                    description = "500 g pack of authentic golden danedar desi ghee. Rich fragrance and authentic grainy texture for your daily rotis, parathas, and sweets.",
                    freshnessInfo = "Slow-cooked traditionally with zero additives or synthetic flavors.",
                    sourceFarm = "Milkify Traditional Bilona Hub, Station Road, Joura (MP)",
                    labTestingInfo = "100% pure desi ghee with authentic aroma guarantee.",
                    deliveryTiming = "Same-day doorstep delivery across Joura",
                    isAvailable = true,
                    stock = 120,
                    category = "Desi Ghee",
                    badge = "Golden Aroma"
                )
            )
        }
        if (toInsert.isNotEmpty()) {
            dao.insertProducts(toInsert)
        }
    }

    suspend fun getProductsListDirect(): List<ProductEntity> {
        return dao.getProductsList()
    }

    suspend fun updateProductPrice(id: Long, newPrice: Double) {
        dao.updateProductPrice(id, newPrice)
    }

    suspend fun saveProduct(product: ProductEntity) {
        if (product.id == 0L) {
            dao.insertProduct(product)
        } else {
            dao.updateProduct(product)
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.deleteProduct(product)
    }

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    fun getUserByPhone(phone: String): Flow<UserEntity?> = dao.getUserByPhone(phone)

    suspend fun getAllUsersDirect(): List<UserEntity> = dao.getAllUsersDirect()

    suspend fun saveUser(user: UserEntity): Long {
        return dao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        dao.updateUser(user)
    }

    // --- Addresses ---
    fun getAddressesForCustomer(phone: String): Flow<List<AddressEntity>> =
        dao.getAddressesForCustomer(phone)

    suspend fun addAddress(address: AddressEntity): Long {
        return dao.insertAddress(address)
    }

    suspend fun deleteAddress(address: AddressEntity) {
        dao.deleteAddress(address)
    }

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getOrdersForCustomer(phone: String): Flow<List<OrderEntity>> =
        dao.getOrdersForCustomer(phone)

    fun getOrdersForRider(riderId: Long): Flow<List<OrderEntity>> =
        dao.getOrdersForRider(riderId)

    fun getOrderById(orderId: Long): Flow<OrderEntity?> = dao.getOrderById(orderId)

    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        deliverySlot: String,
        items: List<Pair<ProductEntity, Int>>,
        discountAmount: Double = 0.0,
        notes: String? = null
    ): Long {
        val totalItems = items.sumOf { it.second }
        val subtotal = items.sumOf { it.first.price * it.second }
        // Cash on Delivery is 100% FREE for all orders as requested
        val deliveryCharge = 0.0
        val totalAmount = (subtotal + deliveryCharge - discountAmount).coerceAtLeast(0.0)
        val orderNum = "MLK-2026-${Random.nextInt(1000, 9999)}"
        val randomOtp = (1000 + Random.nextInt(9000)).toString()
        val itemsSummary = items.joinToString(", ") { "${it.first.name} (${it.first.volumeLabel}) x ${it.second}" }

        val order = OrderEntity(
            orderNumber = orderNum,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            deliveryArea = "Joura",
            deliverySlot = deliverySlot,
            itemsSummary = itemsSummary,
            totalItems = totalItems,
            subtotal = subtotal,
            deliveryCharge = deliveryCharge,
            discountAmount = discountAmount,
            totalAmount = totalAmount,
            paymentMethod = "Cash on Delivery (COD)",
            paymentStatus = "PENDING",
            orderStatus = "PLACED",
            otpCode = randomOtp,
            customerNotes = notes
        )
        val orderId = dao.insertOrder(order)

        val orderItems = items.map {
            OrderItemEntity(
                orderId = orderId,
                productId = it.first.id,
                productName = it.first.name,
                volumeLabel = it.first.volumeLabel,
                unitPrice = it.first.price,
                quantity = it.second
            )
        }
        dao.insertOrderItems(orderItems)

        // Add Notification
        dao.insertNotification(
            NotificationEntity(
                recipientPhone = customerPhone,
                title = "Order Placed Successfully (#$orderNum)",
                message = "Your order for $totalItems item(s) has been placed for $deliverySlot delivery. COD: ₹${totalAmount.toInt()}.",
                type = "ORDER"
            )
        )
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun assignRider(orderId: Long, rider: RiderEntity) {
        dao.assignRiderToOrder(orderId, rider.id, rider.name, rider.phone)
        val order = dao.getAllOrders()
        dao.insertNotification(
            NotificationEntity(
                recipientPhone = rider.phone,
                title = "New Delivery Assigned",
                message = "You have been assigned order #$orderId in Joura. Check delivery list.",
                type = "ORDER"
            )
        )
    }

    suspend fun verifyOtpAndCompleteDelivery(orderId: Long, enteredOtp: String, expectedOtp: String): Boolean {
        if (enteredOtp.trim() == expectedOtp.trim()) {
            dao.completeDelivery(orderId)
            return true
        }
        return false
    }

    suspend fun getOrderByOrderNumber(orderNumber: String): OrderEntity? =
        dao.getOrderByOrderNumber(orderNumber)

    suspend fun getOrderByIdDirect(orderId: Long): OrderEntity? =
        dao.getOrderByIdDirect(orderId)

    suspend fun getAllOrdersDirect(): List<OrderEntity> =
        dao.getAllOrdersDirect()

    suspend fun insertOrUpdateCloudOrder(order: OrderEntity): Long {
        val existing = dao.getOrderByOrderNumber(order.orderNumber)
        return if (existing != null) {
            val riderId = order.riderId ?: existing.riderId
            val riderName = order.riderName ?: existing.riderName
            val riderPhone = order.riderPhone ?: existing.riderPhone
            val status = if (existing.orderStatus == "DELIVERED" || order.orderStatus == "DELIVERED") {
                "DELIVERED"
            } else if (order.orderStatus != "PLACED") {
                order.orderStatus
            } else if (existing.orderStatus != "PLACED") {
                existing.orderStatus
            } else {
                order.orderStatus
            }
            val paymentStatus = if (order.paymentStatus == "PAID" || existing.paymentStatus == "PAID" || status == "DELIVERED") {
                "PAID"
            } else {
                order.paymentStatus
            }
            val deliveredAt = order.deliveredAt ?: existing.deliveredAt
            val updated = order.copy(
                id = existing.id,
                riderId = riderId,
                riderName = riderName,
                riderPhone = riderPhone,
                orderStatus = status,
                paymentStatus = paymentStatus,
                deliveredAt = deliveredAt
            )
            dao.updateOrder(updated)
            existing.id
        } else {
            val newId = dao.insertOrder(order.copy(id = 0))
            dao.insertNotification(
                NotificationEntity(
                    recipientPhone = order.customerPhone,
                    title = "Order Received by Joura Admin Hub",
                    message = "Order ${order.orderNumber} is confirmed by Joura Dairy Hub for ${order.deliverySlot}.",
                    type = "ORDER"
                )
            )
            newId
        }
    }

    // --- Subscriptions ---
    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()

    fun getSubscriptionsForCustomer(phone: String): Flow<List<SubscriptionEntity>> =
        dao.getSubscriptionsForCustomer(phone)

    suspend fun createSubscription(subscription: SubscriptionEntity): Long {
        val id = dao.insertSubscription(subscription)
        dao.insertNotification(
            NotificationEntity(
                recipientPhone = subscription.customerPhone,
                title = "Subscription Started!",
                message = "Your daily ${subscription.quantityLiters}L milk delivery subscription has been activated from ${subscription.startDate}.",
                type = "SUBSCRIPTION"
            )
        )
        return id
    }

    suspend fun updateSubscriptionStatus(id: Long, status: String) {
        dao.updateSubscriptionStatus(id, status)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        dao.updateSubscription(subscription)
    }

    // --- Riders ---
    val allRiders: Flow<List<RiderEntity>> = dao.getAllRiders()

    suspend fun getAllRidersDirect(): List<RiderEntity> = dao.getAllRidersDirect()

    suspend fun saveRider(rider: RiderEntity): Long {
        return if (rider.id == 0L) {
            dao.insertRider(rider)
        } else {
            dao.updateRider(rider)
            rider.id
        }
    }

    suspend fun insertOrUpdateRider(rider: RiderEntity): Long {
        val existingList = dao.getAllRidersDirect()
        val cleanPhone = rider.phone.replace(" ", "").takeLast(10)
        val match = existingList.find {
            (cleanPhone.length >= 8 && it.phone.replace(" ", "").endsWith(cleanPhone)) ||
            it.name.trim().equals(rider.name.trim(), ignoreCase = true)
        }
        return if (match != null) {
            val updated = rider.copy(
                id = match.id,
                vehicleNumber = if (rider.vehicleNumber.isNotBlank()) rider.vehicleNumber else match.vehicleNumber,
                isShiftActive = rider.isShiftActive,
                status = rider.status
            )
            dao.updateRider(updated)
            match.id
        } else {
            dao.insertRider(rider.copy(id = 0))
        }
    }

    suspend fun toggleRiderShift(riderId: Long, isShiftActive: Boolean) {
        dao.updateRiderShift(riderId, isShiftActive)
    }

    // --- Lab Reports ---
    val allLabReports: Flow<List<LabReportEntity>> = dao.getAllLabReports()
    val visibleLabReports: Flow<List<LabReportEntity>> = dao.getVisibleLabReports()

    suspend fun addLabReport(report: LabReportEntity): Long {
        return dao.insertLabReport(report)
    }

    // --- Support Tickets ---
    val allTickets: Flow<List<SupportTicketEntity>> = dao.getAllTickets()

    fun getTicketsForCustomer(phone: String): Flow<List<SupportTicketEntity>> =
        dao.getTicketsForCustomer(phone)

    suspend fun createTicket(ticket: SupportTicketEntity): Long {
        return dao.insertTicket(ticket)
    }

    suspend fun updateTicketStatus(id: Long, status: String) {
        dao.updateTicketStatus(id, status)
    }

    // --- Coupons ---
    val activeCoupons: Flow<List<CouponEntity>> = dao.getActiveCoupons()
    val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()

    suspend fun saveCoupon(coupon: CouponEntity): Long {
        return dao.insertCoupon(coupon)
    }

    // --- Notifications ---
    fun getNotifications(phone: String): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(phone)

    suspend fun sendNotification(phone: String, title: String, message: String, type: String): Long {
        return dao.insertNotification(
            NotificationEntity(
                recipientPhone = phone,
                title = title,
                message = message,
                type = type
            )
        )
    }

    suspend fun markAllNotificationsRead(phone: String) {
        dao.markAllNotificationsRead(phone)
    }

    suspend fun clearAllNotifications(phone: String) {
        dao.clearAllNotificationsForUser(phone)
    }

    suspend fun deleteNotification(id: Long) {
        dao.deleteNotificationById(id)
    }

    // --- Settings ---
    val allSettings: Flow<List<AppSettingEntity>> = dao.getAllSettings()

    suspend fun saveSetting(key: String, value: String) {
        dao.insertSetting(AppSettingEntity(key, value))
    }

    // --- Customer Hisaab (Khata) ---
    val allHisaabEntries: Flow<List<HisaabEntryEntity>> = dao.getAllHisaabEntries()

    fun getHisaabForCustomer(phone: String): Flow<List<HisaabEntryEntity>> =
        dao.getHisaabForCustomer(phone)

    suspend fun recordHisaabEntry(entry: HisaabEntryEntity): Long {
        return dao.insertHisaabEntry(entry)
    }

    suspend fun deleteHisaabEntry(entry: HisaabEntryEntity) {
        dao.deleteHisaabEntry(entry)
    }

    // --- Dairy Photo Gallery ---
    val allGalleryPhotos: Flow<List<GalleryPhotoEntity>> = dao.getAllGalleryPhotos()

    suspend fun saveGalleryPhoto(photo: GalleryPhotoEntity): Long {
        return dao.insertGalleryPhoto(photo)
    }

    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity) {
        dao.deleteGalleryPhoto(photo)
    }

    suspend fun deleteGalleryPhotoById(id: Long) {
        dao.deleteGalleryPhotoById(id)
    }

    // --- Billboard Banners ---
    val allBillboardBanners: Flow<List<BillboardBannerEntity>> = dao.getAllBillboardBanners()

    suspend fun saveBillboardBanner(banner: BillboardBannerEntity): Long {
        return dao.insertBillboardBanner(banner)
    }

    suspend fun deleteBillboardBannerById(id: Long) {
        dao.deleteBillboardBannerById(id)
    }

    suspend fun updateProductImageUrl(productId: Long, imageUrl: String) {
        dao.updateProductImageUrl(productId, imageUrl)
    }

    // --- User Logins History ---
    val allLoginRecords: Flow<List<LoginRecordEntity>> = dao.getAllLoginRecords()

    suspend fun recordUserLogin(
        role: String,
        name: String,
        phoneOrEmail: String,
        extraInfo: String = ""
    ): Long {
        return dao.insertLoginRecord(
            LoginRecordEntity(
                role = role,
                name = name,
                phoneOrEmail = phoneOrEmail,
                extraInfo = extraInfo,
                loginTime = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteLoginRecord(id: Long) {
        dao.deleteLoginRecordById(id)
    }

    suspend fun clearAllLoginRecords() {
        dao.clearAllLoginRecords()
    }
}

