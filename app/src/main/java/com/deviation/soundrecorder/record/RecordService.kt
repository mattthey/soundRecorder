package com.deviation.soundrecorder.record

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.deviation.soundrecorder.MainActivity
import com.deviation.soundrecorder.R
import com.deviation.soundrecorder.database.RecordDao
import com.deviation.soundrecorder.database.RecordEntity
import com.deviation.soundrecorder.database.SoundRecorderDatabase
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecordService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): RecordService = this@RecordService
    }

    private var mFileName: String? = null
    private var mFilePath: String? = null

    private var mRecorder: MediaRecorder? = null

    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0

    private var mDatabase: RecordDao? = null

    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    override fun onBind(intent: Intent?): IBinder {
        startRecording()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (mRecorder != null)
            stopRecording()

        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        mDatabase = SoundRecorderDatabase.getInstance(application).recordDatabaseDao
    }

    private fun startRecording() {
        setFileNameAndPath()

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder?.setOutputFile(mFilePath)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder?.setAudioChannels(1)
        mRecorder?.setAudioEncodingBitRate(192000)

        try {
            mRecorder?.prepare()
            mRecorder?.start()
            mStartingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: IOException) {
            Log.e("RecordService", "prepare failed")
        }
    }

    fun pauseRecording() {
        mRecorder?.pause()
    }

    fun resumeRecording() {
        mRecorder?.resume()
    }

    private fun createNotification(): Notification? {
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_mic_white_36dp)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_recording))
            .setOngoing(true)
        mBuilder.setContentIntent(
            PendingIntent.getActivities(
                applicationContext,
                0,
                arrayOf(Intent(applicationContext, MainActivity::class.java)),
                0
            )
        )

        return mBuilder.build()
    }

    private fun stopRecording() {
        val recordingItem = RecordEntity()

        mRecorder?.stop()
        mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
        mRecorder?.release()
        Toast.makeText(this, getString(R.string.toast_recording_finish), Toast.LENGTH_SHORT).show()

        recordingItem.name = mFileName.toString()
        recordingItem.filePath = mFilePath.toString()
        recordingItem.length = mFilePath?.let { getDuration(it) } ?: 0
        recordingItem.time = System.currentTimeMillis()


        mRecorder = null

        try {
            mUiScope.launch {
                withContext(Dispatchers.IO) {
                    mDatabase?.insert(recordingItem)
                }
            }
        } catch (e: Exception) {
            Log.e("RecordService", "exception", e)
        }
    }

    private fun setFileNameAndPath() {
        var count = 0
        var f: File
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(System.currentTimeMillis())

        do {
            mFileName = "${getString(R.string.default_file_name)}_${dateTime}_${count}.mp4"
            mFilePath = application.getExternalFilesDir(null)?.absolutePath
            mFilePath += "/$mFileName"

            count++

            f = File(mFilePath)
        } while (f.exists() && !f.isDirectory)
    }

    private fun getDuration(path: String): Long? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)

        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()
    }

}