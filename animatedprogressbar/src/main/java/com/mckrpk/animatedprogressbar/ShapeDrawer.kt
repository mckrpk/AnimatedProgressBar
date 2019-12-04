package com.mckrpk.animatedprogressbar

import android.graphics.Canvas

internal abstract class ShapeDrawer {
    abstract fun onSizeChanged()
    abstract fun startAnimation(targetProgress: Float)
    abstract fun draw(canvas: Canvas)
    abstract fun cancel()

}