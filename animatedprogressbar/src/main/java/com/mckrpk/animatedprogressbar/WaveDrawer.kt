package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

internal class WaveDrawer(private val listener: DrawingView, attrs: TypedArray, context: Context) {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var wavePath: Path = Path()
    private val wavePoints = mutableListOf<MutablePoint>()
    private var progressAnimator: ValueAnimator? = null

    private lateinit var drawRect: RectF
    private var middleY: Float = 0f

    private var mCornerRadius: Float = 0f

    private var mLineWidth: Float = 0f
    private var mProgressTipWidth: Float = 0f

    private var animDuration: Int

    init {
        val progressColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_progressColor,
            getThemePrimaryColor(context, R.color.defaultProgress)
        )
        val progressTipColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_progressTipColor,
            getThemeAccentColor(context, R.color.defaultTip)
        )

        animDuration = attrs.getInt(
            R.styleable.AnimatedProgressBar_animationDurationInMs,
            AnimatedProgressBar.ANIM_DURATION
        )

        mProgressTipWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_progress_tip_width)
                .toFloat()
        mLineWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_line_width)
                .toFloat()
        mCornerRadius = mLineWidth / 2

        paint.color = progressColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = mLineWidth
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun setDrawingRect(drawRect: RectF) {
        this.drawRect = drawRect
        middleY = (drawRect.top + drawRect.bottom) / 2
        mLineWidth = min(drawRect.height(), mLineWidth)
        paint.strokeWidth = mLineWidth
    }

    fun waveAnimation(progress: Float) {
        val angleMax = 4f * PI.toFloat()
        val isInverse = Math.random() < 0.5
        val amplitudeMax = drawRect.height() / 2

        progressAnimator?.cancel()
        wavePoints.clear()
        wavePath.reset()

        progressAnimator = ValueAnimator.ofFloat(0f, angleMax).apply {
            duration = animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                wavePath.rewind() //TODO check if rewind() it works
                wavePath.moveTo(drawRect.left, middleY)
                val angle = animation.animatedValue as Float
                val animProgress = angle / angleMax
                val amplitude = (1 - animProgress) * amplitudeMax
                val xCoord = drawRect.left + animProgress * drawRect.width() * progress
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
        canvas?.drawPath(wavePath, paint)
    }

    fun cancel() {
        progressAnimator?.cancel()
    }


    class MutablePoint(var x: Float, var y: Float)
}