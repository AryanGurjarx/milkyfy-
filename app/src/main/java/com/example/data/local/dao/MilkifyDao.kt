package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface MilkifyDao {

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY id ASC")
    suspend fun getProductsList(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET price = :price WHERE id = :id")
    suspend fun updateProductPrice(id: Long, price: Double)

    // --- Users ---
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    fun getUserByPhone(phone: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhoneDirect(phone: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registeredAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersDirect(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // --- Addresses ---
    @Query("SELECT * FROM addresses WHERE customerPhone = :phone")
    fun getAddressesForCustomer(phone: String): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerPhone = :phone ORDER BY orderDate DESC")
    fun getOrdersForCustomer(phone: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE riderId = :riderId ORDER BY orderDate DESC")
    fun getOrdersForRider(riderId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderByIdDirect(orderId: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByOrderNumber(orderNumber: String): OrderEntity?

    @Query("SELECT * FROM orders")
    suspend fun getAllOrdersDirect(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("UPDATE orders SET riderId = :riderId, riderName = :riderName, riderPhone = :riderPhone, orderStatus = 'OUT_FOR_DELIVERY' WHERE id = :orderId")
    suspend fun assignRiderToOrder(orderId: Long, riderId: Long, riderName: String, riderPhone: String)

    @Query("UPDATE orders SET orderStatus = 'DELIVERED', deliveredAt = :deliveredAt, paymentStatus = 'PAID' WHERE id = :orderId")
    suspend fun completeDelivery(orderId: Long, deliveredAt: Long = System.currentTimeMillis())

    // --- Order Items ---
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // --- Subscriptions ---
    @Query("SELECT * FROM subscriptions ORDER BY createdAt DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscriptionsDirect(): List<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions WHERE customerPhone = :phone AND productName = :productName LIMIT 1")
    suspend fun findExistingSubscription(phone: String, productName: String): SubscriptionEntity?

    @Query("SELECT * FROM subscriptions WHERE customerPhone = :phone ORDER BY createdAt DESC")
    fun getSubscriptionsForCustomer(phone: String): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET status = :status WHERE id = :id")
    suspend fun updateSubscriptionStatus(id: Long, status: String)

    // --- Riders ---
    @Query("SELECT * FROM riders")
    fun getAllRiders(): Flow<List<RiderEntity>>

    @Query("SELECT * FROM riders WHERE id = :id")
    fun getRiderById(id: Long): Flow<RiderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRider(rider: RiderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiders(riders: List<RiderEntity>)

    @Update
    suspend fun updateRider(rider: RiderEntity)

    @Query("UPDATE riders SET isShiftActive = :isShiftActive WHERE id = :id")
    suspend fun updateRiderShift(id: Long, isShiftActive: Boolean)

    @Query("SELECT * FROM riders")
    suspend fun getAllRidersDirect(): List<RiderEntity>

    @Query("SELECT * FROM riders WHERE phone = :phone LIMIT 1")
    suspend fun getRiderByPhoneDirect(phone: String): RiderEntity?

    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets")
    suspend fun getAllTicketsDirect(): List<SupportTicketEntity>

    @Query("SELECT * FROM support_tickets WHERE ticketNumber = :ticketNum LIMIT 1")
    suspend fun getTicketByNumber(ticketNum: String): SupportTicketEntity?

    @Query("SELECT * FROM support_tickets WHERE customerPhone = :phone ORDER BY createdAt DESC")
    fun getTicketsForCustomer(phone: String): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity): Long

    @Query("UPDATE support_tickets SET status = :status WHERE id = :id")
    suspend fun updateTicketStatus(id: Long, status: String)

    // --- Lab Reports ---
    @Query("SELECT * FROM lab_reports ORDER BY id DESC")
    fun getAllLabReports(): Flow<List<LabReportEntity>>

    @Query("SELECT * FROM lab_reports WHERE isVisibleToCustomer = 1 ORDER BY id DESC")
    fun getVisibleLabReports(): Flow<List<LabReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabReport(report: LabReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabReports(reports: List<LabReportEntity>)

    // --- Coupons ---
    @Query("SELECT * FROM coupons WHERE isActive = 1")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<CouponEntity>)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE recipientPhone = :phone OR recipientPhone = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForUser(phone: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientPhone = :phone OR recipientPhone = 'ALL'")
    suspend fun markAllNotificationsRead(phone: String)

    @Query("DELETE FROM notifications WHERE recipientPhone = :phone OR recipientPhone = 'ALL'")
    suspend fun clearAllNotificationsForUser(phone: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    // --- Settings ---
    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<AppSettingEntity>)

    // --- Customer Hisaab (Khata) ---
    @Query("SELECT * FROM customer_hisaab ORDER BY timestamp DESC")
    fun getAllHisaabEntries(): Flow<List<HisaabEntryEntity>>

    @Query("SELECT * FROM customer_hisaab WHERE customerPhone = :phone ORDER BY timestamp DESC")
    fun getHisaabForCustomer(phone: String): Flow<List<HisaabEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHisaabEntry(entry: HisaabEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHisaabEntries(entries: List<HisaabEntryEntity>)

    @Delete
    suspend fun deleteHisaabEntry(entry: HisaabEntryEntity)

    // --- Dairy Photo Gallery ---
    @Query("SELECT * FROM gallery_photos ORDER BY timestamp DESC")
    fun getAllGalleryPhotos(): Flow<List<GalleryPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPhoto(photo: GalleryPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPhotos(photos: List<GalleryPhotoEntity>)

    @Delete
    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity)

    @Query("DELETE FROM gallery_photos WHERE id = :id")
    suspend fun deleteGalleryPhotoById(id: Long)

    // --- Billboard Banners (5+ images) ---
    @Query("SELECT * FROM billboard_banners ORDER BY orderIndex ASC")
    fun getAllBillboardBanners(): Flow<List<BillboardBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillboardBanner(banner: BillboardBannerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillboardBanners(banners: List<BillboardBannerEntity>)

    @Query("DELETE FROM billboard_banners WHERE id = :id")
    suspend fun deleteBillboardBannerById(id: Long)

    @Query("UPDATE products SET imageUrl = :imageUrl WHERE id = :productId")
    suspend fun updateProductImageUrl(productId: Long, imageUrl: String)

    // --- User Login History / Sessions ---
    @Query("SELECT * FROM login_records ORDER BY loginTime DESC")
    fun getAllLoginRecords(): Flow<List<LoginRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginRecord(record: LoginRecordEntity): Long

    @Query("DELETE FROM login_records WHERE id = :id")
    suspend fun deleteLoginRecordById(id: Long)

    @Query("DELETE FROM login_records")
    suspend fun clearAllLoginRecords()
}

