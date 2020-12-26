package com.deviation.soundrecorder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao
{
    @Insert
    fun insert(record: RecordEntity)

    @Update
    fun update(record: RecordEntity)

    // TODO переделать на констатны или вынести в отдельный файл. На данном этапе и размере склоняюсь к константам
    @Query("SELECT * from tbl_record WHERE id = :key")
    fun getRecord(key: Long?): RecordEntity?

    @Query("DELETE FROM tbl_record")
    fun clearAll()

    @Query("DELETE FROM tbl_record WHERE id = :key")
    fun removeRecord(key: Long?)

    @Query("DELETE FROM tbl_record WHERE id in (:keys)")
    fun removeRecords(keys: List<Long>)

    @Query("SELECT * FROM tbl_record ORDER BY id DESC")
    fun getAllRecords(): LiveData<MutableList<RecordEntity>>
}