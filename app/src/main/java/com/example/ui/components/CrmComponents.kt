package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AccentTealContainer
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusActiveGreenBg
import com.example.ui.theme.StatusInactiveSlate
import com.example.ui.theme.StatusInactiveSlateBg
import com.example.ui.theme.StatusLeadBlue
import com.example.ui.theme.StatusLeadBlueBg
import com.example.ui.theme.StatusNegotiatingAmber
import com.example.ui.theme.StatusNegotiatingAmberBg
import com.example.ui.theme.StatusRiskRose
import com.example.ui.theme.StatusRiskRoseBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase(Locale.ROOT)) {
        "active" -> StatusActiveGreenBg to StatusActiveGreen
        "lead" -> StatusLeadBlueBg to StatusLeadBlue
        "negotiating" -> StatusNegotiatingAmberBg to StatusNegotiatingAmber
        "churn risk", "risk" -> StatusRiskRoseBg to StatusRiskRose
        else -> StatusInactiveSlateBg to StatusInactiveSlate
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = status,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SegmentTagChip(
    tag: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isTealActive = tag.equals("Enterprise", ignoreCase = true) ||
            tag.equals("VIP", ignoreCase = true) ||
            tag.equals("Active", ignoreCase = true) ||
            tag.equals("Renewal Q4", ignoreCase = true)

    val bgColor = if (isTealActive) AccentTealContainer else Color(0xFFF1F5F9)
    val textColor = if (isTealActive) AccentTeal else Slate700

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "#$tag",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = IndigoPrimary,
    trendText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (trendText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StatusActiveGreenBg)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = StatusActiveGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = trendText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActiveGreen
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
fun CustomerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 40
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "C" }

    // Derive deterministic pastel hue
    val hueHash = kotlin.math.abs(name.hashCode()) % 5
    val (bg, fg) = when (hueHash) {
        0 -> Color(0xFFEEF2FF) to IndigoPrimary
        1 -> Color(0xFFCCFBF1) to AccentTeal
        2 -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        3 -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        else -> Color(0xFFFCE7F3) to Color(0xFFBE185D)
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = fg,
            fontSize = (sizeDp * 0.38f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InteractionIcon(
    type: String,
    modifier: Modifier = Modifier,
    size: Int = 32
) {
    val (icon, tint, bg) = when (type.uppercase(Locale.ROOT)) {
        "CALL" -> Triple(Icons.Default.Call, Color(0xFF2563EB), Color(0xFFDBEAFE))
        "MEETING" -> Triple(Icons.Default.Event, IndigoPrimary, Color(0xFFEEF2FF))
        "EMAIL" -> Triple(Icons.Default.Email, AccentTeal, Color(0xFFCCFBF1))
        else -> Triple(Icons.Default.Note, Color(0xFFD97706), Color(0xFFFEF3C7))
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = type,
            tint = tint,
            modifier = Modifier.size((size * 0.55f).dp)
        )
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneHour = 3600000L
    val oneDay = 86400000L

    return when {
        diff < 60000L -> "Just now"
        diff < oneHour -> "${diff / 60000L}m ago"
        diff < oneDay -> "${diff / oneHour}h ago"
        diff < oneDay * 2 -> "Yesterday"
        diff < oneDay * 7 -> "${diff / oneDay}d ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

fun formatFullDate(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(timestamp))
}

fun formatDateOnly(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
}
