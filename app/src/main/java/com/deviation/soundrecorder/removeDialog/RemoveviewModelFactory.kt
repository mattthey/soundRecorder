package com.deviation.soundrecorder.removeDialog

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deviation.soundrecorder.database.RecordDao

class RemoveViewModelFactory(
    private val databaseDao: RecordDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoveViewModel::class.java)) {
            return RemoveViewModel(databaseDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}