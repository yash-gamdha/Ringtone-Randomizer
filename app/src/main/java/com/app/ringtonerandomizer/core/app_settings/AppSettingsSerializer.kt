package com.app.ringtonerandomizer.core.app_settings

import android.content.Context
import android.util.Log
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.app.ringtonerandomizer.core.data.GlobalVariables
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer: Serializer<AppSettings> {
    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Log.d("error", e.message.toString())
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: AppSettings,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

    override val defaultValue: AppSettings
        get() = AppSettings()
}

val Context.dataStore by dataStore(GlobalVariables.APPSETTINGSPATH, AppSettingsSerializer)