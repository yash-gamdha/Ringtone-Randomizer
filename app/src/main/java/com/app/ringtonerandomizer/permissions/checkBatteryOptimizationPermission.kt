package com.app.ringtonerandomizer.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import androidx.core.content.ContextCompat

fun checkBatteryOptimizationPermission(context: Context): Boolean {
    // accessing battery settings
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}