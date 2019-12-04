package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.sin

internal class WaveDrawer(private val listener: DrawingView, val attrs: ViewProperties) :
    ShapeDrawer() {

    private var wavePath: Path = Path()
    private val wavePoints = mutableListOf<MutablePoint>()
    private var progressAnimator: ValueAnimator? = null

    private var middleY: Float = 0f

    override fun onSizeChanged() {
        middleY = (attrs.drawRect.top + attrs.drawRect.bottom) / 2
        attrs.wavePaint.strokeWidth = attrs.trackWidth
    }

    override fun startAnimation(targetProgress: Float) {
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
                wavePath.rewind()
                wavePath.moveTo(attrs.drawRect.left, middleY)
                val angle = animation.animatedValue as Float
                val animProgress = angle / angleMax
                val amplitude = (1 - animProgress) * amplitudeMax
                val xCoord =
                    attrs.drawRect.left + animProgress * attrs.drawRect.width() * targetProgress
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

    override fun draw(canvas: Canvas) {
        canvas.drawPath(wavePath, attrs.wavePaint)
    }

    override fun cancel() {
        progressAnimator?.cancel()
    }


    class MutablePoint(var x: Float, var y: Float)
}