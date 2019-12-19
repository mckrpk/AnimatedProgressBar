package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.*

internal class WaveDrawer(private val listener: DrawingView, private val attrs: ViewProperties) :
    ShapeDrawer() {

    private var wavePath: Path = Path()
    private lateinit var waveClipPath: Path
    private lateinit var waveTipRect: RectF

    private val wavePoints = mutableListOf<MutablePoint>()
    private var progressAnimator: ValueAnimator? = null

    private var middleY: Float = 0f
    private var longLine = true

    override fun onSizeChanged() {
        middleY = (attrs.drawRect.top + attrs.drawRect.bottom) / 2
        attrs.wavePaint.strokeWidth = attrs.lineWidth

        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.lineWidth) / 2
        val lineBottomY = lineTopY + attrs.lineWidth

        waveTipRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)

        val trackRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.right, lineBottomY)

        waveClipPath = Path().apply {
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
        wavePoints.clear()
        wavePath.reset()

        if (targetProgress == 0f) {
            return
        }

        progressAnimator = ValueAnimator.ofFloat(0f, angleMax).apply {
            duration = attrs.animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                wavePath.rewind()
                wavePath.moveTo(xCoordStart, middleY)

                val angle = animation.animatedValue as Float
                val animProgress = angle / angleMax

                wavePoints.forEachIndexed { index, mutablePoint ->
                    val positionFactor = index.toFloat() / wavePoints.size
                    wavePath.lineTo(
                        mutablePoint.x,
                        middleY + mutablePoint.y * (1 - animProgress) * positionFactor
                    )
                }

                currentDrawProgress = animProgress * progressRange

                val xCoord = xCoordStart + currentDrawProgress
                val sineValue = sin(if (isInverse) -1 * angle else angle)
                val yVal = sineValue * amplitudeMax
                val yCoord = middleY + yVal * (1 - animProgress)

                wavePoints.add(MutablePoint(xCoord, yVal))
                wavePath.lineTo(xCoord, yCoord)

                waveTipRect.top = yCoord - attrs.cornerRadius
                waveTipRect.bottom = yCoord + attrs.cornerRadius
                waveTipRect.left = xCoord - attrs.cornerRadius
                waveTipRect.right = xCoord + attrs.cornerRadius

                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()

    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        if (!longLine) {
            canvas.clipPath(waveClipPath)
        }

        canvas.drawPath(wavePath, attrs.wavePaint)
        if (attrs.progressTipEnabled) {
            canvas.drawOval(waveTipRect, attrs.progressTipPaint)
        }

        canvas.restore()
    }

    override fun cancel() {
        progressAnimator?.cancel()
    }


    class MutablePoint(var x: Float, var y: Float)
}