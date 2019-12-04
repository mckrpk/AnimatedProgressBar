package com.mckrpk.animatedprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View

class AnimatedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), DrawingView {

    private var shouldStartAnimation = true

    private var lastDraw = System.currentTimeMillis()

    //Elements to draw
    private var trackDrawer: TrackDrawer?
    private var progressDrawer: ProgressDrawer? = null
    private var waveDrawer: WaveDrawer? = null

    private var attrs: ViewProperties

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AnimatedProgressBar, 0, 0).apply {
            try {
                this@AnimatedProgressBar.attrs = ViewProperties(this, context)
            } finally {
                recycle()
            }
        }
        trackDrawer = TrackDrawer(this, this.attrs)
        if (ProgressStyle.WAVE == this.attrs.progressStyle) {
            waveDrawer = WaveDrawer(this, this.attrs)
        } else {
            progressDrawer = ProgressDrawer(this, this.attrs)
        }
    }

    fun setProgressColor(color: Int) {
        attrs.progressColor = color
        postInvalidate()
    }

    fun setProgressTipColor(color: Int) {
        attrs.progressTipColor = color
        postInvalidate()
    }

    fun setTrackColor(color: Int) {
        attrs.trackColor = color
        postInvalidate()
    }

    fun setAnimDuration(duration: Int) {
        attrs.animDuration = duration
        postInvalidate()
    }

    fun setProgressTipEnabled(enabled: Boolean) {
        attrs.progressTipEnabled = enabled
        postInvalidate()
    }

    fun setProgressStyle(progressStyle: ProgressStyle) {
        if (attrs.progressStyle == progressStyle) {
            return
        }
        attrs.progressStyle = progressStyle
        if (ProgressStyle.WAVE == this.attrs.progressStyle) {
            progressDrawer?.cancel()
            waveDrawer = WaveDrawer(this, this.attrs)
        } else {
            waveDrawer?.cancel()
            progressDrawer = ProgressDrawer(this, this.attrs)

        }
        postInvalidate()
    }

    fun setTrackWidth(width: Float) {
        attrs.trackWidth = width

        trackDrawer?.onSizeChanged()
        progressDrawer?.onSizeChanged()
        waveDrawer?.onSizeChanged()
        postInvalidate()
    }

    fun setProgressTipWidth(width: Float) {
        attrs.progressTipWidth = width
        progressDrawer?.onSizeChanged()
        postInvalidate()
    }

    fun setProgress(progress: Float) {
        setProgress(progress, true)
    }

    fun setProgress(progress: Float, animate: Boolean) {
        attrs.progress = progress
        shouldStartAnimation = animate
        postInvalidate()
    }

    fun setMax(maxProgress: Float) {
        attrs.maxProgress = maxProgress
    }

    private fun startAnimation() {
        shouldStartAnimation = false
        trackDrawer?.startAnimation(attrs.getProgressNormalized())
        progressDrawer?.startAnimation(attrs.getProgressNormalized())
        waveDrawer?.startAnimation(attrs.getProgressNormalized())
    }

    //TODO hide from outside
    override fun requestInvalidate() {
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val mDrawStartPosX = paddingLeft.toFloat()
        val mDrawEndPosX = (w - paddingRight).toFloat()
        val mDrawStartPosY = paddingTop.toFloat()
        val mDrawEndPosY = (h - paddingBottom).toFloat()

        val drawRect = RectF(mDrawStartPosX, mDrawStartPosY, mDrawEndPosX, mDrawEndPosY)
        attrs.drawRect = drawRect
        waveDrawer?.onSizeChanged()
        trackDrawer?.onSizeChanged()
        progressDrawer?.onSizeChanged()

        Log.i("customView", "onSizeChanged: $oldw -> $w, $oldh -> $h")
    }

    //Method doesn't need to be overridden if we don't have particular requirements
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("customView", "onMeasure")
        // MeasureSpecs contain size and specs that can be obtained in the following way:
        // val specMode = View.MeasureSpec.getMode(widthMeasureSpec)
        // val specSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val minHeight =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_min_height)

        //Remember to take paddings into account
        val minw = paddingLeft + suggestedMinimumWidth + paddingRight
        val minh = paddingTop + minHeight + paddingBottom

        //Call this to get final width and height with respect of measure spec
        val w = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h = resolveSizeAndState(minh, heightMeasureSpec, 0)

        //This call is required
        setMeasuredDimension(w, h)
    }

    override fun onAttachedToWindow() {
        Log.i("customView", "onAttachedToWindow")
        super.onAttachedToWindow()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("customView", "onLayout")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val time = System.currentTimeMillis() - lastDraw
//        Log.i("customView", "Previous draw: $time${if (time > 25) "<----------------" else ""}")
        if (shouldStartAnimation) {
            startAnimation()
            Log.i("customView", "Starting animation")
        }

        lastDraw = System.currentTimeMillis()

        trackDrawer?.draw(canvas)
        waveDrawer?.draw(canvas)
        progressDrawer?.draw(canvas)
    }

    enum class ProgressStyle {
        SIMPLE,
        WAVE
    }
}

internal interface DrawingView {
    fun requestInvalidate()
}
