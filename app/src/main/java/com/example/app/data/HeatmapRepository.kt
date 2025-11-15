package com.example.app.data

import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random
import com.example.app.data.model.KeyData

object HeatmapRepository {

    suspend fun getHeatmapData(timeIndex: Int, osIndex: Int): List<List<Int>> {
//        delay(200L) // 模拟网络延迟

        // 定义热力图网格的分辨率，代表屏幕被分成了 120x80 个区域
        val gridWidth = 80 * 4
        val gridHeight = 45 * 4
        val data = MutableList(gridHeight) { MutableList(gridWidth) { 0 } }

        // 根据选择的索引，确定模拟点击的总次数和热区的集中程度
        val totalClicks = when (timeIndex) {
            0 -> 5000   // 24h
            1 -> 20000  // 7d
            2 -> 50000  // 1m
            else -> 10000
        }
        val hotspotStrength = when (osIndex) {
            0 -> 2.0 // win (更集中)
            1 -> 1.5 // linux (较集中)
            else -> 1.0 // all (较分散)
        }

        // 定义几个热区中心点 (x, y)
        val hotspots = listOf(
            Pair(gridWidth * 0.2, gridHeight * 0.3), // 左上
            Pair(gridWidth * 0.8, gridHeight * 0.7), // 右下
            Pair(gridWidth * 0.5, gridHeight * 0.5)  // 中心
        )

        // 模拟生成点击数据
        repeat(totalClicks) {
            val hotspot = hotspots.random() // 随机选一个热区

            // 使用数学公式生成向热区集中的随机点
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val radius = gridWidth * 2.0 * Random.nextDouble().pow(hotspotStrength)

            val x = (hotspot.first + radius * kotlin.math.cos(angle)).toInt()
            val y = (hotspot.second + radius * kotlin.math.sin(angle)).toInt()

            // 确保坐标在网格范围内，并增加该点的计数值
            if (x in 0 until gridWidth && y in 0 until gridHeight) {
                data[y][x]++
            }
        }

        return data
    }

    suspend fun getKeyboardHeatmapData(timeIndex: Int, osIndex: Int): Map<String, Int> {
//        delay(600L) // 模拟一个不同的网络延迟

        // 定义所有可能的按键
//        val allKeys = listOf(
//            "`","1","2","3","4","5","6","7","8","9","0","-","=","BACKSPACE",
//            "TAB","Q","W","E","R","T","Y","U","I","O","P","[","]","\\",
//            "CAPS","A","S","D","F","G","H","J","K","L",";","'","ENTER",
//            "L_SHIFT","Z","X","C","V","B","N","M",",",".","/","R_SHIFT",
//            "L_CTRL", "WIN", "L_ALT", "SPACE", "R_ALT", "FN", "R_CTRL"
//        )
        val allKeys = listOf(
            // Row 1
            "ESC", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
            "DEL", "HOME", "END", "PGUP", "PGDN",
            // Row 2
            "`","1","2","3","4","5","6","7","8","9","0","-","=","BACKSPACE",
            "NUMLOCK", "NUM_DIV", "NUM_MUL", "NUM_SUB",
            // Row 3
            "TAB","Q","W","E","R","T","Y","U","I","O","P","[","]","\\",
            "NUM_7", "NUM_8", "NUM_9", "NUM_ADD",
            // Row 4
            "CAPS","A","S","D","F","G","H","J","K","L",";","'","ENTER",
            "NUM_4", "NUM_5", "NUM_6",
            // Row 5
            "L_SHIFT","Z","X","C","V","B","N","M",",",".","/","R_SHIFT",
            "UP", "NUM_1", "NUM_2", "NUM_3", "NUM_ENTER",
            // Row 6
            "L_CTRL", "WIN", "L_ALT", "SPACE", "R_ALT", "FN", "R_CTRL",
            "LEFT", "DOWN", "RIGHT", "NUM_0", "NUM_DOT"
        )

        val commonKeys = setOf("E", "A", "S", "T", "I", "O", "N", "R", "H", "L", "SPACE", "ENTER")
        val osBias = if (osIndex == 0) setOf("W", "A", "S", "D", "L_SHIFT", "SPACE") else emptySet()

        val totalClicks = when (timeIndex) {
            0 -> 10000
            1 -> 50000
            2 -> 150000
            else -> 30000
        }


        val keyboardData = mutableMapOf<String, Int>()
        allKeys.forEach { keyboardData[it] = 0 }

        repeat(totalClicks) {
            val key = if (Random.nextInt(10) < 6) { // 60% 的点击落在常见键上
                commonKeys.random()
            } else if (Random.nextInt(10) < 2 && osBias.isNotEmpty()) { // 20% 落在系统偏好键上
                osBias.random()
            } else { // 剩余的随机分布
                allKeys.random()
            }
            keyboardData[key] = (keyboardData[key] ?: 0) + 1
        }
        return keyboardData
    }
}