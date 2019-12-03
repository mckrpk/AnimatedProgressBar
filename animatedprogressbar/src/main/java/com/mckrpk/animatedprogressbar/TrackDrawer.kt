package com.mckrpk.animatedprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat

internal open class TrackDrawer(
    private val listener: DrawingView,
    attrs: TypedArray,
    context: Context
) {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var trackRect: RectF
    private lateinit var drawRect: RectF
    private var mCornerRadius: Float = 0f
    private var mLineWidth: Float = 0f
    private var trackAnimator: ValueAnimator? = null

    init {
        val trackColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_trackColor,
            ContextCompat.getColor(context, R.color.defaultTrack)
        )
        mLineWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_line_width)
                .toFloat()
        mCornerRadius = mLineWidth / 2
        paint.color = trackColor
    }

    fun setDrawingRect(drawRect: RectF) {
        this.drawRect = drawRect
        val lineTopY = drawRect.top + (drawRect.height() - mLineWidth) / 2
        val lineBottomY = lineTopY + mLineWidth
        trackRect = RectF(drawRect.left, lineTopY, drawRect.left, lineBottomY)
    }

    fun trackAnimation() {
        trackAnimator?.cancel()

        trackAnimator = ValueAnimator.ofFloat(drawRect.left, drawRect.right).apply {
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
        canvas?.drawRoundRect(trackRect, mCornerRadius, mCornerRadius, paint)
    }

}