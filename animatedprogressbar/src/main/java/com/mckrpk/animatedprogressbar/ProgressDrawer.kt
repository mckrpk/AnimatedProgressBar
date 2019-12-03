package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.max

internal class ProgressDrawer(val listener: DrawingView, val attrs: ViewProperties) {

    private lateinit var progressRect: RectF
    private lateinit var progressTipRect: RectF

    protected var progressAnimator: ValueAnimator? = null

    fun progressAnimation(progress: Float) {
        progressAnimator?.cancel()

        val endValX = attrs.drawRect.left + progress * attrs.drawRect.width()
        progressAnimator = ValueAnimator.ofFloat(attrs.drawRect.left, endValX).apply {
            duration = AnimatedProgressBar.ANIM_DURATION.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                progressRect.right = animation.animatedValue as Float
                if (attrs.progressTipEnabled) {
                    progressTipRect.right = animation.animatedValue as Float
                    progressTipRect.left = max(0f, progressTipRect.right - attrs.mProgressTipWidth)
                }
                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()
    }

    fun onViewReady() {
        this.attrs.drawRect = attrs.drawRect
        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.mLineWidth) / 2
        val lineBottomY = lineTopY + attrs.mLineWidth
        progressRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
        if (attrs.progressTipEnabled) {
            progressTipRect =
                RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.right, lineBottomY)
        }
    }

    fun draw(canvas: Canvas?) {
        canvas?.drawRoundRect(
            progressRect,
            attrs.mCornerRadius,
            attrs.mCornerRadius,
            attrs.progressPaint
        )
        if (attrs.progressTipEnabled) {
            canvas?.drawRect(progressTipRect, attrs.progressTipPaint)
        }
    }

}