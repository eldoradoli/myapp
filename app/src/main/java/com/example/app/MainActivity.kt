package com.example.app

import DataFetchWorker
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.db.williamchart.view.BarChartView
import com.example.app.ui.theme.AppTheme
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
//        setContent {
//            AppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
        setContentView(R.layout.activity_main)
        // 设置欢迎信息
        val greetingTextView: TextView = findViewById(R.id.greeting_text)
        greetingTextView.text = "Hello Android!"

        // 获取并设置图表数据
        val barChart: BarChartView = findViewById(R.id.barChart)
        
        // 准备数据：月份和对应的值
        val barData = listOf(
            "Jan" to 20f,
            "Feb" to 40f,
            "Mar" to 30f,
            "Apr" to 50f,
            "May" to 70f
        )

        // 设置柱状图的数据
        barChart.animation.duration = 1000L  // 动画时间
        barChart.animate(barData)  // 为图表添加数据

        // 可选：显示点击的柱状数据
//        barChart.onDataPointTouchListener = { index, value ->
//            // 这里可以通过 Toast 或其他方式显示柱状图的数据
//            Toast.makeText(this, "Clicked on ${barData[index].first}: Value = $value", Toast.LENGTH_SHORT).show()
//        }


        updateWidgets()
//        setupAlarmManager()
        setupPeriodicWork()

    }

    private fun updateWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(packageName, AppWidget::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        if (appWidgetIds.isNotEmpty()) {
            AppWidget().onUpdate(this, appWidgetManager, appWidgetIds)
        }
    }

    private fun setupPeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataFetchWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AppTheme {
//        Greeting("Android")
//    }
//}