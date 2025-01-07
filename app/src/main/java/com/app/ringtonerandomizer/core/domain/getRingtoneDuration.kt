package com.app.ringtonerandomizer.core.domain

import android.annotation.SuppressLint
import android.media.MediaPlayer
import kotlin.math.roundToInt

fun getRingtoneDuration(path: String): String {
    var mediaPlayer: MediaPlayer? = null
    return try {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()
        return mediaPlayer.duration.msToMinuteSecond()
    } catch (e: Exception) {
        "-1"
    } finally {
        mediaPlayer?.release()
    }
}

@SuppressLint("DefaultLocale")
fun Int.msToMinuteSecond(): String {
    var seconds = (this / 1000.0).roundToInt()
    val minutes = seconds / 60
    seconds -= (60 * minutes)
    return String.format("%02d:%02d", minutes, seconds)
}