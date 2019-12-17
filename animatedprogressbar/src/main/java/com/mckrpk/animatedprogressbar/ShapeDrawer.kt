package com.mckrpk.animatedprogressbar

import android.graphics.Canvas

internal abstract class ShapeDrawer {
    abstract fun onSizeChanged()
    /**
     * @targetProgress should be normalized to value from 0 to 1
     */
    abstract fun startAnimation(targetProgress: Float)
    abstract fun draw(canvas: Canvas)
    abstract fun cancel()

}