package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.max

internal class ProgressDrawer(
    private val listener: DrawingView,
    private val attrs: ViewProperties
) : ShapeDrawer() {

    private lateinit var progressRect: RectF
    private lateinit var progressTipRect: RectF

    protected var progressAnimator: ValueAnimator? = null

    override fun onSizeChanged() {
        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.trackWidth) / 2
        val lineBottomY = lineTopY + attrs.trackWidth
        progressRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
        progressTipRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.right, lineBottomY)
    }

    override fun startAnimation(targetProgress: Float) {
        progressAnimator?.cancel()

        val endValX = attrs.drawRect.left + targetProgress * attrs.drawRect.width()
        progressAnimator = ValueAnimator.ofFloat(attrs.drawRect.left, endValX).apply {
            duration = attrs.animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                progressRect.right = animation.animatedValue as Float
                if (attrs.progressTipEnabled) {
                    progressTipRect.right = animation.animatedValue as Float
                    progressTipRect.left = max(0f, progressTipRect.right - attrs.progressTipWidth)
                }
                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(
            progressRect,
            attrs.cornerRadius,
            attrs.cornerRadius,
            attrs.progressPaint
        )
        if (attrs.progressTipEnabled) {
            canvas.drawRect(progressTipRect, attrs.progressTipPaint)
        }
    }

    override fun cancel() {
        progressAnimator?.cancel()
    }

}