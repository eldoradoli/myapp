package com.example.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Typeface
import android.widget.RemoteViews

class AppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, 0,0,null)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            hours:Int,
            minutes: Int,
            intensityValues: List<Int>? = null
        ) {
            val views = RemoteViews(context.packageName, R.layout.app_widget)

            // 分别生成每个文本部分的位图
            val bitmapHour = BitmapHelper.createTextBitmap(
                context,
//                "11",
                hours.toString(),
                R.font.oneui,
                85f,
                android.graphics.Color.WHITE,
                0.05f,
                Typeface.BOLD
            )

            val bitmapZhHour = BitmapHelper.createTextBitmap(
                context,
                "小时",
                R.font.oneui,
                45f,
                android.graphics.Color.WHITE,
                0.00f,
                Typeface.BOLD
            )

            val bitmapMinute = BitmapHelper.createTextBitmap(
                context,
//                "20",
                minutes.toString(),
                R.font.oneui,
                85f,
                android.graphics.Color.WHITE,
                0.05f,
                Typeface.BOLD
            )

            val bitmapZhMinute = BitmapHelper.createTextBitmap(
                context,
                "分钟",
                R.font.oneui,
                45f,
                android.graphics.Color.WHITE,
                0.00f,
                Typeface.BOLD
            )

            // 将生成的位图设置到不同的 ImageView 中
            views.setImageViewBitmap(R.id.imgViewHour, bitmapHour)
            views.setImageViewBitmap(R.id.imgViewZhHour, bitmapZhHour)
            views.setImageViewBitmap(R.id.imgViewMinute, bitmapMinute)
            views.setImageViewBitmap(R.id.imgViewZhMinute, bitmapZhMinute)

            fun scaleToRange(intensities: List<Int>, newMin: Int, newMax: Int): List<Int> {
                // 找到数据中的最小值和最大值
                val minValue = intensities.minOrNull() ?: 0
                val maxValue = intensities.maxOrNull() ?: 1

                // 线性缩放到新范围
                return intensities.map { intensity ->
                    if (maxValue == minValue) {
                        newMin // 如果所有值都相同，直接返回新范围的最小值
                    } else {
                        val scaled = ((intensity - minValue).toDouble() / (maxValue - minValue) * (newMax - newMin)).toInt() + newMin
                        scaled.coerceIn(newMin, newMax) // 确保缩放后的值在新范围内
                    }
                }
            }

//            // 设置强度值数组并生成表示工作强度的位图
//            val defaultIntensityValues = listOf(
//                368, 142, 500,
//                8, 165, 645, 21, 34,
//                50, 200, 5
//            )
            val defaultIntensityValues = listOf(
                0,0,0,
                0,0,0,0,
                0,0,0
            )
            val finalIntensityValues = intensityValues ?: defaultIntensityValues
            val scaledIntensities = scaleToRange(finalIntensityValues, 0, 49)

            val intensityBarBitmap =
                ChartHelper.createIntensityBarBitmap(context, 1200, 80, scaledIntensities, 50f)
            views.setImageViewBitmap(R.id.intensityBarView, intensityBarBitmap)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAppWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
            hours: Int,
            minutes: Int,
            intensityValues: List<Int>

        ) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, hours,minutes,intensityValues)
            }
        }
    }
}
//package com.example.app
//
//import android.appwidget.AppWidgetManager
//import android.appwidget.AppWidgetProvider
//import android.content.Context
//import android.graphics.Typeface
//import android.util.Log
//import android.widget.RemoteViews
//import kotlin.math.log
//
//class AppWidget : AppWidgetProvider() {
//    override fun onUpdate(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetIds: IntArray
//    ) {
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId)
//        }
//    }
//
//    companion object {
//        fun updateAppWidget(
//            context: Context,
//            appWidgetManager: AppWidgetManager,
//            appWidgetId: Int
//        ) {
//            val views = RemoteViews(context.packageName, R.layout.app_widget)
//
//            // 分别生成每个文本部分的位图
//            val bitmapHour = BitmapHelper.createTextBitmap(
//                context,
//                "11",
//                R.font.oneui,
//                85f,
//                android.graphics.Color.WHITE,
//                0.05f,
//                Typeface.BOLD
//            )
//
//            val bitmapZhHour = BitmapHelper.createTextBitmap(
//                context,
//                "小时",
//                R.font.oneui,
//                45f,
//                android.graphics.Color.WHITE,
//                0.00f,
//                Typeface.BOLD
//            )
//
//            val bitmapMinute = BitmapHelper.createTextBitmap(
//                context,
//                "20",
//                R.font.oneui,
//                85f,
//                android.graphics.Color.WHITE,
//                0.05f,
//                Typeface.BOLD
//            )
//
//            val bitmapZhMinute = BitmapHelper.createTextBitmap(
//                context,
//                "分钟",
//                R.font.oneui,
//                45f,
//                android.graphics.Color.WHITE,
//                0.00f,
//                Typeface.BOLD
//            )
//
//            // 将生成的位图设置到不同的 ImageView 中
//            views.setImageViewBitmap(R.id.imgViewHour, bitmapHour)
//            views.setImageViewBitmap(R.id.imgViewZhHour, bitmapZhHour)
//            views.setImageViewBitmap(R.id.imgViewMinute, bitmapMinute)
//            views.setImageViewBitmap(R.id.imgViewZhMinute, bitmapZhMinute)
//
//            fun scaleToRange(intensities: List<Int>, newMin: Int, newMax: Int): List<Int> {
//                // 找到数据中的最小值和最大值
//                val minValue = intensities.minOrNull() ?: 0
//                val maxValue = intensities.maxOrNull() ?: 1
//
//                // 线性缩放到新范围
//                return intensities.map { intensity ->
//                    if (maxValue == minValue) {
//                        newMin // 如果所有值都相同，直接返回新范围的最小值
//                    } else {
//                        val scaled = ((intensity - minValue).toDouble() / (maxValue - minValue) * (newMax - newMin)).toInt() + newMin
//                        scaled.coerceIn(newMin, newMax) // 确保缩放后的值在新范围内
//                    }
//                }
//            }
//
//
//            // 设置强度值数组并生成表示工作强度的位图
//            val intensityValues = listOf(
//                368, 142, 500,
//                8, 165, 645, 21, 34,
//                50,200, 5
//            )
//            val scaledIntensities = scaleToRange(intensityValues, 0, 49)
//            Log.i("dsf", "updateAppWidget: "+scaledIntensities)
//
//
////            val intensityValues = listOf(0, 99,0, 99,0, 99,0, 99,0, 99,0, 99, )
////            val intensityValues = listOf(10,99,10,99)
//
//            val intensityBarBitmap =
//                ChartHelper.createIntensityBarBitmap(context, 1200, 80, scaledIntensities,50f)
//            views.setImageViewBitmap(R.id.intensityBarView, intensityBarBitmap)
//
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
//    }
//}
