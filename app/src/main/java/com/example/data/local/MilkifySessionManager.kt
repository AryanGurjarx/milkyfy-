package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.viewmodel.AppRole

class MilkifySessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("milkify_user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_ROLE = "key_role"
        private const val KEY_CUST_NAME = "key_cust_name"
        private const val KEY_CUST_PHONE = "key_cust_phone"
        private const val KEY_CUST_VILLAGE = "key_cust_village"
        private const val KEY_CUST_EMAIL = "key_cust_email"
        private const val KEY_RIDER_ID = "key_rider_id"
        private const val KEY_RIDER_NAME = "key_rider_name"
        private const val KEY_RIDER_PHONE = "key_rider_phone"
        private const val KEY_RIDER_VEHICLE = "key_rider_vehicle"
        private const val KEY_ADMIN_EMAIL = "key_admin_email"
        private const val KEY_BACKEND_TYPE = "key_cloud_backend_type"
        private const val KEY_FIREBASE_URL = "key_cloud_firebase_url"
        private const val KEY_FIREBASE_AUTH = "key_cloud_firebase_auth"
        private const val KEY_SUPABASE_URL = "key_cloud_supabase_url"
        private const val KEY_SUPABASE_KEY = "key_cloud_supabase_key"
    }

    data class CloudBackendConfig(
        val backendType: String = "FIREBASE",
        val firebaseUrl: String = "https://milkyfi-default-rtdb.firebaseio.com",
        val firebaseAuth: String = "",
        val supabaseUrl: String = "",
        val supabaseKey: String = ""
    )

    fun getCloudBackendConfig(): CloudBackendConfig {
        val storedType = prefs.getString(KEY_BACKEND_TYPE, "FIREBASE") ?: "FIREBASE"
        val storedUrl = prefs.getString(KEY_FIREBASE_URL, "https://milkyfi-default-rtdb.firebaseio.com") ?: "https://milkyfi-default-rtdb.firebaseio.com"
        val finalType = if (storedType == "AUTO" || storedType.isBlank()) "FIREBASE" else storedType
        val finalUrl = if (storedUrl.isBlank()) "https://milkyfi-default-rtdb.firebaseio.com" else storedUrl

        return CloudBackendConfig(
            backendType = finalType,
            firebaseUrl = finalUrl,
            firebaseAuth = prefs.getString(KEY_FIREBASE_AUTH, "") ?: "",
            supabaseUrl = prefs.getString(KEY_SUPABASE_URL, "") ?: "",
            supabaseKey = prefs.getString(KEY_SUPABASE_KEY, "") ?: ""
        )
    }

    fun saveCloudBackendConfig(config: CloudBackendConfig) {
        prefs.edit()
            .putString(KEY_BACKEND_TYPE, config.backendType)
            .putString(KEY_FIREBASE_URL, config.firebaseUrl)
            .putString(KEY_FIREBASE_AUTH, config.firebaseAuth)
            .putString(KEY_SUPABASE_URL, config.supabaseUrl)
            .putString(KEY_SUPABASE_KEY, config.supabaseKey)
            .apply()
    }

    data class SessionData(
        val isLoggedIn: Boolean,
        val role: AppRole,
        val customerName: String,
        val customerPhone: String,
        val customerVillage: String,
        val customerEmail: String,
        val riderId: Long,
        val riderName: String,
        val riderPhone: String,
        val riderVehicle: String,
        val adminEmail: String
    )

    fun getSession(): SessionData {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val roleStr = prefs.getString(KEY_ROLE, AppRole.CUSTOMER.name) ?: AppRole.CUSTOMER.name
        val role = try {
            AppRole.valueOf(roleStr)
        } catch (e: Exception) {
            AppRole.CUSTOMER
        }

        return SessionData(
            isLoggedIn = isLoggedIn,
            role = role,
            customerName = prefs.getString(KEY_CUST_NAME, "") ?: "",
            customerPhone = prefs.getString(KEY_CUST_PHONE, "") ?: "",
            customerVillage = prefs.getString(KEY_CUST_VILLAGE, "") ?: "",
            customerEmail = prefs.getString(KEY_CUST_EMAIL, "") ?: "",
            riderId = prefs.getLong(KEY_RIDER_ID, 1L),
            riderName = prefs.getString(KEY_RIDER_NAME, "") ?: "",
            riderPhone = prefs.getString(KEY_RIDER_PHONE, "") ?: "",
            riderVehicle = prefs.getString(KEY_RIDER_VEHICLE, "") ?: "",
            adminEmail = prefs.getString(KEY_ADMIN_EMAIL, "") ?: ""
        )
    }

    fun saveCustomerSession(name: String, phone: String, village: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_ROLE, AppRole.CUSTOMER.name)
            .putString(KEY_CUST_NAME, name)
            .putString(KEY_CUST_PHONE, phone)
            .putString(KEY_CUST_VILLAGE, village)
            .putString(KEY_CUST_EMAIL, email)
            .apply()
    }

    fun saveRiderSession(riderId: Long, name: String, phone: String, vehicle: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_ROLE, AppRole.RIDER.name)
            .putLong(KEY_RIDER_ID, riderId)
            .putString(KEY_RIDER_NAME, name)
            .putString(KEY_RIDER_PHONE, phone)
            .putString(KEY_RIDER_VEHICLE, vehicle)
            .apply()
    }

    fun saveAdminSession(email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_ROLE, AppRole.ADMIN.name)
            .putString(KEY_ADMIN_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        // Keeps user details saved so login form can pre-fill, but marks isLoggedIn = false
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}
