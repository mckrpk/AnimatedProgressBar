package com.mckrpk.animatedprogressbar.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal actual fun platformProgressColor(): Color = DefaultProgressColor

@Composable
internal actual fun platformProgressTipColor(): Color = DefaultProgressTipColor
