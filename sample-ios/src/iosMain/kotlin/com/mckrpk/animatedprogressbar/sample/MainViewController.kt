package com.mckrpk.animatedprogressbar.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.mckrpk.animatedprogressbar.compose.AnimatedProgressBar
import com.mckrpk.animatedprogressbar.compose.AnimatedProgressBarStyle
import kotlin.random.Random

@Suppress("unused", "FunctionName") // Called from Swift
fun MainViewController() = ComposeUIViewController {
    var progress by remember { mutableIntStateOf(75) }
    var tipEnabled by remember { mutableStateOf(true) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.safeDrawingPadding().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Progress: $progress / 100", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { progress = Random.nextInt(0, 101) }) {
                        Text("Random progress")
                    }
                    Text("Tip", modifier = Modifier.padding(start = 24.dp, end = 8.dp))
                    Switch(checked = tipEnabled, onCheckedChange = { tipEnabled = it })
                }
                for (style in AnimatedProgressBarStyle.entries) {
                    Text(
                        style.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    AnimatedProgressBar(
                        progress = progress,
                        style = style,
                        progressTipEnabled = tipEnabled,
                    )
                }
            }
        }
    }
}
