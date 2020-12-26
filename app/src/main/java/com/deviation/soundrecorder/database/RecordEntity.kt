package com.deviation.soundrecorder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Описание таблицы записи данных
 */
@Entity(tableName = "tbl_record")
data class RecordEntity (

    // TODO избавиться за ненадобностью
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "filePath")
    var filePath: String = "",

    @ColumnInfo(name = "length")
    var length: Long = 0L,

    @ColumnInfo(name = "time")
    var time: Long = 0L
)
