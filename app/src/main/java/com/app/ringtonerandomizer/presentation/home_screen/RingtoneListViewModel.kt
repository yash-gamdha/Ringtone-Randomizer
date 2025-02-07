package com.app.ringtonerandomizer.presentation.home_screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ringtonerandomizer.core.app_settings.dataStore
import com.app.ringtonerandomizer.core.data.GlobalVariables
import com.app.ringtonerandomizer.core.data.features.changeRingtone
import com.app.ringtonerandomizer.core.data.features.addRingtones
import com.app.ringtonerandomizer.core.data.features.deleteRingtone
import com.app.ringtonerandomizer.core.data.features.getFileUri
import com.app.ringtonerandomizer.core.domain.getCurrentRingtone
import com.app.ringtonerandomizer.core.domain.getRingtoneNames
import com.app.ringtonerandomizer.core.presentation.doToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RingtoneListViewModel(
    context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(RingtoneListState())
    lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    var permissionMap = mutableStateOf<Map<String, Boolean>>(emptyMap())

    val state = _state
        .onStart {
            loadRingtoneList()
            updateCurrentRingtone(context)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            RingtoneListState()
        )

    private val _isPlaying = MutableStateFlow(-1) // current ringtone index which is playing
    val isPlaying = _isPlaying.asStateFlow()
    var mediaPlayer: MediaPlayer? = null
    var currentPlayingRingtone: String = ""
    var ringtonePlayingJob: Job? = null


    fun onClick(clickEvent: ClickEvents) {
        when (clickEvent) {
            is ClickEvents.ChangeRingtone -> {
                changeRingtoneRandomly(context = clickEvent.context)
                updateCurrentRingtone(context = clickEvent.context) // updating current ringtone after changing it
            }

            is ClickEvents.CopyRingtone -> {
                addSelectedRingtones(clickEvent.listOfUri, clickEvent.context)
            }

            is ClickEvents.DropDownClick -> {
                when (clickEvent.option) {
                    "Set as ringtone" -> {
                        changeToSelectedRingtone(clickEvent.context, clickEvent.ringtone)
                        updateCurrentRingtone(context = clickEvent.context)
                    }

                    "Delete" -> {
                        if (clickEvent.ringtone == _state.value.currentRingtone) {
                            doToast(
                                clickEvent.context,
                                "Cannot delete current ringtone"
                            )
                        } else {
                            deleteSelectedRingtone(clickEvent.context, clickEvent.ringtone)
                        }
                    }
                }
            }

            is ClickEvents.PlayRingtone -> {
                if (currentPlayingRingtone != clickEvent.ringtone) {
                    currentPlayingRingtone = clickEvent.ringtone
                    val ringtoneUri = getFileUri(
                        "${GlobalVariables.PATH}${clickEvent.ringtone}",
                        clickEvent.context
                    )

                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(clickEvent.context, ringtoneUri)

                    ringtonePlayingJob?.cancel()
                }
                playRingtone(mediaPlayer!!, clickEvent.index)
            }

            is ClickEvents.PauseRingtone -> {
                pauseRingtone(mediaPlayer!!)
            }

            is ClickEvents.UpdateSequentialRotationSetting -> {
                updateSequentialRotationSetting(clickEvent.context, clickEvent.value)
            }
        }
    }

    private fun loadRingtoneList() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val list = getRingtoneNames()
            _state.update {
                it.copy(
                    isLoading = false,
                    ringtoneList = list!!.sortedWith { o1, o2 ->
                        o1.compareTo(o2, ignoreCase = true)
                    }
                )
            }
            _isPlaying.update {
                _state.value.ringtoneList!!.indexOf(currentPlayingRingtone)
            }
        }
    }

    private fun updateCurrentRingtone(context: Context) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentRingtone = getCurrentRingtone(context)
                )
            }
        }
    }

    private fun changeRingtoneRandomly(context: Context) {
        viewModelScope.launch {
            val random = _state.value.ringtoneList?.random() ?: return@launch
            changeRingtone(context, random)

            doToast(
                context,
                "Successfully changed to $random"
            )
        }
    }

    private fun changeToSelectedRingtone(context: Context, ringtone: String) {
        viewModelScope.launch {
            changeRingtone(context, ringtone)

            doToast(
                context,
                "Successfully changed to $ringtone"
            )
        }
    }

    private fun addSelectedRingtones(listOfUri: List<Uri>, context: Context) {
        viewModelScope.launch {
            if (addRingtones(listOfUri, context)) {
                doToast(context, "File(s) added successfully")
            } else {
                doToast(context, "failed to add some files")
            }

            loadRingtoneList() // refreshing list after adding ringtone
        }
    }

    private fun deleteSelectedRingtone(context: Context, ringtone: String) {
        viewModelScope.launch {
            if (currentPlayingRingtone == ringtone) { // in case if current playing ringtone gets deleted
                _isPlaying.update { -1 }
                mediaPlayer?.release()
            }
            if (deleteRingtone(context, ringtone, intentSenderLauncher)) {
                doToast(
                    context,
                    "Successfully deleted $ringtone"
                )
                loadRingtoneList()
            }
        }
    }

    private fun playRingtone(mediaPlayer: MediaPlayer, index: Int) {
        _isPlaying.update { index }
        ringtonePlayingJob = viewModelScope.launch { mediaPlayer.start() }
    }

    private fun pauseRingtone(mediaPlayer: MediaPlayer) {
        _isPlaying.update { -1 }
        ringtonePlayingJob = viewModelScope.launch { mediaPlayer.pause() }
    }

    // to update settings
    private fun updateSequentialRotationSetting(context: Context, value: Boolean) {
        viewModelScope.launch {
            context.dataStore.updateData {
                it.copy(isSequentialRotationOn = value)
            }
        }
    }
}