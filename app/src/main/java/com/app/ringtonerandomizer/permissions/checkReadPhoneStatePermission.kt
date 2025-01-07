package com.app.ringtonerandomizer.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun checkReadPhoneStatePermission(context: Context) =
    ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
