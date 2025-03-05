package com.example.app

import DataFetchWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar

class AppWidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // 检查是否在晚上12点到早上8点之间
        if (hour in 0..7) {
            return
        }
        val workRequest = OneTimeWorkRequestBuilder<DataFetchWorker>().build()
        // 加入WorkManager队列
        WorkManager.getInstance(context).enqueue(workRequest)

    }
}
