package com.example.app

import DataFetchWorker
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Space
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
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.example.app.ui.theme.AppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.util.concurrent.TimeUnit
import androidx.compose.ui.graphics.lerp



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Vico compose3") }) }) { paddingValues ->
                    MainScreen(modifier = Modifier.padding(paddingValues))
                }
            }
        }
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


//        updateWidgets()
//        setupAlarmManager()
//        setupPeriodicWork()

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
        val workRequest =
            PeriodicWorkRequestBuilder<DataFetchWorker>(15, TimeUnit.MINUTES).setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true).build()
            ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataFetchWork", ExistingPeriodicWorkPolicy.KEEP, workRequest
        )
    }

}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedOSIndex by remember { mutableStateOf(0) }
    var selectedBarIndex by remember { mutableStateOf(-1) }

    val timeRanges = listOf("24h", "7d", "1m", "自定义")
    val osOptions = listOf("win", "linux", "all")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 操作系统 TabRow
        TabRow(
            selectedTabIndex = selectedOSIndex,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            indicator = {}
        ) {
            osOptions.forEachIndexed { index, title ->
                val isSelected = selectedOSIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedOSIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
                        )
                )
            }
        }
        // 添加 TabRow 用于切换时间段
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)) // 添加圆角
                .background(MaterialTheme.colorScheme.primaryContainer),
            indicator = {}) {
            timeRanges.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        BarChartExample(timeRangeIndex = selectedTabIndex)
        BarChartExample(timeRangeIndex = selectedTabIndex)
    }
}


private fun getColorForValue(value: Float, minValue: Float = 0f, maxValue: Float = 100f): Color {
    val fraction = (value - minValue) / (maxValue - minValue)
    val clampedFraction = fraction.coerceIn(0f, 1f)

    return when {
        clampedFraction < 0.5f -> { // 0.0 - 0.5 (蓝色 → 紫色)
            val newFraction = clampedFraction / 0.5f // 重新缩放到 [0, 1]
            lerp(Color(0xff91eae4), Color(0xff86a8e7), newFraction)
        }
        else -> { // 0.5 - 1.0 (紫色 → 橙红色)
            val newFraction = (clampedFraction - 0.5f) / 0.5f // 重新缩放到 [0, 1]
            lerp(Color(0xff86a8e7), Color(0xff7f7fd5), newFraction)
        }
    }
}


fun getPositiveColumnProvider(positiveColumn: LineComponent): ColumnCartesianLayer.ColumnProvider {
    return object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore
        ): LineComponent {
//            val value = entry.y.toFloat() // 确保转换为 Float
            val color = getColorForValue(entry.y.toFloat()) // 根据数值获取颜色
            return LineComponent(
                fill = fill(color),
                thicknessDp = 8.0f,
                shape = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40)
            )
        }

        override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = positiveColumn
    }
}


//private fun getPositiveColumnProvider(positive: LineComponent) =
//    object : ColumnCartesianLayer.ColumnProvider {
//        override fun getColumn(
//            entry: ColumnCartesianLayerModel.Entry,
//            seriesIndex: Int,
//            extraStore: ExtraStore,
//        ) = positive // 只返回 positiveColumn
//
//        override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = positive
//    }


@Composable
fun BarChartExample(
    modifier: Modifier = Modifier,
    timeRangeIndex: Int) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val positiveColumn =
        rememberLineComponent(
//            fill = fill(Color(0xff0ac285)), // 绿色
//            fill = fill(
//                ShaderProvider.verticalGradient(
//                    intArrayOf(Color(0xff0ac285).toArgb(), Color(0xff00a6ff).toArgb()) // 绿色 → 蓝色渐变
//                )
//            ),
            thickness = 8.dp,
            shape = CorneredShape.rounded(topLeftPercent = 10, topRightPercent = 10) // 顶部圆角
        )

    // 根据时间段动态生成数据
    LaunchedEffect(timeRangeIndex) {
        modelProducer.runTransaction {
            when (timeRangeIndex) {
                0 -> columnSeries { series(20f, 40f, 30f, 50f, 70f, 40f, 20f, 40f, 50f,40f,23f,34f) } // 24h 数据
                1 -> columnSeries { series(10f, 20f, 15f, 25f, 35f,46f) } // 7d 数据
                2 -> columnSeries { series(5f, 10f, 8f, 12f, 18f) }   // 1m 数据
                else -> columnSeries { series(30f, 50f, 40f, 60f, 80f) } // 自定义数据
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = remember(positiveColumn) {
                    getPositiveColumnProvider(positiveColumn) // 只绑定正值柱状图样式
                },
                columnCollectionSpacing = 4.dp, // 控制柱子间距
            ),
            startAxis = VerticalAxis.rememberStart(guideline = null),
            bottomAxis = HorizontalAxis.rememberBottom(guideline = null),
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}



/** **预览模式** */
@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    AppTheme {
        MainScreen()
    }
}