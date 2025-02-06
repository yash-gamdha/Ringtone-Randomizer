package com.app.ringtonerandomizer.presentation.home_screen

import android.Manifest
import android.annotation.SuppressLint
import com.app.ringtonerandomizer.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.core.presentation.doToast
import com.app.ringtonerandomizer.core.presentation.snackBarRequestPermission
import com.app.ringtonerandomizer.permissions.checkBatteryOptimizationPermission
import com.app.ringtonerandomizer.permissions.checkModifySettingsPermission
import com.app.ringtonerandomizer.permissions.checkReadAudio
import com.app.ringtonerandomizer.presentation.home_screen.components.AppInfoBottomSheet
import com.app.ringtonerandomizer.presentation.home_screen.components.MessageComposable
import com.app.ringtonerandomizer.presentation.home_screen.components.RingtoneList

@SuppressLint("BatteryLife")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: RingtoneListState,
    onClick: (ClickEvents) -> Unit,
    snackBarHostState: SnackbarHostState,
    context: Context,
    isPlaying: Int,
    permissionMap: MutableState<Map<String, Boolean>>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }

    // scroll behavior and list state
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    // file picker
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { listOfUri ->
        onClick(ClickEvents.CopyRingtone(listOfUri, context))
    }

    // permissions
    var batteryOptimization by remember {
        mutableStateOf(checkBatteryOptimizationPermission(context))
    }
    var modifySettings by remember {
        mutableStateOf(checkModifySettingsPermission(context))
    }

    var readAudio by remember {
        mutableStateOf(checkReadAudio(context))
    }
    while (readAudio == false) {
        readAudio = checkReadAudio(context)
    }

    // to manipulate value of "expanded"
    var expanded = remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonColors(
                            containerColor = Color.Unspecified,
                            contentColor = Color.Unspecified,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Unspecified
                        )
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.twotone_music_note_24),
                            contentDescription = "App icon"
                        )
                    }
                },
                title = {
                    Text(text = "Ringtone Randomizer")
                },
                actions = {
                    IconButton(
                        onClick = {
                            isSheetVisible = true
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            contentDescription = "App info"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        if (modifySettings) {
                            onClick(ClickEvents.ChangeRingtone(context))
                        } else {
                            doToast(
                                context,
                                "Modify settings permission is not allowed"
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.random_icon),
                        contentDescription = "Change ringtone randomly",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.add_icon),
                            contentDescription = "Add"
                        )
                    },
                    text = {
                        Text("Add")
                    },
                    onClick = {
                        if (readAudio) {
                            picker.launch(arrayOf("audio/*"))
                        } else {
                            doToast(
                                context = context,
                                message = "Please grant necessary storage permissions"
                            )
                        }
                    },
                    expanded = expanded.value
                )
            }
        }
    ) { padding ->

        if ((permissionMap.value[Manifest.permission.READ_MEDIA_AUDIO]) ?: readAudio) {
            if (state.ringtoneList == null) {
                MessageComposable(
                    message = "Loading...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            } else {
                AnimatedContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    targetState = state,
                    label = "ringtone_list"
                ) { state ->
                    if (state.ringtoneList!!.isEmpty()) {
                        MessageComposable(
                            message = "Click \"+ Add\" button to add ringtones",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        )
                    } else {
                        RingtoneList(
                            ringtones = state.ringtoneList,
                            state = listState,
                            currentRingtone = state.currentRingtone.toString(),
                            context = context,
                            onDropDownClick = onClick,
                            scope = scope,
                            isPlaying = isPlaying,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            MessageComposable(
                message = "Please grant necessary permissions to see ringtone list",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
        if (!modifySettings) {
            snackBarRequestPermission(
                scope = scope,
                permission = "Modify system settings",
                snackBarHostState = snackBarHostState
            ) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                modifySettings = checkModifySettingsPermission(context)
            }
        }
        if (!batteryOptimization) {
            snackBarRequestPermission(
                scope = scope,
                permission = "Disable Battery Optimization",
                snackBarHostState = snackBarHostState
            ) {
                val intent =
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                context.startActivity(intent)
                batteryOptimization = checkBatteryOptimizationPermission(context)
            }
        }

        if (isSheetVisible) {
            AppInfoBottomSheet(
                sheetState = sheetState,
                context = context,
                onDismiss = { isSheetVisible = false }
            )
        }
    }
}