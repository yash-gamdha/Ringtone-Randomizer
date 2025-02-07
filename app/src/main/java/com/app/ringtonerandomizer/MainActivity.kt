package com.app.ringtonerandomizer

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ringtonerandomizer.core.data.features.IncomingCallReceiver
import com.app.ringtonerandomizer.core.presentation.doToast
import com.app.ringtonerandomizer.presentation.home_screen.HomeScreen
import com.app.ringtonerandomizer.presentation.home_screen.RingtoneListViewModel
import com.app.ringtonerandomizer.ui.theme.RingtoneRandomizerTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("BatteryLife")
class MainActivity : ComponentActivity() {
    lateinit var callReceiver: IncomingCallReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        callReceiver = IncomingCallReceiver()
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, intentFilter)

        setContent {
            val snackBarHostState = remember { SnackbarHostState() }

            val ringtoneListViewModel = RingtoneListViewModel(this@MainActivity)
            ringtoneListViewModel.intentSenderLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    doToast(this@MainActivity, "deleted successfully")
                } else {
                    doToast(this@MainActivity, "Failed to delete")
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) {
                ringtoneListViewModel.permissionMap.value = it
            }

            // to launch permission dialog boxes
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                lifecycleOwner
            ) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        ) // launch
                    }
                }

                // adding observer to lifecycle
                lifecycleOwner.lifecycle.addObserver(observer)

                // removing observer from lifecycle
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            RingtoneRandomizerTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentWindowInsets = WindowInsets(0,0,0,0)
                ) { innerPadding ->
                    val state by ringtoneListViewModel.state.collectAsStateWithLifecycle()
                    val isPlaying by ringtoneListViewModel.isPlaying.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        context = this@MainActivity,
                        onClick = ringtoneListViewModel::onClick,
                        snackBarHostState = snackBarHostState,
                        isPlaying = isPlaying,
                        modifier = Modifier.padding(innerPadding),
                        permissionMap = ringtoneListViewModel.permissionMap
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callReceiver)
    }
}