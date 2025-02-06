package com.app.ringtonerandomizer.presentation.home_screen.components

import android.content.Context
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.R
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.core.domain.getRingtoneDuration
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RingtoneRow(
    ringtone: String,
    currentRingtone: String,
    context: Context,
    scope: CoroutineScope,
    onClick: (ClickEvents) -> Unit,
    index: Int,
    isPlaying: Int // current ringtone index which is playing
) {
    // to show the menu
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }

    var itemHeight by remember {
        mutableStateOf(0.dp)
    }

    var dropDownItems = listOf(
        "Set as ringtone", "Delete"
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp

    val density = LocalDensity.current
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // gradient background for current ringtone
    val bgModifier = if (ringtone == currentRingtone) {
        Modifier.background(
            brush = getGradientBackground()
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .indication(interactionSource, LocalIndication.current)
                .onSizeChanged {
                    itemHeight = with(density) { it.height.toDp() }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        },
                        // for ripple effect
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    )
                }
                .then(bgModifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index + 1}. $ringtone",
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = getRingtoneDuration("${GlobalVariables.PATH}$ringtone")
                )
                IconButton(
                    onClick = {
                        if (isPlaying == index) {
                            onClick(ClickEvents.PauseRingtone(ringtone, index))
                        } else {
                            onClick(ClickEvents.PlayRingtone(context, ringtone, index))
                        }
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (isPlaying == index) {
                                R.drawable.pause
                            } else R.drawable.play
                        ),
                        contentDescription = if (isPlaying == index) {
                            "Pause button"
                        } else "Play button"
                    )
                }
            }
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(
                x = if (pressOffset.x > (screenWidth.dp / 2)) {
                    (pressOffset.x) - (pressOffset.x / 3)
                } else {
                    pressOffset.x
                },
                y = pressOffset.y - itemHeight
            )
        ) {
            dropDownItems.forEach {
                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            isContextMenuVisible = false
                            delay(1000L)
                            onClick(
                                ClickEvents.DropDownClick(
                                    option = it,
                                    context = context,
                                    ringtone = ringtone
                                )
                            )
                        }
                    },
                    text = {
                        Text(it)
                    }
                )
            }
        }
    }
}