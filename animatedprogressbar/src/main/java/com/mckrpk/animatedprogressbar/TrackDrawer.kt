package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator

internal open class TrackDrawer(private val listener: DrawingView, val attrs: ViewProperties) {

    private lateinit var trackRect: RectF
    private var trackAnimator: ValueAnimator? = null

    fun onViewReady() {
        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.mLineWidth) / 2
        val lineBottomY = lineTopY + attrs.mLineWidth
        trackRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
    }

    fun trackAnimation() {
        trackAnimator?.cancel()

        trackAnimator = ValueAnimator.ofFloat(attrs.drawRect.left, attrs.drawRect.right).apply {
            duration = AnimatedProgressBar.ANIM_DURATION.toLong() / 2
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                trackRect.right = animation.animatedValue as Float
                listener.requestInvalidate()
            }
        }
        trackAnimator?.start()
    }

    fun draw(canvas: Canvas?) {
        canvas?.drawRoundRect(trackRect, attrs.mCornerRadius, attrs.mCornerRadius, attrs.trackPaint)
    }

}