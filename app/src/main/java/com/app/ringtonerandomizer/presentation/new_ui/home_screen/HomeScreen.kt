package com.app.ringtonerandomizer.presentation.new_ui.home_screen

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.permissions.checkBatteryOptimizationPermission
import com.app.ringtonerandomizer.permissions.checkModifySettingsPermission
import com.app.ringtonerandomizer.permissions.checkReadAudio
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents
import com.app.ringtonerandomizer.presentation.home_screen.RingtoneListState
import com.app.ringtonerandomizer.presentation.home_screen.components.MessageComposable
import com.app.ringtonerandomizer.R
import com.app.ringtonerandomizer.core.presentation.doToast
import com.app.ringtonerandomizer.presentation.home_screen.components.RingtoneList

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    state: RingtoneListState,
    onClick: (ClickEvents) -> Unit,
    snackBarHostState: SnackbarHostState,
    context: Context,
    isPlaying: Int,
    permissionMap: MutableState<Map<String, Boolean>>
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { listOfUri ->
            onClick(ClickEvents.CopyRingtone(listOfUri, context))
        }
    )

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

    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    var fabMenuExpanded by remember { mutableStateOf(false) }
    val fabItems = listOf(
        R.drawable.random_icon to "Select randomly",
        R.drawable.add_icon to "Add"
    )

    val fabActions = listOf(
        {
            if (modifySettings) {
                onClick(ClickEvents.ChangeRingtone(context))
            } else {
                doToast(
                    context = context,
                    message = "Modify settings permission is not allowed"
                )
            }
        },
        {
            if (readAudio) {
                picker.launch(arrayOf("audio/*"))
            } else {
                doToast(
                    context = context,
                    message = "Please grant necessary storage permissions"
                )
            }
        }
    )

    if (readAudio) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
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
                                contentDescription = "App Icon"
                            )
                        }
                    },
                    title = {
                        Text("Ringtone Randomizer")
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

                FloatingActionButtonMenu(
                    expanded = fabMenuExpanded,
                    button = {
                        ToggleFloatingActionButton(
                            checked = fabMenuExpanded,
                            onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                            modifier = Modifier.animateFloatingActionButton(
                                visible = fabVisible || fabMenuExpanded,
                                alignment = Alignment.BottomEnd
                            )
                        ) {
                            val imageVector by remember {
                                derivedStateOf {
                                        if (checkedProgress == 1f) R.drawable.close
                                            else R.drawable.add_icon
                                }
                            }

                            val rotate by remember {
                                derivedStateOf {
                                    if (checkedProgress > 0.5f) 90f else 0f
                                }
                            }

                            val animateRotate by animateFloatAsState(
                                targetValue = rotate,
                                label = "rotate",
                                animationSpec = tween(durationMillis = 250)
                            )

                            Icon(
                                imageVector = ImageVector.vectorResource(imageVector),
                                contentDescription = if (checkedProgress == 1f) "close" else "open",
                                modifier = Modifier
                                    .rotate(animateRotate)
                                    .animateIcon({ checkedProgress })
                            )
                        }
                    }
                ) {
                    fabItems.forEachIndexed { index, item ->
                        FloatingActionButtonMenuItem(
                            onClick = fabActions[index],
                            text = { Text(item.second) },
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(item.first),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            if (state.ringtoneList == null) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    LoadingIndicator()
                }
            } else {
                AnimatedContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    targetState = state,
                    label = "ringtone_list"
                ) { state ->
                    if (state.ringtoneList!!.isEmpty()) {
                        MessageComposable(
                            message = "Click \"+ Add\" button to add ringtones",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        RingtoneList(
                            ringtones = state.ringtoneList,
                            currentRingtone = state.currentRingtone ?: "",
                            state = listState,
                            context = context,
                            scope = rememberCoroutineScope(),
                            isPlaying = isPlaying,
                            onDropDownClick = onClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    } else {
        MessageComposable(
            message = "Please grant permission to read audio",
            modifier = Modifier.fillMaxSize()
        )
    }
}