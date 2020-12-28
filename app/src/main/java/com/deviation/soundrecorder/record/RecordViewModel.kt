package com.deviation.soundrecorder.record

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RecordViewModel : ViewModel() {

    private val SECOND: Long = 1_000L

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTimeString: LiveData<String> = Transformations.map(_elapsedTime) { time ->
        timeFormatter(time)
    }

    private lateinit var timer: CountDownTimer

    var isBounded = false
    var isPaused = false

    lateinit var recordService: RecordService

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordService.LocalBinder
            recordService = binder.getService()
            isBounded = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBounded = false
        }
    }

    private fun timeFormatter(time: Long): String {
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time) % 60,
            TimeUnit.MILLISECONDS.toMinutes(time) % 60,
            TimeUnit.MILLISECONDS.toSeconds(time) % 60
        )
    }

    fun stopTimer() {
        if (this::timer.isInitialized)
            timer.cancel()

        resetTimer()
    }

    fun startTimer() {
        viewModelScope.launch {
            timer = countDownTimer(SystemClock.elapsedRealtime())
            timer.start()
        }
    }

    fun pauseTimer() {
        timer.cancel()
    }

    fun resumeTimer() {
        viewModelScope.launch {
            timer = countDownTimer(SystemClock.elapsedRealtime() - (_elapsedTime.value ?: 0))
            timer.start()
        }
    }

    fun resetTimer() {
        _elapsedTime.value = 0
    }

    private fun countDownTimer(triggerTime: Long):CountDownTimer{
        return object: CountDownTimer(triggerTime, SECOND){
            override fun onTick(millisUntilFinished: Long){
                _elapsedTime.value = SystemClock.elapsedRealtime() - triggerTime
            }

            override fun onFinish() {
                resetTimer()
            }
        }
    }
}