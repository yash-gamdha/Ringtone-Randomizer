package com.app.ringtonerandomizer.presentation.home_screen.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(
    sheetState: SheetState,
    context: Context,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() },
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle()
                Text(
                    text = "App Info",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeadingText("Which permissions are needed and why?")

            permissionList.forEach {
                Spacer(Modifier.height(8.dp))
                PermissionText(it.permission, it.explanation)
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("NOTE")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(" : ")
                    }
                    append(
                        "If you deny permissions two times, permission pop-up won\'t show up" +
                                "and you will have to manually grant the permissions"
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HeadingText("How the app works?")

            working.forEach {
                Spacer(Modifier.height(8.dp))
                Text(text = "▷ $it")
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HeadingText("Developer info")

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Created by : ")
                    }
                    append("Yash Gamdha")
                },
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue
                        )
                    ) {
                        append("Github")
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse("https://github.com/yash-gamdha")
                        )
                        context.startActivity(intent)
                    }
            )
        }
    }
}

private data class PermissionExplanation(
    val permission: String,
    val explanation: String
)

private val permissionList = listOf(
    PermissionExplanation(
        "Modify system settings",
        "To change the default ringtone of the smartphone"
    ),
    PermissionExplanation(
        "Read and write audio",
        "To read the ringtone list and to add ringtones to it"
    ),
    PermissionExplanation(
        "Disable battery optimization",
        "To run the app in the background"
    ),
    PermissionExplanation(
        "Read phone state",
        "To detect incoming calls and change ringtone"
    )
)

private val working = listOf(
    "The app makes a directory named \'Randomizer\' in Ringtones folder.",
    "All the ringtones you add are copied there.",
    "Whenever the app detects an incoming call, the app fetches list of ringtones in the directory" +
            "and changes the ringtone randomly"
)