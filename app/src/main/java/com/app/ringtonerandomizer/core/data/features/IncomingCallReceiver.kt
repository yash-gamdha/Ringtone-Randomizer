package com.app.ringtonerandomizer.core.data.features

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.app.ringtonerandomizer.permissions.checkModifySettingsPermission

class IncomingCallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING && checkModifySettingsPermission(context!!)) {
                changeRingtone(context)
            }
        }
    }
}