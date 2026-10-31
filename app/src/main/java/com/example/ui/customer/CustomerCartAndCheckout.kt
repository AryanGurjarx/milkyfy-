package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyCream
import com.example.ui.theme.MilkifyCreamCard
import com.example.ui.theme.MilkifyDarkGreen
import com.example.ui.theme.MilkifyGold
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyOnGreenContainer
import com.example.ui.theme.MilkifyPrimaryGreen
import com.example.ui.theme.MilkifySurface
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.MilkifyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutModalSheet(
    viewModel: MilkifyViewModel,
    onDismiss: () -> Unit,
    onAddNewAddress: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val selectedSlot by viewModel.selectedDeliverySlot.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val savedAddresses by viewModel.customerAddresses.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    var customerNotes by remember { mutableStateOf("") }
    var showAddressSelector by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val itemsList = cartItems.values.toList()
    val subtotal = itemsList.sumOf { it.first.price * it.second }
    // Free Cash on Delivery for all customer orders
    val deliveryCharge = 0.0
    val discount = if (appliedCoupon != null) {
        if (appliedCoupon!!.discountType == "PERCENTAGE") {
            (subtotal * (appliedCoupon!!.discountValue / 100.0)).coerceAtMost(appliedCoupon!!.maxDiscount)
        } else {
            appliedCoupon!!.discountValue
        }
    } else 0.0
    val grandTotal = (subtotal + deliveryCharge - discount).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MilkifySurface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Cart & Checkout",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MilkifyTextPrimary
                        )
                        Text(
                            text = "${itemsList.sumOf { it.second }} bottle(s) selected",
                            fontSize = 12.sp,
                            color = MilkifyTextMuted
                        )
                    }

                    if (itemsList.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (itemsList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your milk cart is empty",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MilkifyTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add fresh morning cow milk from the home screen",
                            fontSize = 13.sp,
                            color = MilkifyTextMuted
                        )
                    }
                }
            } else {
                // Cart Items List
                items(itemsList) { entry ->
                    val product = entry.first
                    val qty = entry.second
                    Surface(
                        color = MilkifyCreamCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MilkifyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "${product.volumeLabel} • ₹${product.price.toInt()} each",
                                    fontSize = 12.sp,
                                    color = MilkifyTextSecondary
                                )
                                Text(
                                    text = "Item Total: ₹${(product.price * qty).toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MilkifyPrimaryGreen
                                )
                            }

                            // Quantity controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, MilkifyBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.decrementCart(product.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = MilkifyPrimaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = qty.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.addToCart(product) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = MilkifyPrimaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Delivery Slot Selector
                item {
                    Text(
                        text = "Preferred Delivery Slot",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MilkifyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val morningSlot = "Morning (3:00 AM – 11:00 AM)"
                        val eveningSlot = "Evening (3:00 PM – 8:00 PM)"

                        SlotSelectionCard(
                            title = "Morning Slot",
                            timing = "3:00 AM – 11:00 AM",
                            isSelected = selectedSlot.startsWith("Morning"),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setDeliverySlot(morningSlot) }
                        )

                        SlotSelectionCard(
                            title = "Evening Slot",
                            timing = "3:00 PM – 8:00 PM",
                            isSelected = selectedSlot.startsWith("Evening"),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setDeliverySlot(eveningSlot) }
                        )
                    }
                }

                // Delivery Address
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Delivery Address",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MilkifyTextPrimary
                        )
                        TextButton(onClick = { showAddressSelector = true }) {
                            Text("Change", color = MilkifyPrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        color = MilkifyCreamCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MilkifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MilkifyPrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedAddress,
                                fontSize = 12.sp,
                                color = MilkifyTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Coupon Code Section
                item {
                    Text(
                        text = "Apply Coupon Code",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MilkifyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (appliedCoupon != null) {
                        Surface(
                            color = MilkifyGreenContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MilkifyPrimaryGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "'${appliedCoupon!!.code}' APPLIED",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MilkifyDarkGreen
                                        )
                                        Text(
                                            text = "You saved ₹${discount.toInt()}",
                                            fontSize = 11.sp,
                                            color = MilkifyPrimaryGreen
                                        )
                                    }
                                }

                                TextButton(onClick = { viewModel.removeCoupon() }) {
                                    Text("Remove", color = Color.Red, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                placeholder = { Text("Try 'FRESH20' for 20% off", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (couponInput.isNotBlank()) {
                                        viewModel.applyCoupon(couponInput)
                                        couponInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("APPLY")
                            }
                        }
                    }
                }

                // Customer Delivery Notes
                item {
                    OutlinedTextField(
                        value = customerNotes,
                        onValueChange = { customerNotes = it },
                        label = { Text("Delivery instructions (optional)") },
                        placeholder = { Text("e.g., Leave in milk bag at door, ring bell", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                // Bill Summary
                item {
                    Surface(
                        color = MilkifyCreamCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MilkifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Bill Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MilkifyTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            BillRow(title = "Item Subtotal", amount = "₹${subtotal.toInt()}")
                            BillRow(
                                title = "Delivery Charge",
                                amount = if (deliveryCharge == 0.0) "FREE" else "₹${deliveryCharge.toInt()}",
                                isHighlight = deliveryCharge == 0.0
                            )
                            if (discount > 0) {
                                BillRow(title = "Coupon Discount", amount = "-₹${discount.toInt()}", isDiscount = true)
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MilkifyBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "To Pay (COD)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "₹${grandTotal.toInt()}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MilkifyPrimaryGreen
                                )
                            }
                        }
                    }
                }

                // Payment Method Notice (100% Offline Cash on Delivery)
                item {
                    Surface(
                        color = MilkifyLightGrey,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MilkifyBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = MilkifyBlack,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Payment Mode: Offline Cash on Delivery (COD)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MilkifyBlack
                                )
                                Text(
                                    text = "Pay cash in hand directly to delivery partner upon milk handover. No online gateway or wallet recharge needed. Customer hisaab is maintained at Joura hub.",
                                    fontSize = 11.sp,
                                    color = MilkifyTextSecondary
                                )
                            }
                        }
                    }
                }

                // Place Order Action Button
                item {
                    Button(
                        onClick = {
                            viewModel.checkoutOrder(notes = customerNotes) {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MilkifyBlack,
                            contentColor = MilkifyWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_order_button")
                    ) {
                        Text(
                            text = "CONFIRM ORDER • ₹${grandTotal.toInt()} (CASH ON DELIVERY)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Address Switcher Dialog
    if (showAddressSelector) {
        AlertDialog(
            onDismissRequest = { showAddressSelector = false },
            title = { Text("Select Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    savedAddresses.forEach { addr ->
                        val text = "${addr.houseNumber}, ${addr.street}, ${addr.area}, Joura"
                        Surface(
                            color = if (selectedAddress.contains(addr.street)) MilkifyGreenContainer else MilkifyCreamCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDeliveryAddress(text)
                                    showAddressSelector = false
                                }
                                .padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text, fontSize = 12.sp, color = MilkifyTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            showAddressSelector = false
                            onAddNewAddress()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New Address")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddressSelector = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SlotSelectionCard(
    title: String,
    timing: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) MilkifyGreenContainer else MilkifyCreamCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) MilkifyPrimaryGreen else MilkifyBorder
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) MilkifyDarkGreen else MilkifyTextPrimary
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MilkifyPrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timing,
                fontSize = 11.sp,
                color = if (isSelected) MilkifyDarkGreen else MilkifyTextSecondary
            )
        }
    }
}

@Composable
fun BillRow(
    title: String,
    amount: String,
    isHighlight: Boolean = false,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 13.sp, color = MilkifyTextSecondary)
        Text(
            text = amount,
            fontSize = 13.sp,
            fontWeight = if (isHighlight || isDiscount) FontWeight.Bold else FontWeight.Medium,
            color = if (isDiscount) MilkifyGold else if (isHighlight) MilkifyPrimaryGreen else MilkifyTextPrimary
        )
    }
}

@Composable
fun AddNewAddressDialog(
    onDismiss: () -> Unit,
    onSave: (label: String, house: String, street: String, area: String, landmark: String) -> Unit
) {
    var label by remember { mutableStateOf("Home") }
    var house by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("Civil Lines") }
    var landmark by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Delivery Address in Joura", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Address Label (e.g. Home, Office)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = house,
                    onValueChange = { house = it },
                    label = { Text("House / Flat Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street / Road Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area / Colony (Joura)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark (e.g. Near Gandhi Chowk)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "City: Joura, Morena (MP) - Pincode: 476221",
                    fontSize = 11.sp,
                    color = MilkifyTextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (house.isNotBlank() && street.isNotBlank()) {
                        onSave(label, house, street, area, landmark)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyPrimaryGreen)
            ) {
                Text("Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
