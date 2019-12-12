package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator

internal open class TrackDrawer(private val listener: DrawingView, val attrs: ViewProperties) :
    ShapeDrawer() {

    private lateinit var trackRect: RectF
    private var trackAnimator: ValueAnimator? = null

    override fun onSizeChanged() {
        val lineTopY = attrs.drawRect.top + (attrs.drawRect.height() - attrs.trackWidth) / 2
        val lineBottomY = lineTopY + attrs.trackWidth
        trackRect = RectF(attrs.drawRect.left, lineTopY, attrs.drawRect.left, lineBottomY)
    }

    override fun startAnimation(targetProgress: Float) {
        trackAnimator?.cancel()

        trackAnimator = ValueAnimator.ofFloat(attrs.drawRect.left, attrs.drawRect.right).apply {
            duration = attrs.animDuration.toLong() / 2
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener { animation ->
                trackRect.right = animation.animatedValue as Float
                listener.requestInvalidate()
            }
        }
        trackAnimator?.start()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(trackRect, attrs.cornerRadius, attrs.cornerRadius, attrs.trackPaint)
    }

    override fun cancel() {
        trackAnimator?.cancel()
    }

}