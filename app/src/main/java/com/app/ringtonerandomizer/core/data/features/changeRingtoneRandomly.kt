package com.app.ringtonerandomizer.core.data.features

import android.content.Context
import android.media.RingtoneManager
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.core.domain.getCurrentRingtone
import com.app.ringtonerandomizer.core.domain.getRingtoneNames
import kotlin.random.Random

// to change from the app
fun changeRingtone(context: Context, ringtone: String) {
    RingtoneManager.setActualDefaultRingtoneUri(
        context,
        RingtoneManager.TYPE_RINGTONE,
        getFileUri(
            "${GlobalVariables.PATH}$ringtone",
            context
        )
    )
}

// for when incoming call is detected
fun changeRingtone(context: Context) {
    var listOfRingtones = getRingtoneNames()
    val currentRingtone = getCurrentRingtone(context)
    if (listOfRingtones!!.isNotEmpty()) {
        var randomRingtone = listOfRingtones[Random.nextInt(listOfRingtones.size)]

        while (randomRingtone == currentRingtone) {
            randomRingtone = listOfRingtones[Random.nextInt(listOfRingtones.size)]
        }

        changeRingtone(context, randomRingtone)
    }
}