package com.kc123.appmanager.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class PatternGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }

    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#98cb00")
        strokeWidth = 12f
        style = Paint.Style.STROKE
    }

    private val ringPaint = Paint().apply {
        color = Color.parseColor("#98cb00")
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }


    private val selectedDots = mutableListOf<Pair<Int, Int>>()

    private val dotRadius = 15f
    private val gridSize = 3

    private var spacingX = 0f
    private var spacingY = 0f

    private var lastX = 0f
    private var lastY = 0f
    private var isDrawing = false

    var onPatternDrawn: ((List<Pair<Int, Int>>) -> Unit)? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Use the smaller dimension to keep grid square
        val size = minOf(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)

        spacingX = size / gridSize.toFloat()
        spacingY = size / gridSize.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dotPaint.alpha = if (isEnabled) 255 else 100 // 100 = ~40% opacity

        // Draw dots
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cx = col * spacingX + spacingX / 2f
                val cy = row * spacingY + spacingY / 2f

                canvas.drawCircle(cx, cy, dotRadius, dotPaint)

                if (selectedDots.contains(Pair(row, col))) {
                    canvas.drawCircle(cx, cy, dotRadius + 40f, ringPaint) // glow ring
                }
            }
        }

        // Draw connecting path
        if (selectedDots.isNotEmpty()) {
            val path = Path()
            selectedDots.forEachIndexed { index, (row, col) ->
                val cx = col * spacingX + spacingX / 2f
                val cy = row * spacingY + spacingY / 2f
                if (index == 0) path.moveTo(cx, cy)
                else path.lineTo(cx, cy)
            }
            if (isDrawing) path.lineTo(lastX, lastY)
            canvas.drawPath(path, pathPaint)
        }
    }

    private fun handleTouch(x: Float, y: Float) {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cx = col * spacingX + spacingX / 2f
                val cy = row * spacingY + spacingY / 2f
                val dx = x - cx
                val dy = y - cy
                val distance = sqrt(dx * dx + dy * dy)

                val dot = Pair(row, col)
                if (distance < dotRadius * 3 && !selectedDots.contains(dot)) {
                    selectedDots.add(dot)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                selectedDots.clear()
                handleTouch(x, y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                lastX = x
                lastY = y
                handleTouch(x, y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                invalidate()
                onPatternDrawn?.invoke(selectedDots.toList())
                return true
            }
        }
        return false
    }

    fun reset() {
        selectedDots.clear()
        invalidate()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidate() // Redraw view with new alpha
    }

    fun setPattern(pattern: List<Pair<Int, Int>>) {
        selectedDots.clear()
        selectedDots.addAll(pattern)
        invalidate()
    }



}
