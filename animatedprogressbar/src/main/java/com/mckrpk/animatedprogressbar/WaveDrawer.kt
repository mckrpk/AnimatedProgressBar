package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.sin

internal class WaveDrawer(private val listener: DrawingView, val attrs: ViewProperties) {

    private var wavePath: Path = Path()
    private val wavePoints = mutableListOf<MutablePoint>()
    private var progressAnimator: ValueAnimator? = null

    private var middleY: Float = 0f

    fun setDrawingRect(drawRect: RectF) {
        middleY = (drawRect.top + drawRect.bottom) / 2
        attrs.wavePaint.strokeWidth = attrs.mLineWidth
    }

    fun waveAnimation(progress: Float) {
        val angleMax = 4f * PI.toFloat()
        val isInverse = Math.random() < 0.5
        val amplitudeMax = attrs.drawRect.height() / 2

        progressAnimator?.cancel()
        wavePoints.clear()
        wavePath.reset()

        progressAnimator = ValueAnimator.ofFloat(0f, angleMax).apply {
            duration = attrs.animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                wavePath.rewind() //TODO check if rewind() it works
                wavePath.moveTo(attrs.drawRect.left, middleY)
                val angle = animation.animatedValue as Float
                val animProgress = angle / angleMax
                val amplitude = (1 - animProgress) * amplitudeMax
                val xCoord = attrs.drawRect.left + animProgress * attrs.drawRect.width() * progress
                val sineValue = sin(if (isInverse) -1 * angle else angle)
                val yVal = sineValue * amplitudeMax

                wavePoints.forEachIndexed { index, mutablePoint ->
                    val positionFactor = index.toFloat() / wavePoints.size
                    wavePath.lineTo(
                        mutablePoint.x,
                        middleY + mutablePoint.y * (1 - animProgress) * positionFactor
                    )
                }
                wavePoints.add(
                    MutablePoint(
                        xCoord,
                        yVal
                    )
                )
                wavePath.lineTo(xCoord, middleY + yVal * (1 - animProgress))

                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()
    }

    fun draw(canvas: Canvas?) {
        canvas?.drawPath(wavePath, attrs.wavePaint)
    }

    fun cancel() {
        progressAnimator?.cancel()
    }


    class MutablePoint(var x: Float, var y: Float)
}