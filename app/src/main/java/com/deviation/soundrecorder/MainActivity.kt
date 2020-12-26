package com.deviation.soundrecorder

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Меню переключения:
        // Необходимо передать меню навигации и основной View
        NavigationUI.setupWithNavController(bottom_navigation,
        Navigation.findNavController(this, R.id.nav_host_fragment_container))
    }

    fun isServiceRunning(): Boolean {
        val  manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // TODO исправить
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.deviation.soundrecorder.record.RecordService" == service.service.className) {
                return true
            }
        }
        return false
    }
}