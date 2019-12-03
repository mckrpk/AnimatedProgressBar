package com.mckrpk.animatedprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.min

class AnimatedProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs),
    DrawingView {

    private var mProgress: Float = 0f
    private var notDrawnYet = true

    private var lastDraw = System.currentTimeMillis()

    enum class ProgressStyle {
        SIMPLE,
        WAVE
    }

    //Elements to draw
    private var trackDrawer: TrackDrawer?
    private var progressDrawer: ProgressDrawer? = null
    private var waveDrawer: WaveDrawer? = null

    private var attributes: ViewProperties

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AnimatedProgressBar, 0, 0).apply {
            try {
                attributes = ViewProperties(this, context)

                trackDrawer = TrackDrawer(this@AnimatedProgressBar, attributes)
                if (ProgressStyle.WAVE == attributes.progressStyle) {
                    waveDrawer = WaveDrawer(this@AnimatedProgressBar, attributes)
                } else {
                    progressDrawer = ProgressDrawer(this@AnimatedProgressBar, attributes)
                }
            } finally {
                recycle()
            }
        }
    }

    fun setStyle(progressStyle: ProgressStyle) {
        if (attributes.progressStyle == progressStyle) {
            return
        }

        when (progressStyle) {
            ProgressStyle.SIMPLE -> {
                waveDrawer?.cancel()
                waveDrawer = null
//                progressDrawer = ProgressDrawer(this, this, context)
            }
            ProgressStyle.WAVE -> {

            }
        }
    }

    fun setProgress(progress: Float) {
        mProgress = progress
        notDrawnYet = true

        postInvalidate()
    }

    private fun startAnimation(progress: Float) {
        notDrawnYet = false
        trackDrawer?.trackAnimation()
        progressDrawer?.progressAnimation(progress)
        waveDrawer?.waveAnimation(progress)
    }

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
        attributes.drawRect = drawRect
        attributes.mLineWidth = min(drawRect.height(), attributes.mLineWidth)
        waveDrawer?.setDrawingRect(drawRect)
        trackDrawer?.onViewReady()
        progressDrawer?.onViewReady()

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val time = System.currentTimeMillis() - lastDraw
        Log.i("customView", "Previous draw: $time${if (time > 25) "<----------------" else ""}")
        if (notDrawnYet) {
            startAnimation(mProgress)
        }

        lastDraw = System.currentTimeMillis()

        trackDrawer?.draw(canvas)
        waveDrawer?.draw(canvas)
        progressDrawer?.draw(canvas)
    }


    companion object {
        val ANIM_DURATION = 1500
    }
}

internal interface DrawingView {
    fun requestInvalidate()
}
