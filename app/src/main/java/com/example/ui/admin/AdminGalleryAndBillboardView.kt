package com.example.ui.admin

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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entities.BillboardBannerEntity
import com.example.data.local.entities.GalleryPhotoEntity
import com.example.data.local.entities.ProductEntity
import com.example.ui.theme.MilkifyAmber
import com.example.ui.theme.MilkifyBlack
import com.example.ui.theme.MilkifyBlue
import com.example.ui.theme.MilkifyBlueContainer
import com.example.ui.theme.MilkifyBorder
import com.example.ui.theme.MilkifyGreen
import com.example.ui.theme.MilkifyGreenContainer
import com.example.ui.theme.MilkifyLightGrey
import com.example.ui.theme.MilkifyRed
import com.example.ui.theme.MilkifyRedLight
import com.example.ui.theme.MilkifySurface
import com.example.ui.theme.MilkifyTextMuted
import com.example.ui.theme.MilkifyTextPrimary
import com.example.ui.theme.MilkifyTextSecondary
import com.example.ui.theme.MilkifyWhite
import com.example.ui.viewmodel.MilkifyViewModel

@Composable
fun AdminGalleryAndBillboardView(viewModel: MilkifyViewModel) {
    val galleryPhotos by viewModel.galleryPhotos.collectAsState()
    val billboardBanners by viewModel.billboardBanners.collectAsState()
    val products by viewModel.products.collectAsState()

    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var bannerToEdit by remember { mutableStateOf<BillboardBannerEntity?>(null) }
    var productToEditImage by remember { mutableStateOf<ProductEntity?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Header & Quick Actions
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dairy Photos & Billboard Manager",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MilkifyTextPrimary
                            )
                            Text(
                                text = "Manage 5 Billboard carousel images, customer dairy photo gallery & broadcast alerts.",
                                fontSize = 12.sp,
                                color = MilkifyTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showBroadcastDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddPhotoDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MilkifyRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Gallery Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Billboard Banners (At least 5 sliding images)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MilkifyRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ViewCarousel, contentDescription = null, tint = MilkifyRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Billboard Banners (${billboardBanners.size} Active Slides)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "Appears at the top of customer home screen",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        billboardBanners.forEachIndexed { index, banner ->
                            AdminBillboardBannerItem(
                                banner = banner,
                                slideNumber = index + 1,
                                onEdit = { bannerToEdit = banner }
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Product Image Customizer (Buffalo, Cow, Ghee)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MilkifyGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = MilkifyGreen, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Product Images (Buffalo, Cow, Ghee)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "Change high quality images for each dairy product",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        products.forEach { prod ->
                            AdminProductImageItem(
                                product = prod,
                                onEditImage = { productToEditImage = prod }
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Hamari Dairy Photo Gallery
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MilkifyWhite),
                border = BorderStroke(1.dp, MilkifyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MilkifyBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MilkifyBlue, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Dairy Photo Gallery (${galleryPhotos.size} Photos)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MilkifyTextPrimary
                                )
                                Text(
                                    text = "Visible in Customer App under 'Hamari Dairy & Farm Gallery'",
                                    fontSize = 11.sp,
                                    color = MilkifyTextMuted
                                )
                            }
                        }

                        IconButton(onClick = { showAddPhotoDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Photo", tint = MilkifyRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (galleryPhotos.isEmpty()) {
                        Text(
                            text = "No gallery photos yet. Click '+ Add Gallery Photo' above to showcase your dairy farm.",
                            fontSize = 12.sp,
                            color = MilkifyTextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            galleryPhotos.forEach { photo ->
                                AdminGalleryPhotoItem(
                                    photo = photo,
                                    onDelete = { viewModel.adminDeleteGalleryPhoto(photo) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog: Add Gallery Photo
    if (showAddPhotoDialog) {
        AddGalleryPhotoDialog(
            onDismiss = { showAddPhotoDialog = false },
            onAdd = { title, desc, url, cat ->
                viewModel.adminAddGalleryPhoto(title, desc, url, cat)
                showAddPhotoDialog = false
            }
        )
    }

    // Dialog: Edit Billboard Banner
    if (bannerToEdit != null) {
        EditBillboardBannerDialog(
            banner = bannerToEdit!!,
            onDismiss = { bannerToEdit = null },
            onSave = { title, subtitle, url, badge ->
                viewModel.adminAddOrUpdateBillboardBanner(
                    id = bannerToEdit!!.id,
                    title = title,
                    subtitle = subtitle,
                    imageUrl = url,
                    badge = badge,
                    orderIndex = bannerToEdit!!.orderIndex
                )
                bannerToEdit = null
            }
        )
    }

    // Dialog: Edit Product Image
    if (productToEditImage != null) {
        EditProductImageDialog(
            product = productToEditImage!!,
            onDismiss = { productToEditImage = null },
            onSave = { newUrl ->
                viewModel.adminUpdateProductImage(productToEditImage!!.id, newUrl)
                productToEditImage = null
            }
        )
    }

    // Dialog: Broadcast Notification
    if (showBroadcastDialog) {
        BroadcastNotificationDialog(
            onDismiss = { showBroadcastDialog = false },
            onSend = { title, message, type ->
                viewModel.adminBroadcastNotification(title, message, type)
                showBroadcastDialog = false
            }
        )
    }
}

@Composable
fun AdminBillboardBannerItem(
    banner: BillboardBannerEntity,
    slideNumber: Int,
    onEdit: () -> Unit
) {
    Surface(
        color = MilkifyLightGrey,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MilkifyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp, 40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomEnd = 4.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "#$slideNumber",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MilkifyRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = banner.badge,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = banner.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MilkifyTextPrimary,
                        maxLines = 1
                    )
                }
                Text(
                    text = banner.subtitle,
                    fontSize = 11.sp,
                    color = MilkifyTextSecondary,
                    maxLines = 1
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit banner", tint = MilkifyBlue, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AdminProductImageItem(
    product: ProductEntity,
    onEditImage: () -> Unit
) {
    val fallbackRes = when {
        product.category.contains("Ghee", ignoreCase = true) -> R.drawable.ic_desi_ghee_art
        product.category.contains("Buffalo", ignoreCase = true) -> R.drawable.ic_buffalo_milk_art
        else -> R.drawable.ic_cow_milk_art
    }

    Surface(
        color = MilkifyWhite,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MilkifyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MilkifyLightGrey)
            ) {
                AsyncImage(
                    model = product.imageUrl.ifBlank { null },
                    placeholder = painterResource(id = fallbackRes),
                    error = painterResource(id = fallbackRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${product.name} (${product.volumeLabel})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MilkifyTextPrimary
                )
                Text(
                    text = "Price: ₹${product.price.toInt()} • ${product.category}",
                    fontSize = 11.sp,
                    color = MilkifyTextMuted
                )
            }

            OutlinedButton(
                onClick = onEditImage,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, MilkifyBlue)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = MilkifyBlue)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Change Photo", fontSize = 11.sp, color = MilkifyBlue)
            }
        }
    }
}

@Composable
fun AdminGalleryPhotoItem(
    photo: GalleryPhotoEntity,
    onDelete: () -> Unit
) {
    Surface(
        color = MilkifyLightGrey,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MilkifyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = MilkifyGreenContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = photo.category,
                        color = MilkifyGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = photo.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MilkifyTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = photo.description,
                    fontSize = 11.sp,
                    color = MilkifyTextSecondary,
                    maxLines = 2,
                    lineHeight = 15.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete photo", tint = MilkifyRed)
            }
        }
    }
}

@Composable
fun AddGalleryPhotoDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, desc: String, url: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Farm & Cattle") }

    val categories = listOf("Farm & Cattle", "Milking Process", "Cold Chain", "Ghee Making", "Quality Lab", "Doorstep Delivery")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Photo to Dairy Gallery", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Photo Title (e.g. Healthy Murrah Buffaloes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Dairy Details") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("High Quality Image URL (https://...)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Select Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MilkifyTextPrimary)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MilkifyRed else MilkifyLightGrey,
                            border = BorderStroke(1.dp, if (isSelected) MilkifyRed else MilkifyBorder),
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MilkifyTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && imageUrl.isNotBlank()) {
                        onAdd(title, description, imageUrl, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyRed),
                enabled = title.isNotBlank() && imageUrl.isNotBlank()
            ) {
                Text("Add Photo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditBillboardBannerDialog(
    banner: BillboardBannerEntity,
    onDismiss: () -> Unit,
    onSave: (title: String, subtitle: String, url: String, badge: String) -> Unit
) {
    var title by remember { mutableStateOf(banner.title) }
    var subtitle by remember { mutableStateOf(banner.subtitle) }
    var imageUrl by remember { mutableStateOf(banner.imageUrl) }
    var badge by remember { mutableStateOf(banner.badge) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Billboard Slide #${banner.orderIndex}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Banner Heading") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle / Details") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = badge,
                    onValueChange = { badge = it },
                    label = { Text("Badge Label (e.g. 100% PURE, FAT 7.2%)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (https://...)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, subtitle, imageUrl, badge) },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyRed),
                enabled = title.isNotBlank() && imageUrl.isNotBlank()
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditProductImageDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (newUrl: String) -> Unit
) {
    var imageUrl by remember { mutableStateOf(product.imageUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Update Image: ${product.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Provide high-quality image URL for ${product.name} (${product.volumeLabel}).",
                    fontSize = 12.sp,
                    color = MilkifyTextSecondary
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (https://...)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (imageUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MilkifyLightGrey)
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(imageUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlue),
                enabled = imageUrl.isNotBlank()
            ) {
                Text("Update Image")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BroadcastNotificationDialog(
    onDismiss: () -> Unit,
    onSend: (title: String, message: String, type: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ORDER") }

    val types = listOf("ORDER", "PROMO", "QUALITY")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Broadcast Alert to All Customers", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "This alert will instantly appear in all customer mobile apps with a notification bell badge.",
                    fontSize = 12.sp,
                    color = MilkifyTextSecondary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Alert Title (e.g. 🌅 Morning Dispatch Live)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Body") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { t ->
                        val isSelected = type == t
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MilkifyBlue else MilkifyLightGrey,
                            modifier = Modifier.clickable { type = t }
                        ) {
                            Text(
                                text = t,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MilkifyTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(title, message, type) },
                colors = ButtonDefaults.buttonColors(containerColor = MilkifyBlue),
                enabled = title.isNotBlank() && message.isNotBlank()
            ) {
                Text("Send Broadcast")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
