package com.app.ringtonerandomizer.core.presentation

import android.content.Context
import android.widget.Toast

fun doToast(
    context: Context,
    message: String
) = Toast.makeText(
    context,
    message,
    Toast.LENGTH_SHORT
).show()