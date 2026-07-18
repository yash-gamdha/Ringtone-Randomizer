package com.app.ringtonerandomizer.presentation.app_settings

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ringtonerandomizer.core.app_settings.AppSettings
import com.app.ringtonerandomizer.core.app_settings.dataStore
import com.app.ringtonerandomizer.permissions.checkNotificationPermission
import com.app.ringtonerandomizer.presentation.app_settings.components.ListItemForAppSetting
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

    var notificationPermissionBool by rememberSaveable {
        mutableStateOf(checkNotificationPermission(context))
    }

    var showNotificationsBool by rememberSaveable {
        mutableStateOf(notificationPermissionBool && appSettings.showNotifications)
    }

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onClick(ClickEvents.UpdateShowNotificationsSetting(context, true))
                notificationPermissionBool = true
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationPermissionBool = checkNotificationPermission(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(notificationPermissionBool, appSettings.showNotifications) {
        showNotificationsBool = notificationPermissionBool && appSettings.showNotifications
    }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ListItemForAppSetting(
                headingText = "Toggle Service",
                supportingText = "Use the app as only ringtone selector, the app will stop randomizing on per call.",
                trailingContent = {
                    Switch(
                        checked = appSettings.serviceOn,
                        onCheckedChange = { value ->
                            onClick(ClickEvents.UpdateServiceSettings(context, value))
                        }
                    )
                }
            )
            ListItemForAppSetting(
                headingText = "Sequential rotation",
                supportingText = "Change ringtone sequentially or randomly after incoming call",
                trailingContent = {
                    Switch(
                        checked = appSettings.isSequentialRotationOn,
                        enabled = appSettings.serviceOn,
                        onCheckedChange = { value ->
                            onClick(ClickEvents.UpdateSequentialRotationSetting(context, value))
                        }
                    )
                }
            )
            ListItemForAppSetting(
                headingText = "Show Notifications",
                supportingText = "Get notified about changed ringtone after each call",
                trailingContent = {
                    Switch(
                        checked = showNotificationsBool,
                        onCheckedChange = { value ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionBool) {
                                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onClick(ClickEvents.UpdateShowNotificationsSetting(context, value))
                            }
                        }
                    )
                }
            )
        }
        Spacer(Modifier.height(48.dp))
    }
}