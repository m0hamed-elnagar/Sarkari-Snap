package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.FrequencyMode

@Composable
fun FrequencyCard(
    mode: FrequencyMode,
    isActive: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (label, desc, icon) = when (mode) {
        FrequencyMode.BREAKING -> Triple(
            stringResource(R.string.breaking_mode),
            stringResource(R.string.get_every_important_update_instantly),
            Icons.Outlined.NotificationsActive
        )

        FrequencyMode.STANDARD -> Triple(
            stringResource(R.string.standard_mode),
            stringResource(R.string.only_top_headlines_no_overload),
            Icons.Outlined.Schedule
        )

        FrequencyMode.Custom -> Triple(
            stringResource(R.string.calm_mode),
            stringResource(R.string.customize_your_preferences),
            Icons.Outlined.Bedtime
        )
    }

    val activeColor = Color(0xFF0A84FF)
    val inactiveColor = Color(0xFF2C2C2E)
    val textColor = Color(0xFF8392B4)
    val iconBGColor = Color(0xFFE7EBF4)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            contentColor = if (isActive) activeColor else Black,
            containerColor = White
        ),
        border = if (isActive) BorderStroke(1.dp, activeColor) else BorderStroke(
            1.dp,
            Color.Gray.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBGColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Black
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 13.sp,
                    color = textColor
                )
            }
        }
    }
}
