package com.app.ringtonerandomizer.presentation.home_screen.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.app.ringtonerandomizer.R
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.core.domain.getRingtoneDuration
import com.app.ringtonerandomizer.presentation.home_screen.ClickEvents
import com.app.ringtonerandomizer.ui.theme.RingtoneRandomizerTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RingtoneRow(
    ringtone: String,
    currentRingtone: String,
    context: Context,
    onClick: (ClickEvents) -> Unit,
    index: Int,
    isPlaying: Int, // current ringtone index which is playing
    modifier: Modifier = Modifier
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    // gradient background for current ringtone
    val bgModifier = if (ringtone == currentRingtone) {
        modifier.background(brush = getGradientBackground())
    } else {
        modifier.background(
            if (isExpanded) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.background
        )
    }

    val isPlayingBool by derivedStateOf {
        isPlaying == index
    }

    val rotateValue by derivedStateOf {
        if (isPlayingBool) 360F else 0F
    }

    val animationProgress by animateFloatAsState(
        targetValue = rotateValue,
        label = "toggle_play_pause",
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val playPauseIconVector by derivedStateOf {
        if (isPlayingBool && rotateValue >= 90) R.drawable.pause else R.drawable.play
    }

    Column(
        modifier = bgModifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(8.dp)
        ) {
            Text(
                text = "${index + 1}. $ringtone",
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(3F)
            )
            Text(
                text = getRingtoneDuration("${GlobalVariables.PATH}$ringtone"),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1F)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            label = "ringtone_actions",
            enter = expandVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
            exit = shrinkVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
            modifier = Modifier.padding(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        onClick(ClickEvents.DeleteRingtone(context, ringtone))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = "delete $ringtone",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }

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
                        imageVector = ImageVector.vectorResource(playPauseIconVector),
                        contentDescription = if (isPlaying != -1) "pause $ringtone" else "play $ringtone",
                        modifier = Modifier
                            .rotate(animationProgress),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }

                IconButton(
                    onClick = {
                        onClick(ClickEvents.SetRingtone(context, ringtone))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.music),
                        contentDescription = "set $ringtone as ringtone",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun RingtoneRowPreview() {
    RingtoneRandomizerTheme {
        RingtoneRow(
            ringtone = "Ringtone 1",
            currentRingtone = "Ringtone 2",
            context = LocalContext.current,
            onClick = { },
            index = 1,
            isPlaying = -1,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )
    }
}