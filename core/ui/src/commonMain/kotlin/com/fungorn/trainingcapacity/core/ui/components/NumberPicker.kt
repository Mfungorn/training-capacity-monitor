package com.fungorn.trainingcapacity.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    modifier: Modifier = Modifier
) {
    val next = (value + step).coerceAtMost(maxValue)
    val previous = (value - step).coerceAtLeast(minValue)
    val canIncrement = value < maxValue
    val canDecrement = value > minValue

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { if (canIncrement) onValueChange(next) },
                enabled = canIncrement
            ) {
                Text(
                    text = "$next",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FilledTonalIconButton(
                    onClick = { if (canDecrement) onValueChange(previous) },
                    enabled = canDecrement
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease $label")
                }
                Text(
                    text = "$value",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(min = 64.dp)
                        .padding(vertical = 4.dp)
                )
                FilledTonalIconButton(
                    onClick = { if (canIncrement) onValueChange(next) },
                    enabled = canIncrement
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase $label")
                }
            }

            TextButton(
                onClick = { if (canDecrement) onValueChange(previous) },
                enabled = canDecrement
            ) {
                Text(
                    text = "$previous",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
