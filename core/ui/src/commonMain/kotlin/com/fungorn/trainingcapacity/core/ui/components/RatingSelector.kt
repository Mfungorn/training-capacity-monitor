package com.fungorn.trainingcapacity.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/** How the rating values map onto a red → green scale. */
enum class RatingColorScale {
    NONE,
    HIGH_IS_GOOD,
    LOW_IS_GOOD,
}

@Composable
fun RatingSelector(
    label: String,
    value: Int?,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 1..5,
    enabled: Boolean = true,
    colorScale: RatingColorScale = RatingColorScale.NONE,
    supportingText: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val options = range.toList()
            options.forEachIndexed { index, option ->
                val color = ratingColor(colorScale, index, options.size)
                SegmentedButton(
                    selected = option == value,
                    onClick = { onValueChange(option) },
                    enabled = enabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    colors = if (color == null) {
                        SegmentedButtonDefaults.colors()
                    } else {
                        SegmentedButtonDefaults.colors(
                            activeContainerColor = color,
                            activeContentColor = if (color.luminance() > 0.5f) Color.Black else Color.White,
                            activeBorderColor = color,
                            inactiveContainerColor = color.copy(alpha = 0.18f),
                        )
                    },
                    icon = {}
                ) {
                    Text("$option")
                }
            }
        }
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private val ratingPalette = listOf(
    Color(0xFFE53935), // red
    Color(0xFFFB8C00), // orange
    Color(0xFFFDD835), // yellow
    Color(0xFF9CCC65), // light green
    Color(0xFF43A047), // green
)

private fun ratingColor(scale: RatingColorScale, index: Int, count: Int): Color? {
    if (scale == RatingColorScale.NONE || count < 2) return null
    val position = index.toFloat() / (count - 1)
    val paletteIndex = (position * (ratingPalette.size - 1)).toInt()
    return when (scale) {
        RatingColorScale.HIGH_IS_GOOD -> ratingPalette[paletteIndex]
        RatingColorScale.LOW_IS_GOOD -> ratingPalette[ratingPalette.size - 1 - paletteIndex]
        RatingColorScale.NONE -> null
    }
}
