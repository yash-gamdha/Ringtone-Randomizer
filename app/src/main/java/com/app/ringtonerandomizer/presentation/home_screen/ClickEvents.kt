package com.app.ringtonerandomizer.presentation.home_screen

import android.content.Context
import android.net.Uri

sealed interface ClickEvents {
    data class ChangeRingtone(val context: Context): ClickEvents
    data class CopyRingtone(val listOfUri: List<Uri>, val context: Context): ClickEvents
    data class DropDownClick(val context: Context,val option: String, val ringtone: String): ClickEvents
    data class PlayRingtone(val context: Context, val ringtone: String, val index: Int): ClickEvents
    data class PauseRingtone(val ringtone: String, val index: Int): ClickEvents
}