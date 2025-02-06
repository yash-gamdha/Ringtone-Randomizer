package com.app.ringtonerandomizer.presentation.home_screen.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents
import kotlinx.coroutines.CoroutineScope

@Composable
fun RingtoneList(
    ringtones: List<String>,
    currentRingtone: String,
    state: LazyListState,
    context: Context,
    scope: CoroutineScope,
    isPlaying: Int,
    onDropDownClick: (ClickEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        state = state
    ) {
        items(ringtones.size) { index ->
            RingtoneRow(
                ringtone = ringtones[index],
                currentRingtone = currentRingtone,
                index = index,
                context = context,
                onClick = onDropDownClick,
                scope = scope,
                isPlaying = isPlaying
            )
        }
    }
}