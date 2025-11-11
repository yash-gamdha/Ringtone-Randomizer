package com.app.ringtonerandomizer.presentation.home_screen.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents
import com.app.ringtonerandomizer.presentation.home_screen.RingtoneListViewModel

@Composable
fun RingtoneList(
    ringtones: List<String>,
    currentRingtone: String,
    state: LazyListState,
    context: Context,
    ringtoneListViewModel: RingtoneListViewModel,
    onDropDownClick: (ClickEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        items(
            ringtones.size,
            key = { it }
        ) { index ->
            RingtoneRow(
                ringtone = ringtones[index],
                currentRingtone = currentRingtone,
                index = index,
                context = context,
                onClick = onDropDownClick,
                ringtoneListViewModel = ringtoneListViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}