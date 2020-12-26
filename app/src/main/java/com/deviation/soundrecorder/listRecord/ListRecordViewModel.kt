package com.deviation.soundrecorder.listRecord

import androidx.lifecycle.ViewModel
import com.deviation.soundrecorder.database.RecordDao

class ListRecordViewModel(dataSource: RecordDao) : ViewModel() {
    val database = dataSource
    val records = database.getAllRecords()
}