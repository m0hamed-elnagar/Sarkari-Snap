package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FilterChip
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.FrequencyMode
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.NotificationCategory
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.NotificationsActions

@Composable
fun TopicLevelItem (
    topic : NotificationCategory,
    onClick : (NotificationsActions) -> Unit
){
    val options = listOf("High", "Normal")
    var selected by remember { mutableStateOf(options[1]) }
    var selectedIndex by remember { mutableIntStateOf(1) }
    if (topic.level == FrequencyMode.BREAKING){
        selectedIndex = 0
    }else selectedIndex=1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(vertical = 8.dp)
    ) {
    androidx.compose.material3.OutlinedTextField(topic.category,
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)
            .padding(horizontal = 8.dp),

      onValueChange = {}  )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.weight(1f)
        ) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .wrapContentWidth(),
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index
                              onClick(NotificationsActions
                                  .OnTopicLevelChange(topic.id,
                                      if(selectedIndex==0) FrequencyMode.BREAKING else FrequencyMode.STANDARD))},
                    label = { Text(option,
                         fontSize = 12.sp,
                         fontWeight = FontWeight.Light) },
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }
}
