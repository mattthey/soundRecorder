package com.deviation.soundrecorder.listRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deviation.soundrecorder.database.RecordDao

class ListRecordViewModelFactory(private val dataSource: RecordDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListRecordViewModel::class.java)) {
            return ListRecordViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}