package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import kotlin.math.max

internal class ProgressDrawer(
    private val listener: DrawingView,
    private val attrs: ViewProperties
) : ShapeDrawer() {

    private lateinit var progressRect: RectF
    private lateinit var trackRect: RectF
    private lateinit var progressTipRect: RectF
    private val clipPath: Path = Path()

    protected var progressAnimator: ValueAnimator? = null

    override fun onSizeChanged() {
        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.lineWidth) / 2
        val lineBottomY = lineTopY + attrs.lineWidth
        progressRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
        trackRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.right, lineBottomY)
        progressTipRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
        clipPath.addRoundRect(trackRect, attrs.cornerRadius, attrs.cornerRadius, Path.Direction.CW)
    }

    override fun startAnimation(targetProgress: Float) {
        progressAnimator?.cancel()

        val endValX = attrs.drawRect.left + targetProgress * attrs.drawRect.width()
        progressAnimator = ValueAnimator.ofFloat(attrs.drawRect.left, endValX).apply {
            duration = attrs.animDuration.toLong()
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                progressRect.right = animatedValue
                if (attrs.progressTipEnabled) {
                    progressTipRect.right = animatedValue
                    progressTipRect.left =
                        max(attrs.drawRect.left, progressTipRect.right - attrs.lineWidth)
                }
                listener.requestInvalidate()
            }
        }
        progressAnimator?.start()
    }

    override fun draw(canvas: Canvas) {
        val checkpoint = canvas.save()
        canvas.clipPath(clipPath)
        try {
            canvas.drawRoundRect(
                progressRect,
                attrs.cornerRadius,
                attrs.cornerRadius,
                attrs.progressPaint
            )
            if (attrs.progressTipEnabled) {
                canvas.drawRoundRect(
                    progressTipRect,
                    attrs.cornerRadius,
                    attrs.cornerRadius,
                    attrs.progressTipPaint
                )
            }
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    override fun cancel() {
        progressAnimator?.cancel()
    }

}