package com.example.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

object BitmapHelper {

    fun createTextBitmap(
        context: Context,
        text: String,
        fontResId: Int,
        textSize: Float,
        textColor: Int,
        letterSpacing: Float = 0f,
        fontWeight: Int = Typeface.NORMAL
    ): Bitmap {
        val typeface = Typeface.create(context.resources.getFont(fontResId), fontWeight)
        val paint = Paint().apply {
            this.typeface = typeface
            this.textSize = textSize
            this.color = textColor
            this.isAntiAlias = true
            this.letterSpacing = letterSpacing
        }

        val width = (paint.measureText(text) + letterSpacing * text.length).toInt()
        val height = (paint.descent() - paint.ascent()).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, -paint.ascent(), paint)

        return bitmap
    }
}
