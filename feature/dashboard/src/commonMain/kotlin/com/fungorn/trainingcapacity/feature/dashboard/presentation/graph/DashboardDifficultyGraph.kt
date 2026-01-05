package com.fungorn.trainingcapacity.feature.dashboard.presentation.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val MAX_DIFFICULTY = 3
private const val MIN_DIFFICULTY = 0
private val GRAPH_HEIGHT = 120.dp
private val BAR_WIDTH = 24.dp
private val BAR_SPACING = 8.dp
private val Y_AXIS_WIDTH = 16.dp

@Composable
fun DashboardDifficultyGraph(
    barData: DashboardDifficultyGraphData,
    modifier: Modifier = Modifier,
) {
    if (barData.entries.isEmpty()) return

    val barColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val scrollState = rememberScrollState()
    val difficultyRange = MAX_DIFFICULTY - MIN_DIFFICULTY
    val entriesCount = barData.entries.size

    LaunchedEffect(entriesCount) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Y-axis labels
            Box(
                modifier = Modifier
                    .width(Y_AXIS_WIDTH)
                    .height(GRAPH_HEIGHT + 12.dp)
            ) {
                for (i in MAX_DIFFICULTY downTo MIN_DIFFICULTY) {
                    val fraction = 1f - (i.toFloat() / difficultyRange)
                    Text(
                        text = i.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = GRAPH_HEIGHT * fraction)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Graph area with bars
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(GRAPH_HEIGHT)
                    .horizontalScroll(scrollState)
            ) {
                val totalWidth = (BAR_WIDTH + BAR_SPACING) * barData.entries.size

                Canvas(
                    modifier = Modifier
                        .width(totalWidth)
                        .height(GRAPH_HEIGHT)
                ) {
                    val graphHeight = size.height
                    val barWidthPx = BAR_WIDTH.toPx()
                    val barSpacingPx = BAR_SPACING.toPx()

                    for (i in MIN_DIFFICULTY..MAX_DIFFICULTY) {
                        val y = (graphHeight - (i.toFloat() / difficultyRange) * graphHeight)
                            .coerceAtLeast(0f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    barData.entries.forEachIndexed { index, entry ->
                        val clampedDifficulty =
                            entry.difficulty.coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY)
                        val barHeight =
                            (clampedDifficulty.toFloat() / difficultyRange) * graphHeight
                        val x = index * (barWidthPx + barSpacingPx)
                        val y = (graphHeight - barHeight).coerceAtLeast(0f)

                        if (barHeight > 0) {
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidthPx, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(start = Y_AXIS_WIDTH + 8.dp)
        ) {
            barData.entries.forEach { entry ->
                Text(
                    modifier = Modifier.width(BAR_WIDTH + BAR_SPACING),
                    text = entry.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}