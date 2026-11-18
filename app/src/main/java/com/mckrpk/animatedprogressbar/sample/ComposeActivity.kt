package com.mckrpk.animatedprogressbar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mckrpk.animatedprogressbar.AnimatedProgressBar
import kotlin.random.Random
import com.mckrpk.animatedprogressbar.compose.AnimatedProgressBar as ComposeAnimatedProgressBar
import com.mckrpk.animatedprogressbar.compose.AnimatedProgressBarStyle

/**
 * Shows the Compose version next to the View version with the same settings.
 */
class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ComparisonScreen()
                }
            }
        }
    }
}

@Composable
private fun ComparisonScreen() {
    var progress by rememberSaveable { mutableIntStateOf(75) }
    var tipEnabled by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Progress: $progress / 100", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { progress = Random.nextInt(0, 101) }) {
                Text("Random progress")
            }
            Text("Tip", modifier = Modifier.padding(start = 24.dp, end = 8.dp))
            Switch(checked = tipEnabled, onCheckedChange = { tipEnabled = it })
        }

        for (style in AnimatedProgressBar.Style.values()) {
            Text(
                style.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text("View (XML)", style = MaterialTheme.typography.labelMedium)
            ViewProgressBar(progress, style, tipEnabled)
            Text("Compose", style = MaterialTheme.typography.labelMedium)
            ComposeAnimatedProgressBar(
                progress = progress,
                style = AnimatedProgressBarStyle.valueOf(style.name),
                trackColor = colorResource(R.color.colorGray),
                progressColor = colorResource(R.color.colorPrimary),
                progressTipColor = colorResource(R.color.colorAccent),
                progressTipEnabled = tipEnabled,
                lineWidth = LINE_WIDTH,
            )
        }
    }
}

@Composable
private fun ViewProgressBar(progress: Int, style: AnimatedProgressBar.Style, tipEnabled: Boolean) {
    val trackColor = colorResource(R.color.colorGray).toArgb()
    val progressColor = colorResource(R.color.colorPrimary).toArgb()
    val tipColor = colorResource(R.color.colorAccent).toArgb()
    val lineWidthPx = with(LocalDensity.current) { LINE_WIDTH.roundToPx() }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AnimatedProgressBar(context).apply {
                setMax(100)
                setTrackColor(trackColor)
                setProgressColor(progressColor)
                setProgressTipColor(tipColor)
                setLineWidth(lineWidthPx)
                setProgressStyle(style)
            }
        },
        update = { view ->
            view.setProgressTipEnabled(tipEnabled)
            // setProgress() replays the animation, so call it only when progress changes
            if (view.tag != progress) {
                view.tag = progress
                view.setProgress(progress)
            }
        }
    )
}

private val LINE_WIDTH = 5.dp
