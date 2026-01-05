package com.fungorn.trainingcapacity.feature.dashboard.presentation.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastCoerceAtLeast

@Composable
fun DashboardDifficultyGraph(
    barData: DashboardDifficultyGraphData,
    modifier: Modifier = Modifier,
) {
    val barWidth = 8
    var xCaptionMaxWidth by remember { mutableIntStateOf(0) }
    var yCaptionMaxWidth by remember { mutableIntStateOf(0) }
    var yCaptionMaxHeight by remember { mutableIntStateOf(0) }
    val dividerThicknessDp = DividerDefaults.Thickness
    val horizontalScrollState = rememberLazyListState()
    val yValues = barData.entries.asSequence()
        .map(DashboardDifficultyGraphEntry::difficulty)
        .sortedDescending()
        .toSet()
    val xValuePadding = 8
    val yValuePadding = 8

    with(LocalDensity.current) {
        Column(
            modifier = modifier,
        ) {
            Row {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    yValues.forEach { y ->
                        Spacer(
                            modifier = Modifier
                                .height(
                                    (yValuePadding - yCaptionMaxHeight / 2)
                                        .fastCoerceAtLeast(0)
                                        .toDp()
                                )
                        )
                        Text(
                            text = y.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            onTextLayout = { result ->
                                val (width, height) = result.size
                                if (width > yCaptionMaxWidth)
                                    yCaptionMaxWidth = width
                                if (height > yCaptionMaxHeight)
                                    yCaptionMaxHeight = height
                            }
                        )
                        Spacer(
                            modifier = Modifier
                                .height(
                                    (yValuePadding - yCaptionMaxHeight / 2)
                                        .fastCoerceAtLeast(0)
                                        .toDp()
                                )
                        )
                    }
                }
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        state = horizontalScrollState,
                        contentPadding = PaddingValues(
                            horizontal = (xValuePadding - barWidth / 2)
                                .fastCoerceAtLeast(0)
                                .toDp(),
                        )
                    ) {
                        itemsIndexed(barData.entries) { index, item ->
                            val height =
                                (yValues.size - 1 - yValues.indexOf(item.difficulty)) * yValuePadding
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .size(
                                        width = barWidth.toDp(),
                                        height = height
                                            .fastCoerceAtLeast(0)
                                            .toDp()
                                    )
                            )
                            if (index != barData.entries.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .width(
                                            (xValuePadding - barWidth)
                                                .fastCoerceAtLeast(0)
                                                .toDp()
                                        )
                                )
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(dividerThicknessDp))
            Row {
                Text(
                    modifier = Modifier.width(yCaptionMaxWidth.toDp()),
                    text = "0",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(dividerThicknessDp))
                LazyRow(
                    modifier = Modifier
                        .weight(1f),
                    state = horizontalScrollState,
                    contentPadding = PaddingValues(
                        horizontal = (xValuePadding - xCaptionMaxWidth / 2)
                            .fastCoerceAtLeast(0)
                            .toDp(),
                    )
                ) {
                    itemsIndexed(barData.entries) { index, item ->
                        Text(
                            text = item.formattedDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (index != barData.entries.size - 1) {
                            Spacer(
                                modifier = Modifier
                                    .width(
                                        (xValuePadding - xCaptionMaxWidth / 2)
                                            .fastCoerceAtLeast(0)
                                            .toDp(),
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}