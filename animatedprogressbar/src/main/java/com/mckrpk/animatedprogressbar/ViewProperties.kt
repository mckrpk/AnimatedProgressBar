package com.mckrpk.animatedprogressbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat

internal class ViewProperties(attrs: TypedArray, context: Context) {

    var progressColor: Int = 0
    var trackColor: Int = 0
    var progressTipColor: Int = 0

    var animDuration: Int
    var progressTipEnabled: Boolean = false
    var progressStyle: AnimatedProgressBar.ProgressStyle = AnimatedProgressBar.ProgressStyle.SIMPLE

    var mCornerRadius: Float = 0f
    var mLineWidth: Float = 0f
    var mProgressTipWidth: Float = 0f

    var trackPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progressTipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var wavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var drawRect: RectF = RectF()

    init {
        //OTHERS
        val styleIndex = attrs.getInt(R.styleable.AnimatedProgressBar_animationStyle, 0)
        progressStyle = AnimatedProgressBar.ProgressStyle.values()[styleIndex]
        progressTipEnabled =
            attrs.getBoolean(R.styleable.AnimatedProgressBar_progressTipEnabled, true)

        animDuration = attrs.getInt(
            R.styleable.AnimatedProgressBar_animationDurationInMs,
            AnimatedProgressBar.ANIM_DURATION
        )

        //COLORS
        progressTipColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_progressTipColor,
            getThemeAccentColor(context, R.color.defaultTip)
        )
        progressColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_progressColor,
            getThemePrimaryColor(context, R.color.defaultProgress)
        )
        trackColor = attrs.getColor(
            R.styleable.AnimatedProgressBar_trackColor,
            ContextCompat.getColor(context, R.color.defaultTrack)
        )

        mProgressTipWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_progress_tip_width)
                .toFloat()
        mLineWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_line_width)
                .toFloat()
        mCornerRadius = mLineWidth / 2

        //PAINTS
        progressPaint.color = progressColor
        trackPaint.color = trackColor
        progressTipPaint.color = progressTipColor
        wavePaint.color = progressColor
        wavePaint.style = Paint.Style.STROKE
        wavePaint.strokeWidth = mLineWidth
        wavePaint.strokeCap = Paint.Cap.ROUND
    }
}