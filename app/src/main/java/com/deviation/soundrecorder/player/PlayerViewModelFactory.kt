package com.deviation.soundrecorder.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayerViewModelFactory(
    private val mediaPath: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(mediaPath, application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}