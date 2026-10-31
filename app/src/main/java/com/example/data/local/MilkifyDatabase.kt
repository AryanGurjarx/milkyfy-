package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        AddressEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        SubscriptionEntity::class,
        RiderEntity::class,
        SupportTicketEntity::class,
        LabReportEntity::class,
        CouponEntity::class,
        NotificationEntity::class,
        AppSettingEntity::class,
        HisaabEntryEntity::class,
        GalleryPhotoEntity::class,
        BillboardBannerEntity::class,
        LoginRecordEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class MilkifyDatabase : RoomDatabase() {

    abstract fun milkifyDao(): MilkifyDao

    companion object {
        @Volatile
        private var INSTANCE: MilkifyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MilkifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilkifyDatabase::class.java,
                    "milkify_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(MilkifyDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class MilkifyDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.milkifyDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: MilkifyDao) {
            // Seed default products with user requested Buffalo Milk (1L @ 69, 500ml @ 35) & Desi Ghee (1kg @ 700, 500g @ 350)
            val defaultProducts = listOf(
                ProductEntity(
                    id = 1,
                    name = "Farm Fresh Buffalo Milk (भैंस का दूध)",
                    volumeLabel = "1 L",
                    volumeMl = 1000,
                    price = 69.0,
                    description = "100% शुद्ध और ताजा मुर्रा भैंस का दूध। Rich natural sweetness, thick malai layer (FAT 7.2%), ideal for rich tea, kheer, homemade mawa & rabri in Joura.",
                    freshnessInfo = "Milked fresh early morning & evening. Chilled immediately to 4°C to preserve nutrients and thick cream.",
                    sourceFarm = "Milkify Dairy Estate & Joura Hub, Morena (MP)",
                    labTestingInfo = "Daily batch tested: FAT 7.2%, SNF 9.2%, 0% water dilution guaranteed.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 250,
                    category = "Buffalo Milk",
                    badge = "Rich FAT 7.2%",
                    imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 2,
                    name = "Farm Fresh Buffalo Milk (भैंस का दूध)",
                    volumeLabel = "500 ml",
                    volumeMl = 500,
                    price = 35.0,
                    description = "500 ml daily family pack of rich, thick Murrah buffalo milk fresh from Joura rural dairy farms.",
                    freshnessInfo = "Milked within hours of delivery. Sealed food-grade hygienic pouch.",
                    sourceFarm = "Milkify Dairy Estate & Joura Hub, Morena (MP)",
                    labTestingInfo = "Certified pure: high calcium, FAT 7.2%, tested for 18 adulterants.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 300,
                    category = "Buffalo Milk",
                    badge = "Daily Pack",
                    imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 3,
                    name = "Pure Traditional Desi Ghee (शुद्ध देशी घी)",
                    volumeLabel = "1 kg",
                    volumeMl = 1000,
                    price = 700.0,
                    description = "100% शुद्ध बिलोना दानेदार देशी घी। Made from traditional slow-simmering of cultured butter at Joura dairy hub. Heavenly aroma, zero preservatives, natural immunity booster.",
                    freshnessInfo = "Handcrafted in small batches using traditional methods. Sealed airtight jar.",
                    sourceFarm = "Milkify Traditional Bilona Hub, Station Road, Joura (MP)",
                    labTestingInfo = "Zero vegetable oil, zero paraffin, pure natural milk fat certified.",
                    deliveryTiming = "Same-day doorstep delivery across Joura",
                    isAvailable = true,
                    stock = 80,
                    category = "Desi Ghee",
                    badge = "Bilona Danedar",
                    imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 4,
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
                    badge = "Golden Aroma",
                    imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 5,
                    name = "Farm Fresh Pure Cow Milk (गाय का दूध)",
                    volumeLabel = "1 L",
                    volumeMl = 1000,
                    price = 69.0,
                    description = "Direct from healthy grass-fed Gir and Sahiwal cows in Joura. Rich in calcium, Vitamin D and natural light sweetness.",
                    freshnessInfo = "Delivered cold within hours of milking. Clean glass-grade sanitized pouch.",
                    sourceFarm = "Milkify Dairy Estate, Ward 4, Station Road, Joura, Morena (MP)",
                    labTestingInfo = "Batch-tested every dawn. MBRT > 5 hours. Zero chemical preservatives.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 250,
                    category = "Cow Milk",
                    badge = "Most Popular",
                    imageUrl = "https://images.unsplash.com/photo-1528750997573-59b89d56f4f7?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 6,
                    name = "Farm Fresh Pure Cow Milk (गाय का दूध)",
                    volumeLabel = "500 ml",
                    volumeMl = 500,
                    price = 35.0,
                    description = "100% natural, raw & unprocessed cow milk. Milked fresh every morning at our Joura dairy farm.",
                    freshnessInfo = "Milked within 4 hours of delivery. Chilled instantly to 4°C.",
                    sourceFarm = "Milkify Dairy Estate, Ward 4, Station Road, Joura, Morena (MP)",
                    labTestingInfo = "Tested daily for purity, FAT (4.2%) and SNF (8.6%). Certified pure.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 150,
                    category = "Cow Milk",
                    badge = "Daily Essential",
                    imageUrl = "https://images.unsplash.com/photo-1528750997573-59b89d56f4f7?auto=format&fit=crop&w=800&q=80"
                ),
                ProductEntity(
                    id = 7,
                    name = "Farm Fresh Pure Cow Milk (गाय का दूध)",
                    volumeLabel = "2 L",
                    volumeMl = 2000,
                    price = 135.0,
                    description = "Economical 2-Litre family pack. Perfect for joint families, daily tea, fresh paneer, and rich curd at home.",
                    freshnessInfo = "Fresh morning milking batch. Sealed cold packaging.",
                    sourceFarm = "Milkify Dairy Estate, Ward 4, Station Road, Joura, Morena (MP)",
                    labTestingInfo = "Fat content 4.3% with thick malai layer. No water dilution guaranteed.",
                    deliveryTiming = "Morning: 3:00 AM – 11:00 AM | Evening: 3:00 PM – 8:00 PM",
                    isAvailable = true,
                    stock = 120,
                    category = "Cow Milk",
                    badge = "Family Pack",
                    imageUrl = "https://images.unsplash.com/photo-1528750997573-59b89d56f4f7?auto=format&fit=crop&w=800&q=80"
                )
            )
            dao.insertProducts(defaultProducts)

            // Seed default 5 billboard carousel banners
            val defaultBillboardBanners = listOf(
                BillboardBannerEntity(
                    id = 1,
                    title = "100% Shuddh Desi Doodh",
                    subtitle = "Farm to Doorstep in 2 Hours • Joura & Morena",
                    imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=1200&q=80",
                    badge = "100% PURE",
                    orderIndex = 1
                ),
                BillboardBannerEntity(
                    id = 2,
                    title = "Pure Danedar Bilona Ghee",
                    subtitle = "Handcrafted with Traditional Vedic Slow-Simmer Method",
                    imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=1200&q=80",
                    badge = "VEDIC BILONA",
                    orderIndex = 2
                ),
                BillboardBannerEntity(
                    id = 3,
                    title = "Daily Morning & Evening Delivery",
                    subtitle = "Guaranteed 6:00 AM At Doorstep • Fresh & Cold",
                    imageUrl = "https://images.unsplash.com/photo-1527153857715-3908f2ae5e81?auto=format&fit=crop&w=1200&q=80",
                    badge = "6 AM DAILY",
                    orderIndex = 3
                ),
                BillboardBannerEntity(
                    id = 4,
                    title = "High FAT 7.2% Murrah Milk",
                    subtitle = "Thick Cream Layer • Best for Mawa, Tea & Rabri",
                    imageUrl = "https://images.unsplash.com/photo-1576186726580-a816e8b12896?auto=format&fit=crop&w=1200&q=80",
                    badge = "FAT 7.2%",
                    orderIndex = 4
                ),
                BillboardBannerEntity(
                    id = 5,
                    title = "Lab Tested Purity Guarantee",
                    subtitle = "Tested for 18 Adulterants • Zero Chemicals",
                    imageUrl = "https://images.unsplash.com/photo-1588718974598-22ee7a2353a9?auto=format&fit=crop&w=1200&q=80",
                    badge = "LAB CERTIFIED",
                    orderIndex = 5
                )
            )
            dao.insertBillboardBanners(defaultBillboardBanners)

            // Seed Dairy Photo Gallery (Farm, Milking, Chilling, Ghee, Lab, Delivery)
            val defaultGalleryPhotos = listOf(
                GalleryPhotoEntity(
                    id = 1,
                    title = "Our Healthy Murrah Buffaloes & Gir Cows",
                    description = "Fed on green organic fodder, mustard cake and mineral supplements at Joura dairy farm.",
                    imageUrl = "https://images.unsplash.com/photo-1546445317-29f4545e9d53?auto=format&fit=crop&w=800&q=80",
                    category = "Farm & Cattle"
                ),
                GalleryPhotoEntity(
                    id = 2,
                    title = "Hygienic Automated Milking Machine",
                    description = "Untouched by human hands. Direct food-grade suction pipeline into sanitized chillers.",
                    imageUrl = "https://images.unsplash.com/photo-1527153857715-3908f2ae5e81?auto=format&fit=crop&w=800&q=80",
                    category = "Milking Process"
                ),
                GalleryPhotoEntity(
                    id = 3,
                    title = "Instant 4°C Bulk Milk Chiller",
                    description = "Milk cooled within 45 minutes of milking to stop bacterial growth and keep farm freshness intact.",
                    imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80",
                    category = "Cold Chain"
                ),
                GalleryPhotoEntity(
                    id = 4,
                    title = "Clay Pot Traditional Bilona Churning",
                    description = "Slow wood-churned cultured makkhan boiled on low flame for authentic grainy golden aroma.",
                    imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80",
                    category = "Ghee Making"
                ),
                GalleryPhotoEntity(
                    id = 5,
                    title = "Daily Purity & FAT Testing Laboratory",
                    description = "Digital ultrasonic milk analyzers measure accurate Fat, SNF, and density before dispatch.",
                    imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80",
                    category = "Quality Lab"
                ),
                GalleryPhotoEntity(
                    id = 6,
                    title = "Dedicated Joura Delivery Fleet",
                    description = "Insulated milk crates on bikes ensuring delivery by 6:00 AM straight to your kitchen.",
                    imageUrl = "https://images.unsplash.com/photo-1616401784845-180882ba9ba8?auto=format&fit=crop&w=800&q=80",
                    category = "Doorstep Delivery"
                )
            )
            dao.insertGalleryPhotos(defaultGalleryPhotos)

            // Seed default riders
            val defaultRiders = listOf(
                RiderEntity(
                    id = 1,
                    name = "Ramesh Kumar",
                    phone = "+91 98261 45011",
                    vehicleNumber = "MP-06-EA-4122",
                    status = "AVAILABLE",
                    isShiftActive = true,
                    todayDeliveries = 18,
                    totalDeliveries = 412,
                    todayEarnings = 450.0,
                    totalEarnings = 12500.0
                ),
                RiderEntity(
                    id = 2,
                    name = "Vikram Singh",
                    phone = "+91 94251 77209",
                    vehicleNumber = "MP-06-EB-8831",
                    status = "ON_DELIVERY",
                    isShiftActive = true,
                    todayDeliveries = 14,
                    totalDeliveries = 298,
                    todayEarnings = 350.0,
                    totalEarnings = 8900.0
                )
            )
            dao.insertRiders(defaultRiders)

            // Seed default customer user
            dao.insertUser(
                UserEntity(
                    id = 1,
                    role = "CUSTOMER",
                    name = "Aryan Sharma",
                    phone = "+91 98765 43210",
                    email = "aryan.sharma@example.com",
                    address = "House No. 42, Civil Lines, Near Gandhi Chowk, Joura",
                    referralCode = "JOU-MILK50",
                    walletBalance = 150.0
                )
            )

            // Seed default address
            dao.insertAddress(
                AddressEntity(
                    id = 1,
                    customerPhone = "+91 98765 43210",
                    label = "Home",
                    houseNumber = "42",
                    street = "Station Road",
                    area = "Civil Lines",
                    city = "Joura",
                    pincode = "476221",
                    landmark = "Near Gandhi Chowk & Old Post Office",
                    gpsCoordinates = "26.3411° N, 77.8105° E",
                    isDefault = true
                )
            )
            dao.insertAddress(
                AddressEntity(
                    id = 2,
                    customerPhone = "+91 98765 43210",
                    label = "Farm House / Office",
                    houseNumber = "Plot 12B",
                    street = "Kailaras Road",
                    area = "Outer Joura",
                    city = "Joura",
                    pincode = "476221",
                    landmark = "Opposite Grain Market Mandi",
                    gpsCoordinates = "26.3355° N, 77.8220° E",
                    isDefault = false
                )
            )

            // Seed sample orders
            dao.insertOrder(
                OrderEntity(
                    id = 1,
                    orderNumber = "MLK-2026-0811",
                    customerName = "Aryan Sharma",
                    customerPhone = "+91 98765 43210",
                    deliveryAddress = "42, Station Road, Civil Lines, Joura (476221)",
                    deliveryArea = "Joura Civil Lines",
                    deliverySlot = "Morning (3:00 AM – 11:00 AM)",
                    itemsSummary = "Farm Fresh Pure Cow Milk (1 L) x 1",
                    totalItems = 1,
                    subtotal = 69.0,
                    deliveryCharge = 0.0,
                    discountAmount = 0.0,
                    totalAmount = 69.0,
                    paymentMethod = "Cash on Delivery (COD)",
                    paymentStatus = "PENDING",
                    orderStatus = "OUT_FOR_DELIVERY",
                    riderId = 1,
                    riderName = "Ramesh Kumar",
                    riderPhone = "+91 98261 45011",
                    estimatedDeliveryTime = "6:30 AM",
                    otpCode = "4829",
                    customerNotes = "Please place near the doorstep milk bag."
                )
            )

            dao.insertOrder(
                OrderEntity(
                    id = 2,
                    orderNumber = "MLK-2026-0809",
                    customerName = "Aryan Sharma",
                    customerPhone = "+91 98765 43210",
                    deliveryAddress = "42, Station Road, Civil Lines, Joura (476221)",
                    deliveryArea = "Joura Civil Lines",
                    deliverySlot = "Morning (3:00 AM – 11:00 AM)",
                    itemsSummary = "Farm Fresh Pure Cow Milk (2 L) x 1",
                    totalItems = 1,
                    subtotal = 135.0,
                    deliveryCharge = 0.0,
                    discountAmount = 20.0,
                    totalAmount = 115.0,
                    paymentMethod = "Cash on Delivery (COD)",
                    paymentStatus = "PAID",
                    orderStatus = "DELIVERED",
                    riderId = 1,
                    riderName = "Ramesh Kumar",
                    riderPhone = "+91 98261 45011",
                    estimatedDeliveryTime = "6:15 AM",
                    otpCode = "7193",
                    deliveredAt = System.currentTimeMillis() - 86400000L
                )
            )

            // Seed sample active subscription
            dao.insertSubscription(
                SubscriptionEntity(
                    id = 1,
                    customerName = "Aryan Sharma",
                    customerPhone = "+91 98765 43210",
                    productName = "Farm Fresh Pure Cow Milk (1 L)",
                    quantityLiters = 1.0,
                    frequency = "Daily",
                    deliverySlot = "Morning (3:00 AM – 11:00 AM)",
                    deliveryAddress = "42, Station Road, Civil Lines, Joura",
                    startDate = "01 Sep 2026",
                    endDate = "30 Sep 2026",
                    pricePerDay = 69.0,
                    status = "ACTIVE"
                )
            )

            // Seed sample coupons
            val defaultCoupons = listOf(
                CouponEntity(
                    id = 1,
                    code = "FRESH20",
                    discountType = "PERCENTAGE",
                    discountValue = 20.0,
                    minOrder = 100.0,
                    maxDiscount = 50.0,
                    description = "Get 20% off up to ₹50 on orders above ₹100",
                    isActive = true
                ),
                CouponEntity(
                    id = 2,
                    code = "JOURA15",
                    discountType = "FLAT",
                    discountValue = 15.0,
                    minOrder = 60.0,
                    maxDiscount = 15.0,
                    description = "Flat ₹15 off on any morning milk order",
                    isActive = true
                )
            )
            dao.insertCoupons(defaultCoupons)

            // Seed official lab reports
            val defaultLabReports = listOf(
                LabReportEntity(
                    id = 1,
                    reportDate = "18 Sep 2026",
                    title = "Daily Batch Purity & Microbiological Test Report",
                    laboratoryName = "MP State Dairy Quality Control Lab, Morena Division",
                    fatPercentage = 4.8,
                    snfPercentage = 8.9,
                    microbialCount = "< 10,000 CFU/ml (Grade A - Premium Quality)",
                    adulterationStatus = "Zero Adulterants Detected (Urea, Starch, Detergent, Neutralizers: NEGATIVE)",
                    temperatureAtCollection = "3.8°C Instant Chilling",
                    certifiedBy = "Dr. S. K. Verma, Quality Assurance Officer",
                    isVisibleToCustomer = true,
                    isLatest = true
                ),
                LabReportEntity(
                    id = 2,
                    reportDate = "11 Sep 2026",
                    title = "Weekly Antibiotic Residue & Heavy Metal Screen",
                    laboratoryName = "National Dairy Research Partner Laboratory, Gwalior",
                    fatPercentage = 4.7,
                    snfPercentage = 8.8,
                    microbialCount = "< 15,000 CFU/ml (Grade A)",
                    adulterationStatus = "Zero Chemical Residues / 100% Organic Fodder Fed",
                    temperatureAtCollection = "4.0°C Chilled",
                    certifiedBy = "M. Pathak, Senior Food Safety Analyst",
                    isVisibleToCustomer = true,
                    isLatest = false
                )
            )
            dao.insertLabReports(defaultLabReports)

            // Seed initial notifications (ALL broadcast + individual)
            val defaultNotifications = listOf(
                NotificationEntity(
                    id = 1,
                    recipientPhone = "ALL",
                    title = "🌅 Morning Slot Dispatch Live in Joura",
                    message = "Today's fresh morning batch (FAT 7.2%, SNF 9.2%) has been dispatched! Riders Ramesh & Vikram are delivering across Joura sectors.",
                    type = "ORDER",
                    timestamp = System.currentTimeMillis() - 1800000L,
                    isRead = false
                ),
                NotificationEntity(
                    id = 2,
                    recipientPhone = "ALL",
                    title = "🍯 Pure Danedar Desi Ghee Available",
                    message = "New batch of slow-simmered Vedic Bilona Ghee is ready at Joura dairy hub. 1kg @ ₹700, 500g @ ₹350 with free delivery.",
                    type = "PROMO",
                    timestamp = System.currentTimeMillis() - 7200000L,
                    isRead = false
                ),
                NotificationEntity(
                    id = 3,
                    recipientPhone = "ALL",
                    title = "🧪 Daily Purity & Lab Test Report",
                    message = "Today's milk verified 100% pure with 0% water dilution and zero chemical additives by Joura Dairy Testing Lab.",
                    type = "QUALITY",
                    timestamp = System.currentTimeMillis() - 14400000L,
                    isRead = false
                ),
                NotificationEntity(
                    id = 4,
                    recipientPhone = "+91 98765 43210",
                    title = "Delivery Partner Assigned",
                    message = "Rider Ramesh Kumar is out with your fresh milk bottle! Estimated delivery 6:30 AM.",
                    type = "ORDER",
                    timestamp = System.currentTimeMillis() - 3600000L,
                    isRead = false
                ),
                NotificationEntity(
                    id = 5,
                    recipientPhone = "9876543210",
                    title = "Welcome to Milkify Joura",
                    message = "Enjoy 100% farm-fresh buffalo & cow milk delivered cold to your doorstep every morning.",
                    type = "PROMO",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    isRead = false
                )
            )
            defaultNotifications.forEach { dao.insertNotification(it) }

            // Seed app settings
            val defaultSettings = listOf(
                AppSettingEntity("business_name", "Milkify Farm Fresh Dairy"),
                AppSettingEntity("tagline", "Fresh Milk. Direct From Our Farm."),
                AppSettingEntity("address", "Station Road, Joura, Morena, Madhya Pradesh - 476221"),
                AppSettingEntity("support_phone", "9343688757"),
                AppSettingEntity("whatsapp_number", "9343688757"),
                AppSettingEntity("admin_email", "Aryanx0045@gmail.com"),
                AppSettingEntity("admin_password", "Aryan@123"),
                AppSettingEntity("announcement_banner", "Fresh Pure Buffalo & Cow Milk delivered daily 3:00 AM – 11:00 AM in Joura!"),
                AppSettingEntity("delivery_charge", "0.0"),
                AppSettingEntity("min_order_value", "35.0"),
                AppSettingEntity("morning_window", "3:00 AM – 11:00 AM"),
                AppSettingEntity("evening_window", "3:00 PM – 8:00 PM"),
                AppSettingEntity("service_areas", "Joura Town, Station Road, Civil Lines, Kailaras Mor, Pagara Road")
            )
            dao.insertSettings(defaultSettings)

            // Seed initial Customer Hisaab / Khata records (Offline Ledger)
            val defaultHisaab = listOf(
                HisaabEntryEntity(
                    id = 1,
                    customerPhone = "+91 98765 43210",
                    customerName = "Aryan Sharma",
                    date = "15 Sep 2026",
                    entryType = "BILL",
                    amount = 414.0,
                    note = "Weekly Milk Deliveries (6 Days x 1L)",
                    timestamp = System.currentTimeMillis() - 86400000L * 4
                ),
                HisaabEntryEntity(
                    id = 2,
                    customerPhone = "+91 98765 43210",
                    customerName = "Aryan Sharma",
                    date = "16 Sep 2026",
                    entryType = "CASH_PAID",
                    amount = 400.0,
                    note = "Cash handed to delivery partner Ramesh",
                    timestamp = System.currentTimeMillis() - 86400000L * 3
                ),
                HisaabEntryEntity(
                    id = 3,
                    customerPhone = "+91 98765 43210",
                    customerName = "Aryan Sharma",
                    date = "18 Sep 2026",
                    entryType = "BILL",
                    amount = 69.0,
                    note = "Morning Milk Order #MLK-2026-0811",
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )
            dao.insertHisaabEntries(defaultHisaab)
        }
    }
}
