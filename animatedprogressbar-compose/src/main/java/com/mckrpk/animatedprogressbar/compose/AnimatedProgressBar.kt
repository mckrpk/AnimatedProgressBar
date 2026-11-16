package com.mckrpk.animatedprogressbar.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.mckrpk.animatedprogressbar.R
import com.mckrpk.animatedprogressbar.getThemeAccentColor
import com.mckrpk.animatedprogressbar.getThemePrimaryColor
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import com.mckrpk.animatedprogressbar.AnimatedProgressBar as AnimatedProgressBarView

/**
 * Jetpack Compose version of [com.mckrpk.animatedprogressbar.AnimatedProgressBar].
 *
 * Every change of [progress], [max], [style] or [animationDurationMillis] replays the
 * animation from the start, the same way `setProgress()` does in the View version.
 * The bar fills the available width, like `match_parent` in XML, and is
 * `max(32.dp, 3 * lineWidth)` high unless [modifier] sets another size.
 *
 * @param progress current progress, from 0 to [max]
 * @param max maximum progress
 * @param style [AnimatedProgressBarView.Style.LINE] or [AnimatedProgressBarView.Style.SNAKE]
 * @param lineWidth thickness of the track and the progress line
 */
@Composable
fun AnimatedProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    max: Int = 100,
    style: AnimatedProgressBarView.Style = AnimatedProgressBarView.Style.LINE,
    trackColor: Color = AnimatedProgressBarDefaults.trackColor(),
    progressColor: Color = AnimatedProgressBarDefaults.progressColor(),
    progressTipColor: Color = AnimatedProgressBarDefaults.progressTipColor(),
    progressTipEnabled: Boolean = true,
    lineWidth: Dp = AnimatedProgressBarDefaults.lineWidth(),
    animationDurationMillis: Int = AnimatedProgressBarDefaults.animationDurationMillis(),
) {
    val targetProgress = if (max > 0) (progress.toFloat() / max).coerceIn(0f, 1f) else 0f

    // Time fraction of the animation; easing is applied per element while drawing
    val animTime = remember { Animatable(0f) }
    val snakeInverse = remember(targetProgress, style) { Random.nextBoolean() }
    LaunchedEffect(targetProgress, style, animationDurationMillis) {
        animTime.snapTo(0f)
        animTime.animateTo(1f, tween(animationDurationMillis, easing = LinearEasing))
    }

    val snakePath = remember { Path() }
    // Like wrap_content of the View: a size set through [modifier] takes precedence
    val defaultHeight = maxOf(AnimatedProgressBarDefaults.minHeight(), lineWidth * 3)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(defaultHeight)
            .semantics {
                progressBarRangeInfo =
                    ProgressBarRangeInfo(progress.toFloat(), 0f..max.coerceAtLeast(0).toFloat())
            }
    ) {
        val lineWidthPx = min(lineWidth.toPx(), size.height)
        val time = animTime.value

        drawTrack(lineWidthPx, Decelerate.transform(min(1f, time * 2)), trackColor)
        if (style == AnimatedProgressBarView.Style.SNAKE) {
            drawSnake(
                snakePath, lineWidthPx, targetProgress, time, snakeInverse,
                progressColor, progressTipColor, progressTipEnabled
            )
        } else {
            drawProgressLine(
                lineWidthPx, targetProgress * Decelerate.transform(time),
                progressColor, progressTipColor, progressTipEnabled
            )
        }
    }
}

object AnimatedProgressBarDefaults {
    @Composable
    fun trackColor(): Color = colorResource(R.color.defaultTrack)

    /** Theme's `colorPrimary`, like in the View version. */
    @Composable
    fun progressColor(): Color =
        Color(getThemePrimaryColor(LocalContext.current, R.color.defaultProgress))

    /** Theme's `colorAccent`, like in the View version. */
    @Composable
    fun progressTipColor(): Color =
        Color(getThemeAccentColor(LocalContext.current, R.color.defaultTip))

    @Composable
    fun lineWidth(): Dp = dimensionResource(R.dimen.animated_progress_bar_line_width)

    @Composable
    fun animationDurationMillis(): Int = integerResource(R.integer.animation_duration)

    @Composable
    internal fun minHeight(): Dp = dimensionResource(R.dimen.animated_progress_bar_min_height)
}

// Same curve as DecelerateInterpolator(1f) used by the View drawers
private val Decelerate = Easing { 1f - (1f - it) * (1f - it) }

private const val SNAKE_TRAIL_SAMPLES = 120

private fun DrawScope.lineRect(lineWidth: Float, right: Float): Rect {
    val top = (size.height - lineWidth) / 2
    return Rect(0f, top, right, top + lineWidth)
}

/** Mirrors TrackDrawer: the track grows from left to right in half of the duration. */
private fun DrawScope.drawTrack(lineWidth: Float, fraction: Float, color: Color) {
    val rect = lineRect(lineWidth, fraction * size.width)
    drawRoundRect(color, rect.topLeft, rect.size, CornerRadius(lineWidth / 2))
}

/** Mirrors ProgressDrawer: a rounded bar clipped to the track, with an optional tip. */
private fun DrawScope.drawProgressLine(
    lineWidth: Float,
    fraction: Float,
    progressColor: Color,
    tipColor: Color,
    tipEnabled: Boolean,
) {
    val radius = CornerRadius(lineWidth / 2)
    val trackRect = lineRect(lineWidth, size.width)
    val clip = Path().apply { addRoundRect(RoundRect(trackRect, radius)) }
    val right = fraction * size.width
    clipPath(clip) {
        val progressRect = lineRect(lineWidth, right)
        drawRoundRect(progressColor, progressRect.topLeft, progressRect.size, radius)
        if (tipEnabled) {
            val left = max(0f, right - lineWidth)
            drawRoundRect(
                tipColor,
                Offset(left, trackRect.top),
                Size(right - left, lineWidth),
                radius
            )
        }
    }
}

/**
 * Mirrors SnakeDrawer. The View version records one trail point per animation frame;
 * here the trail is sampled from the same formulas, so it doesn't depend on the frame rate.
 */
private fun DrawScope.drawSnake(
    path: Path,
    lineWidth: Float,
    targetProgress: Float,
    time: Float,
    inverse: Boolean,
    progressColor: Color,
    tipColor: Color,
    tipEnabled: Boolean,
) {
    if (targetProgress == 0f) return

    val cornerRadius = lineWidth / 2
    val middleY = size.height / 2
    val angleMax = 5f * PI.toFloat()
    val progressFactor = min(max(ln(targetProgress + 0.2) + 1.5, 0.0), 1.0).toFloat()
    val amplitudeMax = (size.height / 2 - cornerRadius) * progressFactor
    val targetLineWidth = targetProgress * size.width
    val longLine = targetLineWidth > lineWidth
    val progressRange = if (longLine) targetLineWidth - lineWidth else targetLineWidth
    val xStart = cornerRadius * if (longLine) 1 else -1

    fun sineAt(animProgress: Float): Float {
        val angle = animProgress * angleMax
        return sin(if (inverse) -angle else angle) * amplitudeMax
    }

    val animProgress = Decelerate.transform(time)
    val decay = 1 - animProgress

    path.rewind()
    path.moveTo(xStart, middleY)
    val samples = (SNAKE_TRAIL_SAMPLES * time).toInt()
    for (i in 0 until samples) {
        val positionFactor = i.toFloat() / samples
        val pointProgress = Decelerate.transform(time * positionFactor)
        path.lineTo(
            xStart + pointProgress * progressRange,
            middleY + sineAt(pointProgress) * decay * positionFactor
        )
    }
    val head = Offset(xStart + animProgress * progressRange, middleY + sineAt(animProgress) * decay)
    path.lineTo(head.x, head.y)

    val drawSnakeBody: DrawScope.() -> Unit = {
        drawPath(
            path,
            progressColor,
            style = Stroke(lineWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        if (tipEnabled) {
            drawCircle(tipColor, cornerRadius, head)
        }
    }
    if (longLine) {
        drawSnakeBody()
    } else {
        val clip = Path().apply {
            addRoundRect(RoundRect(lineRect(lineWidth, size.width), CornerRadius(cornerRadius)))
        }
        clipPath(clip, block = drawSnakeBody)
    }
}
