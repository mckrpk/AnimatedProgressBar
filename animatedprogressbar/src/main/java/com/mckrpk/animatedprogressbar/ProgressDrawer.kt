package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.max

internal class ProgressDrawer(val listener: DrawingView, attrs: TypedArray, context: Context) {

    private var progressTipEnabled = true
    private var mProgressTipWidth: Float = 0f
    private var progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressTipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var progressRect: RectF
    private lateinit var progressTipRect: RectF
    private lateinit var drawRect: RectF

    protected var progressAnimator: ValueAnimator? = null

    private var mCornerRadius: Float = 0f
    private var mLineWidth: Float = 0f

    init {
        progressTipEnabled =
            attrs.getBoolean(R.styleable.AnimatedProgressBar_progressTipEnabled, true)
        val progressColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_progressColor,
            getThemePrimaryColor(context, R.color.defaultProgress)
        )
        progressPaint.color = progressColor
        if (progressTipEnabled) {
            val progressTipColor = attrs.getColor(
                R.styleable.AnimatedProgressBar_progressTipColor,
                getThemeAccentColor(context, R.color.defaultTip)
            )
            progressTipPaint.color = progressTipColor
        }
        android.R.drawable.progress_horizontal
        mProgressTipWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_progress_tip_width)
                .toFloat()
        mLineWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_line_width)
                .toFloat()
        mCornerRadius = mLineWidth / 2
    }

    fun progressAnimation(progress: Float) {
        progressAnimator?.cancel()

        val endValX = drawRect.left + progress * drawRect.width()
        progressAnimator = ValueAnimator.ofFloat(drawRect.left, endValX).apply {
            duration = AnimatedProgressBar.ANIM_DURATION.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                progressRect.right = animation.animatedValue as Float
                if (progressTipEnabled) {
                    progressTipRect.right = animation.animatedValue as Float
                    progressTipRect.left = max(0f, progressTipRect.right - mProgressTipWidth)
                }
                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()
    }


    fun setDrawingRect(drawRect: RectF) {
        this.drawRect = drawRect
        val lineTopY = drawRect.top + (drawRect.height() - mLineWidth) / 2
        val lineBottomY = lineTopY + mLineWidth
        progressRect = RectF(drawRect.left, lineTopY, drawRect.left, lineBottomY)
        if (progressTipEnabled) {
            progressTipRect = RectF(drawRect.left, lineTopY, drawRect.right, lineBottomY)
        }
    }

    fun draw(canvas: Canvas?) {
        canvas?.drawRoundRect(progressRect, mCornerRadius, mCornerRadius, progressPaint)
        if (progressTipEnabled) {
            canvas?.drawRect(progressTipRect, progressTipPaint)
        }
    }

}