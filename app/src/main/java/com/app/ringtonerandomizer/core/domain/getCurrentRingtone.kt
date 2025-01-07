package com.app.ringtonerandomizer.core.domain

import android.content.Context
import android.media.RingtoneManager
import com.app.ringtonerandomizer.core.data.features.getRingtoneName

fun getCurrentRingtone(context: Context): String {
    // current ringtone URI
    val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
        context, RingtoneManager.TYPE_RINGTONE
    )

    return getRingtoneName(ringtoneUri, context.contentResolver)
}