package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.app.charts.colorForKeyValue

private data class KeyData(val text: String, val weight: Float = 1f, val id: String = text)

// ▼▼▼ 1. 布局定义 ▼▼▼
private val f_block1 = listOf(KeyData("F1"), KeyData("F2"), KeyData("F3"), KeyData("F4"))
private val f_block2 = listOf(KeyData("F5"), KeyData("F6"), KeyData("F7"), KeyData("F8"))
private val f_block3 = listOf(KeyData("F9"), KeyData("F10"), KeyData("F11"), KeyData("F12"))

private val nav_block = listOf( KeyData("Home", 1f, "HOME"), KeyData("End", 1f, "END"), KeyData("PgUp", 1f, "PGUP"), KeyData("PgDn", 1f, "PGDN"))

private val num_row = listOf(
    KeyData("`"), KeyData("1"), KeyData("2"), KeyData("3"), KeyData("4"), KeyData("5"),
    KeyData("6"), KeyData("7"), KeyData("8"), KeyData("9"), KeyData("0"), KeyData("-"),
    KeyData("="), KeyData("Backspace", 2f, "BACKSPACE")
)
private val numpad_top = listOf(KeyData("Num", 1f, "NUMLOCK"), KeyData("/", 1f, "NUM_DIV"), KeyData("*", 1f, "NUM_MUL"), KeyData("-", 1f, "NUM_SUB"))

private val tab_row = listOf(
    KeyData("Tab", 1.5f, "TAB"), KeyData("Q"), KeyData("W"), KeyData("E"), KeyData("R"),
    KeyData("T"), KeyData("Y"), KeyData("U"), KeyData("I"), KeyData("O"), KeyData("P"),
    KeyData("["), KeyData("]"), KeyData("\\", 1.5f)
)
private val numpad_789 = listOf(KeyData("7", 1f, "NUM_7"), KeyData("8", 1f, "NUM_8"), KeyData("9", 1f, "NUM_9"))

private val caps_row = listOf(
    KeyData("Caps", 1.75f, "CAPS"), KeyData("A"), KeyData("S"), KeyData("D"), KeyData("F"),
    KeyData("G"), KeyData("H"), KeyData("J"), KeyData("K"), KeyData("L"), KeyData(";", 1f, ";"),
    KeyData("'", 1f, "'"), KeyData("Enter", 2.25f, "ENTER")
)
private val numpad_456 = listOf(KeyData("4", 1f, "NUM_4"), KeyData("5", 1f, "NUM_5"), KeyData("6", 1f, "NUM_6"))

// ▼▼▼ 核心修改区域：为方向键腾出空间 ▼▼▼
// Shift 行被缩短
private val l_shift_row = listOf(KeyData("Shift", 2.25f, "L_SHIFT"), KeyData("Z"), KeyData("X"), KeyData("C"), KeyData("V"), KeyData("B"), KeyData("N"), KeyData("M"), KeyData(","), KeyData("."), KeyData("/"))
private val r_shift_key = KeyData("Shift", 1.85f, "R_SHIFT") // 调整右Shift宽度
// 底部行也被缩短
private val bottom_row_main = listOf(
    KeyData("Ctrl", 1.25f, "L_CTRL"), KeyData("Win", 1.25f, "WIN"),
    KeyData("Alt", 1.25f, "L_ALT"), KeyData("Space", 6.5f, "SPACE"),
    KeyData("Alt", 1f, "R_ALT"), KeyData("Fn", 1f, "FN"), KeyData("Ctrl", 1f, "R_CTRL")
)
// 独立定义方向键
private val arrow_up = listOf(KeyData("↑", 1f, "UP"))
private val arrow_ldr = listOf(KeyData("←", 1f, "LEFT"), KeyData("↓", 1f, "DOWN"), KeyData("→", 1f, "RIGHT"))
// 数字小键盘不再包含方向键
private val numpad_123 = listOf(KeyData("1", 1f, "NUM_1"), KeyData("2", 1f, "NUM_2"), KeyData("3", 1f, "NUM_3"))
private val numpad_0dot = listOf(KeyData("0", 1f, "NUM_0"), KeyData(".", 1f, "NUM_DOT"))

// Cross-row keys
private val numpad_add = KeyData("+", 1f, "NUM_ADD")
private val numpad_enter = KeyData("Enter", 1f, "NUM_ENTER")


@Composable
fun KeyboardHeatmapChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    BoxWithConstraints(modifier = modifier.fillMaxWidth().padding(4.dp)) {

        val u = maxWidth / (num_row.sumOf { it.weight.toDouble() }.toFloat() +
                numpad_top.sumOf { it.weight.toDouble() }.toFloat() + 0.5f +
                (num_row.size - 1 + numpad_top.size) * 0.02f)

        val keyHeight = u * 1f
        val keySpacing = u * 0.04f
        val f_group_gap = u * 0.295f
        val main_numpad_gap = u * 0.5f

        // ▼▼▼ 核心修改：将所有内容放到一个 Column 中，并用 Box 来处理覆盖 ▼▼▼
        Column(verticalArrangement = Arrangement.spacedBy(keySpacing)) {
            // --- Row 1 & 2 are independent and placed first ---
            Row {
                Key(KeyData("Esc", 1f, "ESC"), data, maxValue, u, keyHeight)
                Spacer(Modifier.width(f_group_gap))
                KeyboardRow(f_block1, data, maxValue, u, keyHeight, keySpacing)
                Spacer(Modifier.width(f_group_gap))
                KeyboardRow(f_block2, data, maxValue, u, keyHeight, keySpacing)
                Spacer(Modifier.width(f_group_gap))
                KeyboardRow(f_block3, data, maxValue, u, keyHeight, keySpacing)
                Spacer(Modifier.width(f_group_gap))
                Key(KeyData("Del", 1f, "DEL"), data, maxValue, u, keyHeight)
                Spacer(Modifier.weight(1f)) // Use weight here
                KeyboardRow(nav_block, data, maxValue, u, keyHeight, keySpacing)
            }
            Row {
                KeyboardRow(num_row, data, maxValue, u, keyHeight, keySpacing)
                Spacer(Modifier.weight(1f)) // Use weight here
                KeyboardRow(numpad_top, data, maxValue, u, keyHeight, keySpacing)
            }

            // --- Use a Box for the complex bottom part (Rows 3-6 + Arrows) ---
            Box {
                // --- Background Layer: Main keys and Numpad keys ---
                Row {
                    // Main cluster (left side)
                    Column(verticalArrangement = Arrangement.spacedBy(keySpacing)) {
                        KeyboardRow(tab_row, data, maxValue, u, keyHeight, keySpacing)
                        KeyboardRow(caps_row, data, maxValue, u, keyHeight, keySpacing)
                        Row(horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
                            KeyboardRow(l_shift_row, data, maxValue, u, keyHeight, keySpacing)
                            Key(r_shift_key, data, maxValue, u, keyHeight)
                        }
                        KeyboardRow(bottom_row_main, data, maxValue, u, keyHeight, keySpacing)
                    }

                    Spacer(Modifier.weight(1f))

                    // Numpad cluster (right side) with cross-row key overlay
                    Box {
                        Column(verticalArrangement = Arrangement.spacedBy(keySpacing)) {
                            KeyboardRow(numpad_789, data, maxValue, u, keyHeight, keySpacing)
                            KeyboardRow(numpad_456, data, maxValue, u, keyHeight, keySpacing)
                            KeyboardRow(numpad_123, data, maxValue, u, keyHeight, keySpacing)
                            Row {
                                Spacer(Modifier.width(u * 1f + keySpacing))
                                KeyboardRow(numpad_0dot, data, maxValue, u, keyHeight, keySpacing)
                            }
                        }
                        Row {
                            Spacer(Modifier.width(u * 3 + keySpacing * 3))
                            Column(verticalArrangement = Arrangement.spacedBy(keySpacing)) {
                                Key(numpad_add, data, maxValue, u, keyHeight * 2 + keySpacing)
                                Key(numpad_enter, data, maxValue, u, keyHeight * 2 + keySpacing)
                            }
                        }
                    }
                }

                // --- Overlay Layer: Arrow Keys ---
                Row(modifier = Modifier.fillMaxSize()) { // Fill the Box
                    // Spacer to push arrows horizontally
                    val totalWidthBeforeArrows = bottom_row_main.sumOf { it.weight.toDouble() }.toFloat() * u + (bottom_row_main.size) * keySpacing + u *0.05f
                    Spacer(modifier = Modifier.width(totalWidthBeforeArrows))

                    // We are now at the correct horizontal position.
                    // The vertical position is handled by the parent Column's arrangement.
                    // This Column IS the arrow block.
                    Column(
                        modifier = Modifier.padding(top = keyHeight * 2 + keySpacing * 2+u*0.125f), // Push down 2 rows
                        verticalArrangement = Arrangement.spacedBy(keySpacing)
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(u*1.045f))
                            KeyboardRow(arrow_up, data, maxValue, u, keyHeight, keySpacing)
                        }
                        KeyboardRow(arrow_ldr, data, maxValue, u, keyHeight, keySpacing)
                    }
                }
            }
        }
    }
}


// ▼▼▼ 5. 辅助 Composable ▼▼▼
@Composable
private fun KeyboardRow(
    keys: List<KeyData>,
    data: Map<String, Int>,
    maxValue: Int,
    u: Dp,
    keyHeight: Dp,
    spacing: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        keys.forEach { keyData ->
            Key(keyData, data, maxValue, u, keyHeight)
        }
    }
}

@Composable
private fun Key(
    keyData: KeyData,
    data: Map<String, Int>,
    maxValue: Int,
    u: Dp,
    keyHeight: Dp
) {
    val clickCount = data[keyData.id] ?: 0
    Box(
        modifier = Modifier
            .width(u * keyData.weight)
            .height(keyHeight)
            .clip(RoundedCornerShape(6.dp))
            .background(colorForKeyValue(clickCount, maxValue)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = keyData.text,
            color = Color.White,
            fontSize = (u.value * 0.4f).sp, // 字体大小也与u关联
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}