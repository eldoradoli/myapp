package com.example.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.ui.screens.BarChartExample
import com.example.app.charts.getPositiveColumnProvider
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodel.MainUiState
import com.example.app.ui.viewmodel.MainViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

//收集ViewModel状态并传递给UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Vico compose3") }) }
    ) { paddingValues ->
        MainScreenContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onTimeRangeSelected = mainViewModel::onTimeRangeSelected,
            onOsSelected = mainViewModel::onOsSelected
        )

    }
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    onTimeRangeSelected: (Int) -> Unit,
    onOsSelected: (Int) -> Unit,

    ) {
    val timeRanges = listOf("24h", "7d", "1m", "custom")
    val osOptions = listOf("win", "linux", "all")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SecondaryTabRow(
                selectedTabIndex = uiState.selectOsIndex,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                indicator = {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(uiState.selectOsIndex)
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                },
                divider = {}
            ) {
                osOptions.forEachIndexed { index, title ->
                    val isSelected = uiState.selectOsIndex == index
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onOsSelected(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SecondaryTabRow(
                selectedTabIndex = uiState.selectTimeRangeIndex, // 使用来自ViewModel的状态
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                indicator = {}
            ) {
                timeRanges.forEachIndexed { index, title ->
                    val isSelected = uiState.selectTimeRangeIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { onTimeRangeSelected(index) }, // 点击时调用事件处理器
                        text = {
                            Text(
                                title,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            // 传递时间范围索引给图表组件
            BarChartExample(timeRangeIndex = uiState.selectTimeRangeIndex)
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // 给一个固定的高度
                contentAlignment = Alignment.Center
            ) {
                // 如果正在加载，显示一个圆形的加载动画
                if (uiState.isHeatmapLoading) {
                    CircularProgressIndicator()
                }
                // 如果加载完成且数据不为空，则显示热力图
                else if (uiState.heatmapData != null) {
//                HeatmapChart(data = uiState.heatmapData)
//                AnimatedHeatmapChart(data = uiState.heatmapData)
                    AnimatedContent(
                        // targetState 是驱动动画的关键。当 heatmapData 变化时，动画就会触发。
                        targetState = uiState.heatmapData,
                        // transitionSpec 定义了动画效果
                        transitionSpec = {
                            // 进入动画：淡入
                            fadeIn(
                                animationSpec = androidx.compose.animation.core.tween(
                                    durationMillis = 600
                                )
                            ) togetherWith
                                    // 退出动画：淡出
                                    fadeOut(
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 600
                                        )
                                    )
                        },
                        label = "HeatmapAnimation"
                    ) { data ->
                        // AnimatedContent 的内容 lambda 会接收 targetState (这里是 data)
                        // 我们在这里放置 HeatmapChart
                        HeatmapChart(
                            data = data
                        )
                    }
                }
                // (可选) 如果数据为空，可以显示一个提示信息
                else {
                    Text("暂无数据")
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .defaultMinSize(minHeight = 250.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isKeyboardLoading) {
                    CircularProgressIndicator()
                } else if (uiState.keyboardData != null) {
                    AnimatedContent(
                        targetState = uiState.keyboardData,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(600)) togetherWith
                                    fadeOut(animationSpec = tween(600))
                        },
                        label = "KeyboardHeatmapAnimation"
                    ) { data ->

                            KeyboardHeatmapChart(
                                data = data,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                else {
                    Text("暂无键盘数据")
                }
            }
        }

    }


}

@Composable
fun BarChartExample(
    modifier: Modifier = Modifier,
    timeRangeIndex: Int
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val positiveColumn = rememberLineComponent(
        thickness = 8.dp,
        shape = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40)
    )

    // LaunchedEffect负责在timeRangeIndex变化时执行数据更新的副作用。
    // 这对于Compose的声明式UI范式是正确的做法。
    LaunchedEffect(timeRangeIndex) {
        modelProducer.runTransaction {
            when (timeRangeIndex) {
                0 -> columnSeries {
                    series(
                        20f,
                        40f,
                        30f,
                        50f,
                        70f,
                        40f,
                        20f,
                        40f,
                        50f,
                        40f,
                        23f,
                        34f
                    )
                }

                1 -> columnSeries { series(10f, 20f, 15f, 25f, 35f, 46f) }
                2 -> columnSeries { series(5f, 10f, 8f, 12f, 18f) }
                else -> columnSeries { series(30f, 50f, 40f, 60f, 80f) }
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = remember(positiveColumn) {
                    getPositiveColumnProvider(
                        positiveColumn
                    )
                }
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
        // 预览无状态组件更容易，只需提供一个模拟的State对象即可。
        MainScreenContent(
            uiState = MainUiState(selectTimeRangeIndex = 1, selectOsIndex = 2),
            onTimeRangeSelected = {}, // 在预览中，事件处理器可以为空
            onOsSelected = {}
        )
    }
}