package com.app.ringtonerandomizer.permissions

import android.content.Context
import android.provider.Settings

fun checkModifySettingsPermission(context: Context) = Settings.System.canWrite(context)
