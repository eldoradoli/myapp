package com.example.app

import android.content.Context
import android.graphics.*

object ChartHelper {
    fun createIntensityBarBitmap(
        context: Context,
        width: Int,
        height: Int,
        intensities: List<Int>,
        num: Float
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

        // 计算每个点的坐标
        val points = mutableListOf<PointF>()
        val segmentWidth = width / (intensities.size - 1).toFloat()

        for (index in intensities.indices) {
            val x = index * segmentWidth
            val y = height - (height * (intensities[index] / num))
            points.add(PointF(x, y))
        }

        // 绘制Catmull-Rom样条线
        val path = Path()
        path.moveTo(points[0].x, points[0].y)

        for (i in 0 until points.size - 1) {
            val p0 = points[if (i - 1 < 0) 0 else i - 1]
            val p1 = points[i]
            val p2 = points[if (i + 1 >= points.size) points.size - 1 else i + 1]
            val p3 = points[if (i + 2 >= points.size) points.size - 1 else i + 2]

            val controlX1 = p1.x + (p2.x - p0.x) / 6
            val controlY1 = p1.y + (p2.y - p0.y) / 6
            val controlX2 = p2.x - (p3.x - p1.x) / 6
            val controlY2 = p2.y - (p3.y - p1.y) / 6

            val gradientColors = listOf(
                Color.parseColor("#d1fdff"),
                Color.parseColor("#fddb92")
            )

            // 确定渐变颜色
            val startColor = getBezierGradientColor(
                gradientColors, intensities[i], num
            )
            val endColor = getBezierGradientColor(
                gradientColors, intensities[i + 1], num
            )

            val gradient = LinearGradient(
                p1.x, 0f, p2.x, 0f,
                startColor, endColor,
                Shader.TileMode.CLAMP
            )
            paint.shader = gradient

            // 填充渐变
            val segmentPath = Path()
            segmentPath.moveTo(p1.x, p1.y)
            segmentPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
            segmentPath.lineTo(p2.x, height.toFloat())
            segmentPath.lineTo(p1.x, height.toFloat())
            segmentPath.close()
            canvas.drawPath(segmentPath, paint)

            // 绘制折线
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.shader = gradient // 确保折线也有相同的渐变
            canvas.drawPath(segmentPath, paint)

            // 重置paint样式
            paint.style = Paint.Style.FILL
        }

//         绘制灰线
        val grayLinePaint = Paint().apply {
            color = Color.parseColor("#324b4c")
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        if (points.size > 3) {
            val p3 = points[2]
            val p4 = points[3]
            val midX34 = (p3.x + p4.x) / 2
            val startY34 = (p3.y + p4.y) / 2-2
            canvas.drawLine(midX34, startY34, midX34, height.toFloat(), grayLinePaint)
        }
        if (points.size > 9) {
            val p8 = points[7]
            val p9 = points[8]
            val midX89 = (p9.x + p8.x) / 2
            val startY89 = (p9.y + p8.y) / 2-2
            canvas.drawLine(midX89, startY89, midX89, height.toFloat(), grayLinePaint)
        }



        return bitmap
    }

    private fun getBezierGradientColor(colors: List<Int>, intensity: Int, num: Float): Int {
        val t = intensity / num
        val startColor = colors[0]
        val endColor = colors[1]
        val controlColor = getControlColor(startColor, endColor)

        val red = bezierInterpolate(
            Color.red(startColor),
            Color.red(controlColor),
            Color.red(endColor),
            t
        )
        val green = bezierInterpolate(
            Color.green(startColor),
            Color.green(controlColor),
            Color.green(endColor),
            t
        )
        val blue = bezierInterpolate(
            Color.blue(startColor),
            Color.blue(controlColor),
            Color.blue(endColor),
            t
        )
        return Color.rgb(red, green, blue)
    }

    private fun getControlColor(startColor: Int, endColor: Int): Int {
        // 简单的线性插值控制点，可以根据需求调整
        val red = (Color.red(startColor) + Color.red(endColor)) / 2
        val green = (Color.green(startColor) + Color.green(endColor)) / 2
        val blue = (Color.blue(startColor) + Color.blue(endColor)) / 2
        return Color.rgb(red, green, blue)
    }

    private fun bezierInterpolate(p0: Int, p1: Int, p2: Int, t: Float): Int {
        return ((1 - t) * (1 - t) * p0 + 2 * (1 - t) * t * p1 + t * t * p2).toInt()
    }
}



//package com.example.app
//
//import android.content.Context
//import android.graphics.*
//
//object ChartHelper {
//    fun createIntensityBarBitmap(
//        context: Context,
//        width: Int,
//        height: Int,
//        intensities: List<Int>,
//        cornerRadius: Float = 0f  // 新增的圆角参数，默认值为0
//    ): Bitmap {
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
//        val segmentWidth = width / intensities.size.toFloat()
//        val grayColor = Color.parseColor("#324b4c")
//
//        // 预定义渐变颜色
//        val gradientColors = listOf(
//            Color.parseColor("#fddb92"),
//            Color.parseColor("#d1fdff")
//
//        )
//
//        for (index in intensities.indices) {
//            val intensity = intensities[index]
//            val nextIntensity =
//                if (index + 1 < intensities.size) intensities[index + 1] else intensity
//
//            val left = index * segmentWidth
//            val right = left + segmentWidth + 1
//
//            if (intensity == 0) {
//                // 如果强度值为 0，则绘制纯灰色
//                paint.shader = null
//                paint.color = grayColor
//                val rect = RectF(left, 0f, right, height.toFloat())
//                if (index == 0) {
//                    drawLeftRoundRect(canvas, rect, cornerRadius, paint)
//                } else if (index == intensities.size - 1) {
//                    drawRightRoundRect(canvas, rect, cornerRadius, paint)
//                } else {
//                    canvas.drawRect(rect, paint)
//                }
//            } else {
//                // 获取当前颜色和下一个颜色
//                val currentColor = getBezierGradientColor(gradientColors, intensity)
//                val nextColor = getBezierGradientColor(gradientColors, nextIntensity)
//
//                if (index == 0) {
//                    // 绘制第一个段落的左侧纯色部分
//                    paint.shader = null
//                    paint.color = currentColor
//                    val rectLeft = RectF(left, 0f, left + segmentWidth / 2 + 1, height.toFloat())
//                    drawLeftRoundRect(canvas, rectLeft, cornerRadius, paint)
//
//                    // 绘制第一个段落的右侧渐变部分
//                    val gradient = LinearGradient(
//                        left + segmentWidth / 2, 0f, right, 0f,
//                        currentColor, nextColor,
//                        Shader.TileMode.CLAMP
//                    )
//                    paint.shader = gradient
//                    val rectRightGradient =
//                        RectF(left + segmentWidth / 2, 0f, right, height.toFloat())
//                    canvas.drawRect(rectRightGradient, paint)
//                } else if (index < intensities.size - 1) {
//                    // 绘制非最后一个段落，使用渐变
//                    val gradient = LinearGradient(
//                        left, 0f, right, 0f,
//                        currentColor, nextColor,
//                        Shader.TileMode.CLAMP
//                    )
//                    paint.shader = gradient
//                    val rect = RectF(left, 0f, right, height.toFloat())
//                    canvas.drawRect(rect, paint)
//                } else {
//                    // 绘制最后一个段落，左侧渐变，右侧不渐变
//                    val gradient = LinearGradient(
//                        left, 0f, left + segmentWidth / 2, 0f,
//                        currentColor, nextColor,
//                        Shader.TileMode.CLAMP
//                    )
//                    paint.shader = gradient
//                    val rectLeftGradient =
//                        RectF(left, 0f, left + segmentWidth / 2, height.toFloat())
//                    canvas.drawRect(rectLeftGradient, paint)
//
//                    // 绘制最后一个段落的右侧纯色部分
//                    paint.shader = null
//                    paint.color = nextColor
//                    val rectRightSolid = RectF(left + segmentWidth / 2, 0f, right, height.toFloat())
//                    drawRightRoundRect(canvas, rectRightSolid, cornerRadius, paint)
//                }
//            }
//        }
//
//        return bitmap
//    }
//
//    private fun getBezierGradientColor(colors: List<Int>, intensity: Int): Int {
//        val t = intensity / num
//        val startColor = colors[0]
//        val endColor = colors[1]
//        val controlColor = getControlColor(startColor, endColor)
//
//        val red = bezierInterpolate(
//            Color.red(startColor),
//            Color.red(controlColor),
//            Color.red(endColor),
//            t
//        )
//        val green = bezierInterpolate(
//            Color.green(startColor),
//            Color.green(controlColor),
//            Color.green(endColor),
//            t
//        )
//        val blue = bezierInterpolate(
//            Color.blue(startColor),
//            Color.blue(controlColor),
//            Color.blue(endColor),
//            t
//        )
//        return Color.rgb(red, green, blue)
//    }
//
//    private fun getControlColor(startColor: Int, endColor: Int): Int {
//        // 简单的线性插值控制点，可以根据需求调整
//        val red = (Color.red(startColor) + Color.red(endColor)) / 2
//        val green = (Color.green(startColor) + Color.green(endColor)) / 2
//        val blue = (Color.blue(startColor) + Color.blue(endColor)) / 2
//        return Color.rgb(red, green, blue)
//    }
//
//    private fun bezierInterpolate(p0: Int, p1: Int, p2: Int, t: Float): Int {
//        return ((1 - t) * (1 - t) * p0 + 2 * (1 - t) * t * p1 + t * t * p2).toInt()
//    }
//
//    private fun drawLeftRoundRect(
//        canvas: Canvas,
//        rect: RectF,
//        cornerRadius: Float,
//        paint: Paint
//    ) {
//        val path = Path()
//        path.addRoundRect(
//            rect,
//            floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius),
//            Path.Direction.CW
//        )
//        canvas.drawPath(path, paint)
//    }
//
//    private fun drawRightRoundRect(
//        canvas: Canvas,
//        rect: RectF,
//        cornerRadius: Float,
//        paint: Paint
//    ) {
//        val path = Path()
//        path.addRoundRect(
//            rect,
//            floatArrayOf(0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f),
//            Path.Direction.CW
//        )
//        canvas.drawPath(path, paint)
//    }
//}
