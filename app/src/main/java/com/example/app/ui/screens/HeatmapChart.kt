 package com.example.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.app.charts.colorForScreenHeatmap

/**
 * 一个高性能的、无状态的屏幕空间热力图组件。
 * 它使用 Canvas API 来绘制，以确保即使在网格数据很大时也能保持流畅。
 * @param data 二维整数列表，代表热力图的数据。
 * @param modifier Modifier，用于控制布局。
 */
@Composable
fun HeatmapChart(
    data: List<List<Int>>,
    modifier: Modifier = Modifier
) {
    // 查找数据中的最大值，用于颜色映射。
    // `remember` 可以避免在每次重组时不必要地重新计算最大值。
    val maxValue = data.flatten().maxOrNull() ?: 0

    // Canvas 是一个 Composable，它提供了一个可以在其中进行2D绘制的作用域。
    Canvas(modifier = modifier.fillMaxSize()) {
        // `this.size` 提供了 Canvas 本身的宽度和高度。
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 如果没有数据或数据格式不正确，则不绘制任何内容。
        if (data.isEmpty() || data[0].isEmpty()) {
            return@Canvas
        }

        val gridHeight = data.size
        val gridWidth = data[0].size

        // 根据 Canvas 的尺寸和数据网格的尺寸，计算出每个单元格应该绘制多大。
        // 这确保了热力图总是能完整地、不滚动地填充整个可用空间。
        val cellWidth = canvasWidth / gridWidth
        val cellHeight = canvasHeight / gridHeight

        // 遍历二维数据列表
        data.forEachIndexed { y, row -> // y 是行索引
            row.forEachIndexed { x, value -> // x 是列索引, value 是热度值
                // 如果一个单元格的值是0，我们就不绘制它（因为颜色是透明的），这是一种性能优化。
                if (value == 0) return@forEachIndexed

                // 计算该单元格在画布上的左上角坐标。
                val topLeft = Offset(x * cellWidth, y * cellHeight)

                // 绘制一个矩形来代表这个单元格。
                drawRect(
                    color = colorForScreenHeatmap(value, maxValue),
                    topLeft = topLeft,
                    size = Size(cellWidth, cellHeight)
                )
            }
        }
    }
}