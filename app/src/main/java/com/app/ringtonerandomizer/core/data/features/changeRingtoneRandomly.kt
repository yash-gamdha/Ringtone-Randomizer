package com.app.ringtonerandomizer.core.data.features

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import com.app.ringtonerandomizer.core.app_settings.dataStore
import com.app.ringtonerandomizer.core.data.CurrentRingtoneNotificationService
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.core.domain.getCurrentRingtone
import com.app.ringtonerandomizer.core.domain.getRingtoneNames
import com.app.ringtonerandomizer.permissions.checkNotificationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.system.exitProcess

// to change from the app
fun changeRingtone(context: Context, ringtone: String) {
    Log.d("new index", ringtone)

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
    val listOfRingtones = getRingtoneNames()?.sortedWith { o1, o2 ->
        o1.compareTo(o2, ignoreCase = true)
    }

    if (listOfRingtones!!.isNotEmpty()) {
        val currentRingtone = getCurrentRingtone(context)

        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.data.collect {
                if (it.serviceOn) {

                    val isNotificationPermissionGranted =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            checkNotificationPermission(context)
                        } else true

                    if (it.isSequentialRotationOn) {
                        val index =
                            (listOfRingtones.indexOf(currentRingtone) + 1) % listOfRingtones.size
                        val randomRingtone = listOfRingtones[index]

                        changeRingtone(context, randomRingtone)
                        CurrentRingtoneNotificationService(context).showNotification(
                            contentText = "Ringtone changed to $randomRingtone",
                            shouldShowNotification = isNotificationPermissionGranted && it.showNotifications
                        )
                        exitProcess(0)
                    } else {
                        var randomRingtone = listOfRingtones[Random.nextInt(listOfRingtones.size)]

                        while (randomRingtone == currentRingtone) {
                            randomRingtone = listOfRingtones[Random.nextInt(listOfRingtones.size)]
                        }
                        changeRingtone(context, randomRingtone)
                        CurrentRingtoneNotificationService(context).showNotification(
                            contentText = "Ringtone changed to $randomRingtone",
                            shouldShowNotification = isNotificationPermissionGranted && it.showNotifications
                        )
                    }
                }
            }
        }

    }
}