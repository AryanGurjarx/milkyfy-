package com.example.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyBorderDark
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyOffWhite
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.MilkifyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: MilkifyViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val preselectedTab by viewModel.loginSelectedTabIndex.collectAsState()
    var selectedRoleIndex by remember(preselectedTab) { mutableStateOf(preselectedTab) }

    // Customer login fields (Initially empty - strictly requiring user input!)
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerVillage by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerAttempted by remember { mutableStateOf(false) }

    // Rider login fields (Initially empty)
    var riderName by remember { mutableStateOf("") }
    var riderPhone by remember { mutableStateOf("") }
    var riderVehicle by remember { mutableStateOf("") }
    var riderPin by remember { mutableStateOf("") }
    var riderAttempted by remember { mutableStateOf(false) }

    // Admin login fields (Initially empty)
    var adminEmailInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var adminAttempted by remember { mutableStateOf(false) }

    // Animation & Feedback States
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shakeTrigger by remember { mutableStateOf(false) }

    // Floating & Pulsing Hero animation
    val infiniteTransition = rememberInfiniteTransition(label = "login_hero")
    val heroScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_alpha"
    )
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    // Shake animation for warning when fields are blank
    val shakeOffset by animateFloatAsState(
        targetValue = if (shakeTrigger) 10f else 0f,
        animationSpec = tween(120),
        finishedListener = { if (shakeTrigger) shakeTrigger = false },
        label = "shake_offset"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MilkifyWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. HERO ANIMATION & BRANDING ---
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .size(92.dp),
            contentAlignment = Alignment.Center
        ) {
            // Concentric Animated Ripple Ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(heroScale)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        MilkifyBlack.copy(alpha = ringAlpha),
                        CircleShape
                    )
            )

            // Outer subtle halo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MilkifyLightGrey.copy(alpha = ringAlpha * 0.8f))
            )

            // Main Official Logo Badge with Floating Motion
            Image(
                painter = painterResource(id = R.drawable.ic_milkify_logo),
                contentDescription = "Milkify Official Logo",
                modifier = Modifier
                    .offset { IntOffset(0, floatOffset.dp.roundToPx()) }
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, Color(0xFF81D4FA), RoundedCornerShape(16.dp))
            )
        }

        Text(
            text = "MILKIFY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = MilkifyBlack
        )

        Text(
            text = "Pure Farm Fresh Dairy • Joura Hub (Morena)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MilkifyTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. THREE PANELS RIGHT AT THE START (शुरू में 3 पैनल्स) ---
        Text(
            text = "SELECT YOUR ACCESS PANEL / पैनल चुनें:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MilkifyTextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoleSelectionCard(
                title = "Customer",
                subtitle = "ग्राहक",
                icon = Icons.Default.Person,
                isSelected = selectedRoleIndex == 0,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleIndex = 0
                    viewModel.setLoginTab(0)
                    errorMessage = null
                }
            )

            RoleSelectionCard(
                title = "Rider",
                subtitle = "डिलीवरी",
                icon = Icons.Default.TwoWheeler,
                isSelected = selectedRoleIndex == 1,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleIndex = 1
                    viewModel.setLoginTab(1)
                    errorMessage = null
                }
            )

            RoleSelectionCard(
                title = "Admin",
                subtitle = "एडमिन",
                icon = Icons.Default.Shield,
                isSelected = selectedRoleIndex == 2,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleIndex = 2
                    viewModel.setLoginTab(2)
                    errorMessage = null
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 3. "BINA INFORMATION NO ENTRY" MANDATORY POLICY BANNER ---
        Surface(
            color = MilkifyOffWhite,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MilkifyBorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(shakeOffset.dp.roundToPx(), 0) }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MilkifyBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "🔒 बिना जानकारी के प्रवेश वर्जित है (No Entry Without Details)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MilkifyBlack
                    )
                    Text(
                        text = "First fill all mandatory information, then click login to enter.",
                        fontSize = 10.sp,
                        color = MilkifyTextSecondary
                    )
                }
            }
        }

        // Error Banner if validation fails
        AnimatedVisibility(visible = errorMessage != null) {
            Surface(
                color = Color(0xFFFFF0F0),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 4. ANIMATED FORM CONTENT (CUSTOMER / RIDER / ADMIN) ---
        AnimatedContent(
            targetState = selectedRoleIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "role_form_animation"
        ) { targetRole ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.5.dp, MilkifyBlack)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    when (targetRole) {
                        // ----------------- CUSTOMER PANEL -----------------
                        0 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Customer Details / ग्राहक विवरण",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyBlack
                                    )
                                    Text(
                                        text = "Fill all 4 fields to log in (No OTP needed)",
                                        fontSize = 11.sp,
                                        color = MilkifyTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Full Name
                            val nameError = customerAttempted && customerName.trim().isBlank()
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = {
                                    customerName = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("1. Full Name / पूरा नाम *") },
                                placeholder = { Text("अपना पूरा नाम दर्ज करें (Enter full name)") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MilkifyBlack) },
                                isError = nameError,
                                supportingText = if (nameError) {
                                    { Text("कृपया पूरा नाम भरें (Name is required)", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_customer_name"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 2. Mobile Number
                            val phoneError = customerAttempted && (customerPhone.trim().length < 10)
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = {
                                    if (it.length <= 13) customerPhone = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("2. Mobile Number / मोबाइल नंबर *") },
                                placeholder = { Text("10 अंकों का मोबाइल नंबर (10-digit number)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MilkifyBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = phoneError,
                                supportingText = if (phoneError) {
                                    { Text("10 अंकों का मान्य मोबाइल नंबर भरें (10-digit phone required)", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_customer_phone"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. Village / Locality
                            val villageError = customerAttempted && customerVillage.trim().isBlank()
                            OutlinedTextField(
                                value = customerVillage,
                                onValueChange = {
                                    customerVillage = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("3. Village / गाँव / मोहल्ला *") },
                                placeholder = { Text("गाँव या मोहल्ला दर्ज करें (e.g. Joura / Kailaras)") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MilkifyBlack) },
                                isError = villageError,
                                supportingText = if (villageError) {
                                    { Text("गाँव या मोहल्ला दर्ज करना आवश्यक है", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_customer_village"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 4. Gmail ID
                            val emailError = customerAttempted && (!customerEmail.contains("@") || customerEmail.trim().length < 5)
                            OutlinedTextField(
                                value = customerEmail,
                                onValueChange = {
                                    customerEmail = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("4. Gmail ID / ईमेल आईडी *") },
                                placeholder = { Text("ईमेल आईडी दर्ज करें (e.g. name@gmail.com)") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MilkifyBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = emailError,
                                supportingText = if (emailError) {
                                    { Text("मान्य Gmail ID दर्ज करें (Valid email required)", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_customer_email"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Login Button
                            AnimatedLoginButton(
                                text = "LOGIN TO CUSTOMER PANEL / प्रवेश करें",
                                isLoading = isVerifying,
                                onClick = {
                                    customerAttempted = true
                                    val isInvalid = customerName.trim().isBlank() ||
                                            customerPhone.trim().length < 10 ||
                                            customerVillage.trim().isBlank() ||
                                            !customerEmail.contains("@")

                                    if (isInvalid) {
                                        errorMessage = "⚠️ बिना जानकारी के प्रवेश वर्जित है! कृपया सभी 4 विवरण भरें।"
                                        shakeTrigger = true
                                        return@AnimatedLoginButton
                                    }

                                    isVerifying = true
                                    coroutineScope.launch {
                                        delay(450)
                                        isVerifying = false
                                        viewModel.loginAsCustomer(
                                            phone = customerPhone,
                                            name = customerName,
                                            village = customerVillage,
                                            email = customerEmail
                                        )
                                    }
                                },
                                testTag = "login_customer_submit"
                            )
                        }

                        // ----------------- RIDER PANEL -----------------
                        1 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Rider Details / डिलीवरी राइडर",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MilkifyBlack
                                    )
                                    Text(
                                        text = "Fill rider details & enter hub PIN to start duty",
                                        fontSize = 11.sp,
                                        color = MilkifyTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Rider Name
                            val rNameError = riderAttempted && riderName.trim().isBlank()
                            OutlinedTextField(
                                value = riderName,
                                onValueChange = {
                                    riderName = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Rider Full Name / राइडर का नाम *") },
                                placeholder = { Text("राइडर का पूरा नाम दर्ज करें") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MilkifyBlack) },
                                isError = rNameError,
                                supportingText = if (rNameError) {
                                    { Text("राइडर का नाम दर्ज करें", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rider Phone
                            val rPhoneError = riderAttempted && (riderPhone.trim().length < 10)
                            OutlinedTextField(
                                value = riderPhone,
                                onValueChange = {
                                    if (it.length <= 13) riderPhone = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Rider Mobile / मोबाइल नंबर *") },
                                placeholder = { Text("10 अंकों का मोबाइल नंबर") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MilkifyBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = rPhoneError,
                                supportingText = if (rPhoneError) {
                                    { Text("10 अंकों का मोबाइल नंबर आवश्यक है", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Vehicle Number
                            val rVehError = riderAttempted && riderVehicle.trim().isBlank()
                            OutlinedTextField(
                                value = riderVehicle,
                                onValueChange = {
                                    riderVehicle = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Vehicle No. / बाइक या स्कूटी नंबर *") },
                                placeholder = { Text("गाड़ी या बाइक नंबर (e.g. MP06-XX-0000)") },
                                leadingIcon = { Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = MilkifyBlack) },
                                isError = rVehError,
                                supportingText = if (rVehError) {
                                    { Text("गाड़ी नंबर दर्ज करें", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rider PIN (Secret 0001, provided privately by Admin)
                            val rPinError = riderAttempted && (riderPin.trim() != "0001")
                            OutlinedTextField(
                                value = riderPin,
                                onValueChange = {
                                    riderPin = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Delivery Hub PIN / हब पिन *") },
                                placeholder = { Text("Enter secret PIN provided by Admin") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MilkifyBlack) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                isError = rPinError,
                                supportingText = if (rPinError) {
                                    { Text("अमान्य हब पिन! कृपया एडमिन द्वारा दिया गया सही PIN दर्ज करें", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_rider_pin"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            AnimatedLoginButton(
                                text = "LOGIN AS RIDER / ड्यूटी शुरू करें",
                                isLoading = isVerifying,
                                onClick = {
                                    riderAttempted = true
                                    val isInvalid = riderName.trim().isBlank() ||
                                            riderPhone.trim().length < 10 ||
                                            riderVehicle.trim().isBlank() ||
                                            riderPin.trim() != "0001"

                                    if (isInvalid) {
                                        errorMessage = "⚠️ बिना पूरी जानकारी के राइडर ड्यूटी शुरू नहीं हो सकती!"
                                        shakeTrigger = true
                                        return@AnimatedLoginButton
                                    }

                                    isVerifying = true
                                    coroutineScope.launch {
                                        delay(450)
                                        isVerifying = false
                                        viewModel.loginAsRiderWithValidation(
                                            name = riderName,
                                            phone = riderPhone,
                                            vehicleNumber = riderVehicle,
                                            pin = riderPin
                                        )
                                    }
                                },
                                testTag = "login_rider_submit"
                            )
                        }

                        // ----------------- ADMIN PANEL -----------------
                        2 -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Admin Panel / एडमिन लॉगिन",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlack
                                )
                                Text(
                                    text = "Enter master email & password to manage hub",
                                    fontSize = 11.sp,
                                    color = MilkifyTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Admin Email
                            val admEmailError = adminAttempted && adminEmailInput.trim().isBlank()
                            OutlinedTextField(
                                value = adminEmailInput,
                                onValueChange = {
                                    adminEmailInput = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Admin Email ID *") },
                                placeholder = { Text("Enter your registered admin email") },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MilkifyBlack) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = admEmailError,
                                supportingText = if (admEmailError) {
                                    { Text("Admin Email आवश्यक है", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_admin_email"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Admin Password
                            val admPassError = adminAttempted && adminPasswordInput.trim().isBlank()
                            OutlinedTextField(
                                value = adminPasswordInput,
                                onValueChange = {
                                    adminPasswordInput = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                label = { Text("Admin Password *") },
                                placeholder = { Text("Enter admin password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MilkifyBlack) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = admPassError,
                                supportingText = if (admPassError) {
                                    { Text("Admin Password आवश्यक है", color = Color(0xFFD32F2F)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_admin_password"),
                                colors = outlinedColors()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = MilkifyLightGrey,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = MilkifyBlack, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🔒 Secure Authorized Access • Only registered hub admin can unlock",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MilkifyBlack
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            AnimatedLoginButton(
                                text = "UNLOCK ADMIN PANEL / एडमिन पैनल खोलें",
                                isLoading = isVerifying,
                                onClick = {
                                    adminAttempted = true
                                    if (adminEmailInput.isBlank() || adminPasswordInput.isBlank()) {
                                        errorMessage = "⚠️ बिना Email और Password के एडमिन पैनल नहीं खुल सकता!"
                                        shakeTrigger = true
                                        return@AnimatedLoginButton
                                    }

                                    isVerifying = true
                                    coroutineScope.launch {
                                        delay(450)
                                        isVerifying = false
                                        val success = viewModel.loginAsAdminWithValidation(adminEmailInput, adminPasswordInput)
                                        if (!success) {
                                            errorMessage = "❌ गलत क्रेडेंशियल्स! कृपया सही Email और Password दर्ज करें।"
                                            shakeTrigger = true
                                        }
                                    }
                                },
                                testTag = "login_admin_submit"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// --- 5. REUSABLE ROLE SELECTION CARD ---
@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MilkifyBlack else MilkifyWhite,
        animationSpec = tween(220),
        label = "role_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MilkifyWhite else MilkifyBlack,
        animationSpec = tween(220),
        label = "role_content"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 0.98f,
        animationSpec = tween(180),
        label = "role_scale"
    )

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, MilkifyBlack),
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isSelected) Color(0xFFDDDDDD) else MilkifyTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- 6. ANIMATED LOGIN BUTTON ---
@Composable
private fun AnimatedLoginButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MilkifyBlack,
            contentColor = MilkifyWhite,
            disabledContainerColor = MilkifyBlack.copy(alpha = 0.8f),
            disabledContentColor = MilkifyWhite
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag)
    ) {
        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    color = MilkifyWhite,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "VERIFYING DETAILS...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MilkifyWhite
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MilkifyWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MilkifyWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MilkifyBlack,
    unfocusedBorderColor = MilkifyBorderDark,
    focusedLabelColor = MilkifyBlack,
    errorBorderColor = Color(0xFFD32F2F),
    errorLabelColor = Color(0xFFD32F2F)
)
