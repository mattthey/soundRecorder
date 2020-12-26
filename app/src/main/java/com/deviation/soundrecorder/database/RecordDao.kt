package com.deviation.soundrecorder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {
    companion object {
        const val GET_RECORD_BY_ID_SQL = "SELECT * from tbl_record WHERE id = :key"
        const val DELETE_ALL_SQL = "DELETE FROM tbl_record"
        const val DELETE_RECORD_BY_ID_SQL = "DELETE FROM tbl_record WHERE id = :key"
        const val DELETE_RECORD_BY_IDS_SQL = "DELETE FROM tbl_record WHERE id in (:keys)"
        const val GET_ALL_RECORDS_SQL = "SELECT * FROM tbl_record ORDER BY id DESC"
    }

    @Insert
    fun insert(record: RecordEntity)

    @Update
    fun update(record: RecordEntity)

    @Query(GET_RECORD_BY_ID_SQL)
    fun getRecord(key: Long?): RecordEntity?

    @Query(DELETE_ALL_SQL)
    fun clearAll()

    // легче передать id, чем использовать аннотацию {@link androidx.room.Query.Delete}
    @Query(DELETE_RECORD_BY_ID_SQL)
    fun removeRecord(key: Long?)

    @Query(DELETE_RECORD_BY_IDS_SQL)
    fun removeRecords(keys: List<Long>)

    @Query(GET_ALL_RECORDS_SQL)
    fun getAllRecords(): LiveData<MutableList<RecordEntity>>
}