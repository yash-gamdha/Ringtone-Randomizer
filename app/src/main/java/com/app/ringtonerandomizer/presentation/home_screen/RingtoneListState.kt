package com.app.ringtonerandomizer.presentation.home_screen

import androidx.compose.runtime.Immutable

typealias Ringtones = String

@Immutable
data class RingtoneListState(
    val isLoading: Boolean = false,
    val ringtoneList: List<Ringtones> = emptyList(),
    val currentRingtone: String? = null
)