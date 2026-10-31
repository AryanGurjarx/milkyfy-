package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val volumeLabel: String,
    val volumeMl: Int,
    val price: Double,
    val description: String,
    val freshnessInfo: String,
    val sourceFarm: String,
    val labTestingInfo: String,
    val deliveryTiming: String,
    val isAvailable: Boolean = true,
    val stock: Int = 100,
    val category: String = "Farm Milk",
    val badge: String = "Farm Fresh",
    val imageUrl: String = ""
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "CUSTOMER", "RIDER", "ADMIN"
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val profilePhoto: String = "",
    val referralCode: String = "",
    val walletBalance: Double = 0.0,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerPhone: String,
    val label: String, // "Home", "Work", "Other"
    val houseNumber: String,
    val street: String,
    val area: String,
    val city: String = "Joura",
    val pincode: String = "476221",
    val landmark: String = "",
    val gpsCoordinates: String = "26.3411° N, 77.8105° E",
    val isDefault: Boolean = false
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryArea: String = "Joura",
    val deliverySlot: String, // "Morning (3:00 AM – 11:00 AM)" or "Evening (3:00 PM – 8:00 PM)"
    val itemsSummary: String,
    val totalItems: Int,
    val subtotal: Double,
    val deliveryCharge: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val paymentMethod: String = "Cash on Delivery (COD)",
    val paymentStatus: String = "PENDING", // "PENDING", "PAID"
    val orderStatus: String = "PLACED", // "PLACED", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"
    val orderDate: Long = System.currentTimeMillis(),
    val deliveryDate: Long = System.currentTimeMillis(),
    val riderId: Long? = null,
    val riderName: String? = null,
    val riderPhone: String? = null,
    val estimatedDeliveryTime: String? = "6:30 AM",
    val otpCode: String = "4829",
    val deliveredAt: Long? = null,
    val customerNotes: String? = null
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val volumeLabel: String,
    val unitPrice: Double,
    val quantity: Int
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val productName: String,
    val quantityLiters: Double,
    val frequency: String, // "Daily", "Alternate Days", "Weekly", "Custom Schedule"
    val deliverySlot: String,
    val deliveryAddress: String,
    val startDate: String,
    val endDate: String,
    val pricePerDay: Double,
    val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "CANCELLED"
    val skippedDates: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "riders")
data class RiderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val vehicleNumber: String,
    val status: String = "AVAILABLE", // "AVAILABLE", "OFFLINE", "ON_DELIVERY"
    val isShiftActive: Boolean = true,
    val shiftStartedAt: Long = System.currentTimeMillis(),
    val todayDeliveries: Int = 0,
    val totalDeliveries: Int = 0,
    val todayEarnings: Double = 0.0,
    val totalEarnings: Double = 0.0
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNumber: String,
    val customerPhone: String,
    val customerName: String,
    val issueType: String,
    val description: String,
    val status: String = "OPEN", // "OPEN", "IN_PROGRESS", "RESOLVED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lab_reports")
data class LabReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportDate: String,
    val title: String,
    val laboratoryName: String,
    val fatPercentage: Double,
    val snfPercentage: Double,
    val microbialCount: String,
    val adulterationStatus: String = "Zero Adulterants (Negative)",
    val temperatureAtCollection: String = "3.8°C Chilled",
    val certifiedBy: String,
    val isVisibleToCustomer: Boolean = true,
    val isLatest: Boolean = true
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val discountType: String, // "PERCENTAGE", "FLAT"
    val discountValue: Double,
    val minOrder: Double = 0.0,
    val maxDiscount: Double = 100.0,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientPhone: String,
    val title: String,
    val message: String,
    val type: String, // "ORDER", "SUBSCRIPTION", "PAYMENT", "PROMO"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "customer_hisaab")
data class HisaabEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerPhone: String,
    val customerName: String,
    val date: String,
    val entryType: String, // "BILL" or "CASH_PAID"
    val amount: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gallery_photos")
data class GalleryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String = "Farm", // "Farm & Cattle", "Milking Process", "Cold Chain", "Quality Lab", "Ghee Making", "Delivery"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "billboard_banners")
data class BillboardBannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val badge: String = "100% PURE",
    val orderIndex: Int = 0
)

@Entity(tableName = "login_records")
data class LoginRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "CUSTOMER", "RIDER", "ADMIN"
    val name: String,
    val phoneOrEmail: String,
    val extraInfo: String = "",
    val loginTime: Long = System.currentTimeMillis(),
    val deviceStatus: String = "Active Session"
)

