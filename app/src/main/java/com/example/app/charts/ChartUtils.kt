package com.example.app.charts

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.compose.common.fill

//计算渐变色
fun getColorForValue(value: Float, minValue: Float = 0f, maxValue: Float = 100f): Color {
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
//自定义柱状图
fun getPositiveColumnProvider(positiveColumn: LineComponent): ColumnCartesianLayer.ColumnProvider {
    return object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore
        ): LineComponent {
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
//热力图
fun colorForScreenHeatmap(value: Int, maxValue: Int): Color {
    if (value == 0) return Color.Transparent // 0表示没有点击，是透明的

    val fraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)

    return when {
        fraction < 0.25f -> lerp(Color.Blue, Color.Cyan, fraction / 0.25f)
        fraction < 0.5f -> lerp(Color.Cyan, Color.Green, (fraction - 0.25f) / 0.25f)
        fraction < 0.75f -> lerp(Color.Green, Color.Yellow, (fraction - 0.5f) / 0.25f)
        else -> lerp(Color.Yellow, Color.Red, (fraction - 0.75f) / 0.25f)
    }
}
//键盘热力图
fun colorForKeyValue(value: Int, maxValue: Int): Color {
    // 这里的 value 是一个 Int，所以可以和 0 比较
    if (value == 0) return Color(0xFF444444) // 未点击的按键给个深灰色

    // 这里的 value 是一个 Int，所以可以调用 toFloat()
    val fraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)

    // 使用与屏幕热力图相似的"蓝->红"渐变
    return when {
        fraction < 0.25f -> lerp(Color(0xFF0000FF), Color(0xFF00FFFF), fraction / 0.25f) // Blue -> Cyan
        fraction < 0.5f -> lerp(Color(0xFF00FFFF), Color(0xFF00FF00), (fraction - 0.25f) / 0.25f) // Cyan -> Green
        fraction < 0.75f -> lerp(Color(0xFF00FF00), Color(0xFFFFFF00), (fraction - 0.5f) / 0.25f) // Green -> Yellow
        else -> lerp(Color(0xFFFFFF00), Color(0xFFFF0000), (fraction - 0.75f) / 0.25f) // Yellow -> Red
    }
}