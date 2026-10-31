package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyCreamCard
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGold
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyPrimaryGreen
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.PhonePeOrderSuccessData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhonePeOrderSuccessDialog(
    data: PhonePeOrderSuccessData,
    onDismiss: () -> Unit,
    onTrackOrder: (Long) -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // Spring bouncy scale for main checkmark
    val checkmarkScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmarkScale"
    )

    // Pulsing radar ripple wave for PhonePe animation
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )

    val dateFormatted = remember(data.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(data.timestamp))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = animationStarted,
                enter = scaleIn(tween(350)) + fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 4 })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(vertical = 24.dp)
                        .testTag("phonepe_order_confirmation_dialog"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // PhonePe Iconic Celebration Header Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF5F259F), // PhonePe Purple
                                            Color(0xFF4A148C),
                                            Color(0xFF311B92)
                                        )
                                    )
                                )
                                .padding(top = 28.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Animated Radar Pulse & Checkmark
                                Box(
                                    modifier = Modifier.size(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Outer Radar Ring 1
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .scale(rippleScale)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E676).copy(alpha = rippleAlpha))
                                    )

                                    // Outer Radar Ring 2
                                    Box(
                                        modifier = Modifier
                                            .size(105.dp)
                                            .scale(rippleScale * 0.9f)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = rippleAlpha * 0.5f))
                                    )

                                    // Sparkle Particle Canvas
                                    Canvas(modifier = Modifier.size(100.dp)) {
                                        val radius = size.minDimension / 2
                                        val angles = listOf(30f, 75f, 120f, 165f, 210f, 255f, 300f, 345f)
                                        angles.forEachIndexed { i, deg ->
                                            val rad = Math.toRadians(deg.toDouble())
                                            val dist = radius * (0.85f + (i % 3) * 0.15f)
                                            val x = center.x + (dist * Math.cos(rad)).toFloat()
                                            val y = center.y + (dist * Math.sin(rad)).toFloat()
                                            val pColor = if (i % 2 == 0) Color(0xFFFFD700) else Color(0xFF69F0AE)
                                            drawCircle(color = pColor, radius = if (i % 2 == 0) 4f else 6f, center = Offset(x, y))
                                        }
                                    }

                                    // Primary Green Check Badge with Bouncy Scale
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .scale(checkmarkScale)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00C853))
                                            .shadow(8.dp, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Success",
                                            tint = MilkifyWhite,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Order Placed Successfully!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MilkifyWhite,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "ऑर्डर सफलतापूर्वक दर्ज हो गया!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFB39DDB),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // PhonePe Audio Soundbox Alert pill
                                Surface(
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PhonePe Audio Tone: ₹${data.totalAmount.toInt()} COD Order Verified",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MilkifyWhite
                                        )
                                    }
                                }
                            }
                        }

                        // Order Summary & Amount Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Big Total Amount Banner
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "₹",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlack
                                )
                                Text(
                                    text = "${data.totalAmount.toInt()}",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MilkifyBlack
                                )
                            }

                            Text(
                                text = "Cash on Delivery (Pay to Rider on Arrival)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MilkifyDarkGreen
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Joura Hub Security Verification OTP Box
                            Surface(
                                color = MilkifyCreamCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MilkifyGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "DELIVERY HANDOVER OTP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MilkifyBlack
                                        )
                                        Text(
                                            text = "Share this 4-digit code with Joura rider",
                                            fontSize = 10.sp,
                                            color = MilkifyTextMuted
                                        )
                                    }
                                    Surface(
                                        color = MilkifyBlack,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = data.otpCode,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MilkifyWhite,
                                            letterSpacing = 3.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Order Breakdown Details Table
                            Surface(
                                color = MilkifyLightGrey,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Order Number Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Order Reference", fontSize = 11.sp, color = MilkifyTextMuted)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                data.orderNumber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MilkifyBlack
                                            )
                                            IconButton(
                                                onClick = { clipboardManager.setText(AnnotatedString(data.orderNumber)) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.ContentCopy,
                                                    contentDescription = "Copy Order ID",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = MilkifyTextSecondary
                                                )
                                            }
                                        }
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = MilkifyBorder)

                                    // Delivery Slot Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp), tint = MilkifyPrimaryGreen)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delivery Slot", fontSize = 11.sp, color = MilkifyTextMuted)
                                        }
                                        Text(
                                            text = data.deliverySlot,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MilkifyBlack
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = MilkifyBorder)

                                    // Destination Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = MilkifyPrimaryGreen)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delivering To", fontSize = 11.sp, color = MilkifyTextMuted)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${data.customerName}\n${data.deliveryAddress}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = MilkifyBlack,
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = MilkifyBorder)

                                    // Items Summary Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("Items", fontSize = 11.sp, color = MilkifyTextMuted)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = data.itemsSummary,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = MilkifyBlack,
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = MilkifyBorder)

                                    // Date Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Date & Time", fontSize = 11.sp, color = MilkifyTextMuted)
                                        Text(dateFormatted, fontSize = 11.sp, color = MilkifyTextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Trust & Support Call Strip
                            Surface(
                                color = MilkifyWhite,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = MilkifyPrimaryGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Joura Hub Care: 9343688757",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MilkifyBlack
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919343688757"))
                                            context.startActivity(intent)
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons
                            Button(
                                onClick = { onTrackOrder(data.orderId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5F259F), // PhonePe Purple
                                    contentColor = MilkifyWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("track_order_button")
                            ) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TRACK LIVE DELIVERY / लाइव ट्रैक करें",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MilkifyBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("dismiss_confirmation_button")
                            ) {
                                Text(
                                    text = "Done / वापस होम पर जाएं",
                                    color = MilkifyBlack,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
