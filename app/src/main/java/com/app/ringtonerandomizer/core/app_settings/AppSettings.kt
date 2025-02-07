package com.app.ringtonerandomizer.core.app_settings

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val isSequentialRotationOn: Boolean = false
)