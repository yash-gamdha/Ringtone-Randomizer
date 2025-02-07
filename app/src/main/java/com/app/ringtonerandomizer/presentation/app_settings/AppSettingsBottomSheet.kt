package com.app.ringtonerandomizer.presentation.app_settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ringtonerandomizer.core.app_settings.AppSettings
import com.app.ringtonerandomizer.core.app_settings.dataStore
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsBottomSheet(
    sheetState: SheetState,
    context: Context,
    onClick: (ClickEvents) -> Unit,
    onDismiss: () -> Unit
) {
    val appSettings by context.dataStore.data.collectAsStateWithLifecycle(
        initialValue = AppSettings()
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() },
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle()
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                headlineContent = {
                    Text(
                        text = "Sequential rotation",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = {
                    Text(text = "Change ringtone sequentially or randomly after incoming call")
                },
                trailingContent = {
                    Switch(
                        checked = appSettings.isSequentialRotationOn,
                        onCheckedChange = { value ->
                            onClick(ClickEvents.UpdateSequentialRotationSetting(context, value))
                        }
                    )
                }
            )
        }
        Spacer(Modifier.height(48.dp))
    }
}