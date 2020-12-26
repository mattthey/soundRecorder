package com.deviation.soundrecorder.record

import android.app.NotificationChannel
import android.app.NotificationManager
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
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class RecordFragment : Fragment() {
    private val PERMISSIONS_RECORD_AUDIO = 123

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
            container, false
        )
        database = context?.let { SoundRecorderDatabase.getInstance(it).recordDatabaseDao }
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        binding.recordViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (!mainActivity.isServiceRunning()) {
            viewModel.resetTimer()
        } else {
            binding.playButton.setImageResource(R.drawable.ic_media_stop)
        }

        binding.playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.RECORD_AUDIO
                ) != PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO
                )
            } else {
                if (mainActivity.isServiceRunning()) {
                    onRecord(false)
                    viewModel.stopTimer()
                } else {
                    onRecord(true)
                    viewModel.startTimer()
                }
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
        return binding.root
    }

    private fun onRecord(start: Boolean) {
        val intent = Intent(activity, RecordService::class.java)

        if (start) {
            playButton.setImageResource(R.drawable.ic_media_stop)
            Toast.makeText(activity, R.string.toast_recording_start, LENGTH_SHORT).show()

            // TODO В константы
            val folder =
                File(activity?.getExternalFilesDir(null)?.absolutePath.toString() + "/SoundRecorder")
            if (!folder.exists()) {
                folder.mkdir()
            }

            activity?.startService(intent)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            playButton.setImageResource(R.drawable.ic_mic_white_36dp)

            activity?.stopService(intent)
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
                    onRecord(true)
                    viewModel.startTimer()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_recording_permissions),
                        LENGTH_SHORT
                    ).show()
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
