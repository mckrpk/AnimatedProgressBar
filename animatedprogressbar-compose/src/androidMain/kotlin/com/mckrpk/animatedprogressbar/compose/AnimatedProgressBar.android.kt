package com.mckrpk.animatedprogressbar.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.mckrpk.animatedprogressbar.R
import com.mckrpk.animatedprogressbar.getThemeAccentColor
import com.mckrpk.animatedprogressbar.getThemePrimaryColor
import com.mckrpk.animatedprogressbar.AnimatedProgressBar as AnimatedProgressBarView

@Composable
internal actual fun platformProgressColor(): Color {
    val context = LocalContext.current
    return remember(context) { Color(getThemePrimaryColor(context, R.color.defaultProgress)) }
}

@Composable
internal actual fun platformProgressTipColor(): Color {
    val context = LocalContext.current
    return remember(context) { Color(getThemeAccentColor(context, R.color.defaultTip)) }
}

/** Kept so 1.1.0 code passing the View's `Style` enum still compiles. */
@Deprecated(
    "Use AnimatedProgressBarStyle, which works on every platform",
    ReplaceWith("AnimatedProgressBarStyle.valueOf(style.name)")
)
@Composable
fun AnimatedProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    max: Int = 100,
    // No default, so a call without `style` picks the common overload
    style: AnimatedProgressBarView.Style,
    trackColor: Color = AnimatedProgressBarDefaults.trackColor(),
    progressColor: Color = AnimatedProgressBarDefaults.progressColor(),
    progressTipColor: Color = AnimatedProgressBarDefaults.progressTipColor(),
    progressTipEnabled: Boolean = true,
    lineWidth: Dp = AnimatedProgressBarDefaults.lineWidth(),
    animationDurationMillis: Int = AnimatedProgressBarDefaults.animationDurationMillis(),
) = AnimatedProgressBar(
    progress, modifier, max, AnimatedProgressBarStyle.valueOf(style.name), trackColor,
    progressColor, progressTipColor, progressTipEnabled, lineWidth, animationDurationMillis,
)
