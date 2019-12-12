package com.mckrpk.animatedprogressbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import kotlin.math.min

internal class ViewProperties(attrs: TypedArray, context: Context) {

    var maxProgress: Float = 100f
    var progress: Float = 0f

    var animDuration: Int
    var progressTipEnabled: Boolean = false
    var progressStyle: AnimatedProgressBar.ProgressStyle = AnimatedProgressBar.ProgressStyle.SIMPLE

    var progressColor: Int = 0
        set(value) {
            field = value
            progressPaint.color = value
        }
    var trackColor: Int = 0
        set(value) {
            field = value
            trackPaint.color = value
        }
    var progressTipColor: Int = 0
        set(value) {
            field = value
            progressTipPaint.color = value
        }

    var trackWidth: Float = 0f
        set(value) {
            field = value
            cornerRadius = trackWidth / 2
            progressTipWidth = trackWidth
        }
    var cornerRadius: Float = 0f
    var progressTipWidth: Float = 0f

    var trackPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progressTipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var wavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var drawRect: RectF = RectF()
        set(value) {
            field = value
            trackWidth = min(drawRect.height(), trackWidth)
        }

    init {
        //PROGRESS
        maxProgress = attrs.getInt(R.styleable.AnimatedProgressBar_max, 100).toFloat()
        progress = attrs.getInt(R.styleable.AnimatedProgressBar_progress, 0).toFloat()

        //OTHERS
        val styleIndex = attrs.getInt(R.styleable.AnimatedProgressBar_animationStyle, 0)
        progressStyle = AnimatedProgressBar.ProgressStyle.values()[styleIndex]
        progressTipEnabled =
            attrs.getBoolean(R.styleable.AnimatedProgressBar_progressTipEnabled, true)

        animDuration = attrs.getInt(
            R.styleable.AnimatedProgressBar_animationDurationInMs,
            context.resources.getInteger(R.integer.animation_duration)
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

        progressTipWidth =
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_progress_tip_width)
                .toFloat()
        trackWidth = attrs.getDimensionPixelSize(
            R.styleable.AnimatedProgressBar_lineWidth,
            context.resources.getDimensionPixelSize(R.dimen.animated_progress_bar_line_width)
        ).toFloat()
        cornerRadius = trackWidth / 2
        progressTipWidth = trackWidth

        //PAINTS
        progressPaint.color = progressColor
        trackPaint.color = trackColor
        progressTipPaint.color = progressTipColor
        wavePaint.color = progressColor
        wavePaint.style = Paint.Style.STROKE
        wavePaint.strokeWidth = trackWidth
        wavePaint.strokeCap = Paint.Cap.ROUND
        wavePaint.strokeJoin = Paint.Join.ROUND
    }

    fun getProgressNormalized() = progress / maxProgress

}