package com.deviation.soundrecorder.record

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deviation.soundrecorder.MainActivity
import com.deviation.soundrecorder.R
import com.deviation.soundrecorder.database.RecordDao
import com.deviation.soundrecorder.database.SoundRecorderDatabase
import com.deviation.soundrecorder.databinding.FragmentRecordBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.File

class RecordFragment : Fragment() {
    private val PERMISSIONS_RECORD_AUDIO = 123

    private val recordsFolder = activity?.getExternalFilesDir(null)?.absolutePath.toString() + "/SoundRecorder"

    private lateinit var viewModel: RecordViewModel
    private lateinit var mainActivity: MainActivity
    private var database: RecordDao? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentRecordBinding>(
            inflater,
            R.layout.fragment_record,
            container,
            false
        )
        database = context?.let {
            SoundRecorderDatabase.getInstance(it).recordDatabaseDao
        }
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)

        binding.recordViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (viewModel.isBounded) {
            when (viewModel.isPaused) {
                true -> binding.playButton.setImageResource(R.drawable.ic_media_play)
                false -> binding.playButton.setImageResource(R.drawable.ic_media_pause)
            }
            binding.stopButton.visibility = View.VISIBLE
        } else {
            viewModel.resetTimer()
        }

        binding.playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO
                )

                return@setOnClickListener
            }

            if (viewModel.isBounded) {
                when (viewModel.isPaused) {
                    true -> resumeRecord()
                    false -> pauseRecord()
                }
            }
            else {
                startRecord()
            }
        }

        binding.stopButton.setOnClickListener {
            if (viewModel.isBounded)
                stopRecord()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        return binding.root
    }

    private fun startRecord() {
        onRecord(true)
        viewModel.startTimer()
        stopButton.visibility = View.VISIBLE
    }

    private fun stopRecord() {
        onRecord(false)
        viewModel.stopTimer()
        stopButton.visibility = View.GONE
    }

    private fun pauseRecord() {
        if (viewModel.isBounded) {
            playButton.setImageResource(R.drawable.ic_media_play)
            viewModel.isPaused = true
            viewModel.pauseTimer()
            viewModel.recordService.pauseRecording()
        }
    }

    private fun resumeRecord() {
        if (viewModel.isBounded) {
            playButton.setImageResource(R.drawable.ic_media_pause)
            viewModel.isPaused = false
            viewModel.resumeTimer()
            viewModel.recordService.resumeRecording()
        }
    }

    private fun onRecord(start: Boolean) {
        if (start) {
            playButton.setImageResource(R.drawable.ic_media_pause)
            Toast.makeText(activity, R.string.toast_recording_start, LENGTH_SHORT).show()

            val folder = File(recordsFolder)
            if (!folder.exists())
                folder.mkdir()

            Intent(activity, RecordService::class.java).also { intent ->
                activity?.bindService(intent, viewModel.connection, Context.BIND_AUTO_CREATE)
            }

            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            playButton.setImageResource(R.drawable.ic_mic_white_36dp)

            if (viewModel.isBounded) {
                activity?.unbindService(viewModel.connection)
                viewModel.isBounded = false
            }

            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    startRecord()
                } else {
                    Toast.makeText(
                            activity,
                            getString(R.string.toast_recording_permissions),
                            LENGTH_SHORT)
                            .show()
                }

                return
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
        val notificationManager =
            requireActivity().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
