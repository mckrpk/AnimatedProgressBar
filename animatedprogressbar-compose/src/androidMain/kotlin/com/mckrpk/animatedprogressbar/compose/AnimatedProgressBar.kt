package com.mckrpk.animatedprogressbar.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mckrpk.animatedprogressbar.AnimatedProgressBar as AnimatedProgressBarView

// Keep the 1.1.0 JVM file facade and Compose default arguments for compiled Android callers.
@Deprecated("Use AnimatedProgressBarStyle", level = DeprecationLevel.HIDDEN)
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
) = AnimatedProgressBar(
    progress, modifier, max, AnimatedProgressBarStyle.valueOf(style.name), trackColor,
    progressColor, progressTipColor, progressTipEnabled, lineWidth, animationDurationMillis,
)
