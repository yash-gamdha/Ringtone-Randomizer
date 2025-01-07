package com.app.ringtonerandomizer.core.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun snackBarRequestPermission(
    scope: CoroutineScope,
    permission: String,
    snackBarHostState: SnackbarHostState,
    onActionPerformed: () -> Unit
) {
    scope.launch {
        snackBarHostState.currentSnackbarData?.dismiss()

        val result = snackBarHostState.showSnackbar(
            message = "Allow $permission",
            actionLabel = "ALLOW",
            duration = SnackbarDuration.Indefinite
        )

        when (result) {
            SnackbarResult.ActionPerformed -> {
                onActionPerformed()
            }
            SnackbarResult.Dismissed -> {

            }
        }
    }
}