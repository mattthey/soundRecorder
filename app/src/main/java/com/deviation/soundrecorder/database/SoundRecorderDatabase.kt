package com.deviation.soundrecorder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Синглтон БД, отвечает за ведение БД и предоставление экземпляров DAO.
 */
@Database(entities = [RecordEntity::class], version = 1, exportSchema = false)
abstract class SoundRecorderDatabase : RoomDatabase() {
    abstract val recordDatabaseDao: RecordDao

    companion object
    {

        @Volatile
        private var INSTANCE: SoundRecorderDatabase? = null

        fun getInstance(context: Context): SoundRecorderDatabase
        {
            synchronized(this)
            {
                var instance = INSTANCE

                if (instance == null)
                {
                    instance = Room.databaseBuilder(context.applicationContext, SoundRecorderDatabase::class.java, "record_db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance

            }
        }
    }
}