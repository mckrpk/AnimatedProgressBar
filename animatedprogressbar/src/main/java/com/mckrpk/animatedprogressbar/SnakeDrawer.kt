package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.*

internal class SnakeDrawer(private val listener: DrawingView, private val attrs: ViewProperties) :
    ShapeDrawer() {

    private var snakePath: Path = Path()
    private lateinit var snakeClipPath: Path
    private lateinit var snakeTipRect: RectF

    private val snakePoints = mutableListOf<MutablePoint>()
    private var progressAnimator: ValueAnimator? = null

    private var middleY: Float = 0f
    private var longLine = true

    override fun onSizeChanged() {
        middleY = (attrs.drawRect.top + attrs.drawRect.bottom) / 2
        attrs.snakePaint.strokeWidth = attrs.lineWidth

        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.lineWidth) / 2
        val lineBottomY = lineTopY + attrs.lineWidth

        snakeTipRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)

        val trackRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.right, lineBottomY)

        snakeClipPath = Path().apply {
            addRoundRect(trackRect, attrs.cornerRadius, attrs.cornerRadius, Path.Direction.CW)
        }
    }

    override fun startAnimation(targetProgress: Float) {
        val angleMax = 5f * PI.toFloat()
        val isInverse = Math.random() < 0.5
        val progressFactor = min(max(ln(targetProgress + 0.2) + 1.5, 0.0), 1.0).toFloat()
        val amplitudeMax = ((attrs.drawRect.height() / 2) - attrs.cornerRadius) * progressFactor
        val targetLineWidth = targetProgress * attrs.drawRect.width()
        var currentDrawProgress: Float

        longLine = targetLineWidth > attrs.lineWidth

        val progressRange = if (longLine) targetLineWidth - attrs.lineWidth else targetLineWidth
        val xCoordStart = attrs.drawRect.left + (attrs.cornerRadius * if (longLine) 1 else -1)

        progressAnimator?.cancel()
        snakePoints.clear()
        snakePath.reset()

        if (targetProgress == 0f) {
            return
        }

        progressAnimator = ValueAnimator.ofFloat(0f, angleMax).apply {
            duration = attrs.animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                snakePath.rewind()
                snakePath.moveTo(xCoordStart, middleY)

                val angle = animation.animatedValue as Float
                val animProgress = angle / angleMax

                snakePoints.forEachIndexed { index, mutablePoint ->
                    val positionFactor = index.toFloat() / snakePoints.size
                    snakePath.lineTo(
                        mutablePoint.x,
                        middleY + mutablePoint.y * (1 - animProgress) * positionFactor
                    )
                }

                currentDrawProgress = animProgress * progressRange

                val xCoord = xCoordStart + currentDrawProgress
                val sineValue = sin(if (isInverse) -1 * angle else angle)
                val yVal = sineValue * amplitudeMax
                val yCoord = middleY + yVal * (1 - animProgress)

                snakePoints.add(MutablePoint(xCoord, yVal))
                snakePath.lineTo(xCoord, yCoord)

                snakeTipRect.top = yCoord - attrs.cornerRadius
                snakeTipRect.bottom = yCoord + attrs.cornerRadius
                snakeTipRect.left = xCoord - attrs.cornerRadius
                snakeTipRect.right = xCoord + attrs.cornerRadius

                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()

    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        if (!longLine) {
            canvas.clipPath(snakeClipPath)
        }

        canvas.drawPath(snakePath, attrs.snakePaint)
        if (attrs.progressTipEnabled) {
            canvas.drawOval(snakeTipRect, attrs.progressTipPaint)
        }

        canvas.restore()
    }

    override fun cancel() {
        progressAnimator?.cancel()
    }


    class MutablePoint(var x: Float, var y: Float)
}