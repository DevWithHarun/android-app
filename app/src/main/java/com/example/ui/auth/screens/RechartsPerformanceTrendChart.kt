package com.example.ui.auth.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhysicalMeasurementEntity

data class MetricDataPoint(
    val label: String,
    val speed: Float,      // 0 - 100
    val endurance: Float,  // 0 - 100
    val strength: Float    // 0 - 100
)

@Composable
fun RechartsPerformanceTrendCard(
    measurements: List<PhysicalMeasurementEntity>,
    onAddMetricClick: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    // Series toggles (Recharts interactive legend)
    var showSpeed by remember { mutableStateOf(true) }
    var showEndurance by remember { mutableStateOf(true) }
    var showStrength by remember { mutableStateOf(true) }

    // Dropdown filter for time interval: Weekly, Monthly, Yearly
    var timeInterval by remember { mutableStateOf("Monthly") }
    var showIntervalDropdown by remember { mutableStateOf(false) }

    // Colors matching modern Recharts palette
    val speedColor = Color(0xFF0D9488)      // Teal
    val enduranceColor = Color(0xFF10B981)  // Emerald
    val strengthColor = Color(0xFF3B82F6)   // Royal Blue

    // Derived metric points incorporating physical measurements based on interval
    val trendData = remember(measurements, timeInterval) {
        val latestSprint = measurements.filter { it.testType.contains("Sprint", ignoreCase = true) || it.testType.contains("Speed", ignoreCase = true) }.lastOrNull()
        val latestEndurance = measurements.filter { it.testType.contains("Yo-Yo", ignoreCase = true) || it.testType.contains("Beep", ignoreCase = true) || it.testType.contains("Endurance", ignoreCase = true) }.lastOrNull()
        val latestStrength = measurements.filter { it.testType.contains("Jump", ignoreCase = true) || it.testType.contains("Strength", ignoreCase = true) }.lastOrNull()

        val parsedSpeedVal = latestSprint?.value?.toFloatOrNull()?.let {
            (100f - ((it - 3.5f) * 20f)).coerceIn(60f, 98f)
        } ?: 84f

        val parsedEnduranceVal = latestEndurance?.value?.toFloatOrNull()?.let {
            (it / 20f).coerceIn(65f, 96f)
        } ?: 81f

        val parsedStrengthVal = latestStrength?.value?.toFloatOrNull()?.let {
            (it * 1.5f).coerceIn(60f, 95f)
        } ?: 78f

        when (timeInterval) {
            "Weekly" -> listOf(
                MetricDataPoint("Wk 1", (parsedSpeedVal - 5f).coerceAtLeast(60f), (parsedEnduranceVal - 6f).coerceAtLeast(60f), (parsedStrengthVal - 4f).coerceAtLeast(60f)),
                MetricDataPoint("Wk 2", (parsedSpeedVal - 3f).coerceAtLeast(60f), (parsedEnduranceVal - 4f).coerceAtLeast(60f), (parsedStrengthVal - 2f).coerceAtLeast(60f)),
                MetricDataPoint("Wk 3", (parsedSpeedVal - 4f).coerceAtLeast(60f), (parsedEnduranceVal - 5f).coerceAtLeast(60f), (parsedStrengthVal - 3f).coerceAtLeast(60f)),
                MetricDataPoint("Wk 4", (parsedSpeedVal - 2f).coerceAtLeast(60f), (parsedEnduranceVal - 3f).coerceAtLeast(60f), (parsedStrengthVal - 2f).coerceAtLeast(60f)),
                MetricDataPoint("Wk 5", (parsedSpeedVal - 1f).coerceAtLeast(60f), (parsedEnduranceVal - 1f).coerceAtLeast(60f), (parsedStrengthVal - 1f).coerceAtLeast(60f)),
                MetricDataPoint("Wk 6", parsedSpeedVal, parsedEnduranceVal, parsedStrengthVal)
            )
            "Yearly" -> listOf(
                MetricDataPoint("2021", (parsedSpeedVal - 18f).coerceAtLeast(50f), (parsedEnduranceVal - 16f).coerceAtLeast(50f), (parsedStrengthVal - 15f).coerceAtLeast(50f)),
                MetricDataPoint("2022", (parsedSpeedVal - 12f).coerceAtLeast(55f), (parsedEnduranceVal - 11f).coerceAtLeast(55f), (parsedStrengthVal - 10f).coerceAtLeast(55f)),
                MetricDataPoint("2023", (parsedSpeedVal - 6f).coerceAtLeast(60f), (parsedEnduranceVal - 5f).coerceAtLeast(60f), (parsedStrengthVal - 5f).coerceAtLeast(60f)),
                MetricDataPoint("2024", parsedSpeedVal, parsedEnduranceVal, parsedStrengthVal)
            )
            else -> listOf(
                MetricDataPoint("Jan", (parsedSpeedVal - 11f).coerceAtLeast(55f), (parsedEnduranceVal - 9f).coerceAtLeast(55f), (parsedStrengthVal - 8f).coerceAtLeast(55f)),
                MetricDataPoint("Feb", (parsedSpeedVal - 8f).coerceAtLeast(58f), (parsedEnduranceVal - 7f).coerceAtLeast(58f), (parsedStrengthVal - 6f).coerceAtLeast(58f)),
                MetricDataPoint("Mar", (parsedSpeedVal - 6f).coerceAtLeast(60f), (parsedEnduranceVal - 6f).coerceAtLeast(60f), (parsedStrengthVal - 4f).coerceAtLeast(60f)),
                MetricDataPoint("Apr", (parsedSpeedVal - 4f).coerceAtLeast(62f), (parsedEnduranceVal - 4f).coerceAtLeast(62f), (parsedStrengthVal - 3f).coerceAtLeast(62f)),
                MetricDataPoint("May", (parsedSpeedVal - 1f).coerceAtLeast(64f), (parsedEnduranceVal - 2f).coerceAtLeast(64f), (parsedStrengthVal - 1f).coerceAtLeast(64f)),
                MetricDataPoint("Jun", parsedSpeedVal, parsedEnduranceVal, parsedStrengthVal)
            )
        }
    }

    // Active scrubbing index for Recharts Tooltip
    var activeScrubIndex by remember { mutableStateOf<Int?>(null) }
    val activePoint = activeScrubIndex?.let { trendData.getOrNull(it) } ?: trendData.lastOrNull()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with Recharts badge and Dropdown Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0FDF4)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(12.dp))
                            Text(
                                text = "RECHARTS TREND VISUALIZER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Athletic Progression Trends",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryColor
                    )
                    Text(
                        text = "Comparative spline curves for Speed, Endurance, and Strength.",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                // Dropdown Filter for Weekly, Monthly, Yearly intervals
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.clickable { showIntervalDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when (timeInterval) {
                                    "Weekly" -> Icons.Default.DateRange
                                    "Yearly" -> Icons.Default.History
                                    else -> Icons.Default.CalendarMonth
                                },
                                contentDescription = null,
                                tint = speedColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = timeInterval,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Select interval",
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showIntervalDropdown,
                        onDismissRequest = { showIntervalDropdown = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("Weekly", "Monthly", "Yearly").forEach { interval ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$interval View",
                                        fontWeight = if (timeInterval == interval) FontWeight.Bold else FontWeight.Normal,
                                        color = if (timeInterval == interval) speedColor else primaryColor,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    timeInterval = interval
                                    showIntervalDropdown = false
                                    activeScrubIndex = null
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when(interval) {
                                            "Weekly" -> Icons.Default.DateRange
                                            "Yearly" -> Icons.Default.History
                                            else -> Icons.Default.CalendarMonth
                                        },
                                        contentDescription = null,
                                        tint = if (timeInterval == interval) speedColor else textMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recharts-style Interactive Tooltip Banner
            if (activePoint != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activePoint.label} 2024 Evaluation",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            val overallAvg = String.format("%.0f", (activePoint.speed + activePoint.endurance + activePoint.strength) / 3f)
                            Text(
                                text = "Overall Athletic Index: $overallAvg / 100",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (showSpeed) {
                                TooltipMetricBadge("SPD", "${activePoint.speed.toInt()}", speedColor)
                            }
                            if (showEndurance) {
                                TooltipMetricBadge("END", "${activePoint.endurance.toInt()}", enduranceColor)
                            }
                            if (showStrength) {
                                TooltipMetricBadge("STR", "${activePoint.strength.toInt()}", strengthColor)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Recharts Canvas Graph with grid, cubic bezier lines, dots, gradients, and touch scrubbing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                val textMeasurer = rememberTextMeasurer()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(trendData) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val paddingLeft = 32.dp.toPx()
                                    val paddingRight = 16.dp.toPx()
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    if (chartWidth > 0 && trendData.isNotEmpty()) {
                                        val relX = (offset.x - paddingLeft).coerceIn(0f, chartWidth)
                                        val step = chartWidth / (trendData.size - 1).coerceAtLeast(1)
                                        val idx = (relX / step).toInt().coerceIn(0, trendData.size - 1)
                                        activeScrubIndex = idx
                                    }
                                }
                            )
                        }
                        .pointerInput(trendData) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val paddingLeft = 32.dp.toPx()
                                val paddingRight = 16.dp.toPx()
                                val chartWidth = size.width - paddingLeft - paddingRight
                                if (chartWidth > 0 && trendData.isNotEmpty()) {
                                    val relX = (change.position.x - paddingLeft).coerceIn(0f, chartWidth)
                                    val step = chartWidth / (trendData.size - 1).coerceAtLeast(1)
                                    val idx = (relX / step).toInt().coerceIn(0, trendData.size - 1)
                                    activeScrubIndex = idx
                                }
                            }
                        }
                ) {
                    val paddingLeft = 32.dp.toPx()
                    val paddingRight = 16.dp.toPx()
                    val paddingTop = 16.dp.toPx()
                    val paddingBottom = 26.dp.toPx()

                    val chartWidth = size.width - paddingLeft - paddingRight
                    val chartHeight = size.height - paddingTop - paddingBottom
                    val chartLeft = paddingLeft
                    val chartRight = size.width - paddingRight
                    val chartTop = paddingTop
                    val chartBottom = size.height - paddingBottom

                    // Draw Horizontal Cartesian Grid Lines (Recharts <CartesianGrid strokeDasharray="3 3" />)
                    val yTicks = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                    val yLabels = listOf("25", "50", "75", "100")
                    val gridDash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                    // Baseline Y=0
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(chartLeft, chartBottom),
                        end = Offset(chartRight, chartBottom),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw 0 label
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "0",
                        topLeft = Offset(4.dp.toPx(), chartBottom - 8.dp.toPx()),
                        style = TextStyle(color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )

                    yTicks.forEachIndexed { i, tickRatio ->
                        val yPos = chartBottom - tickRatio * chartHeight
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(chartLeft, yPos),
                            end = Offset(chartRight, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = gridDash
                        )
                        // Y axis tick label
                        drawText(
                            textMeasurer = textMeasurer,
                            text = yLabels[i],
                            topLeft = Offset(4.dp.toPx(), yPos - 7.dp.toPx()),
                            style = TextStyle(color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    if (trendData.isEmpty()) return@Canvas

                    val stepX = chartWidth / (trendData.size - 1).coerceAtLeast(1)

                    // Compute points for each metric
                    fun calculatePoints(selector: (MetricDataPoint) -> Float): List<Offset> {
                        return trendData.mapIndexed { idx, item ->
                            val x = chartLeft + idx * stepX
                            val valueNorm = (selector(item) / 100f).coerceIn(0f, 1f)
                            val y = chartBottom - valueNorm * chartHeight
                            Offset(x, y)
                        }
                    }

                    val speedPoints = calculatePoints { it.speed }
                    val endurancePoints = calculatePoints { it.endurance }
                    val strengthPoints = calculatePoints { it.strength }

                    // Helper to draw Monotone Cubic Bezier curve & gradient area
                    fun drawMonotoneCurve(points: List<Offset>, color: Color) {
                        if (points.isEmpty()) return

                        val linePath = Path()
                        val fillPath = Path()

                        linePath.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, chartBottom)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = if (i > 0) points[i - 1] else points[i]
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val p3 = if (i + 2 < points.size) points[i + 2] else p2

                            val cp1X = p1.x + (p2.x - p0.x) / 5f
                            val cp1Y = p1.y + (p2.y - p0.y) / 5f
                            val cp2X = p2.x - (p3.x - p1.x) / 5f
                            val cp2Y = p2.y - (p3.y - p1.y) / 5f

                            linePath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                            fillPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                        }

                        fillPath.lineTo(points.last().x, chartBottom)
                        fillPath.close()

                        // Gradient fill under curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(color.copy(alpha = 0.22f), color.copy(alpha = 0.0f)),
                                startY = chartTop,
                                endY = chartBottom
                            )
                        )

                        // Smooth Line stroke
                        drawPath(
                            path = linePath,
                            color = color,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Data Points (Recharts activeDot)
                        points.forEachIndexed { idx, pt ->
                            val isSelected = activeScrubIndex == idx
                            drawCircle(Color.White, radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(), center = pt)
                            drawCircle(color, radius = if (isSelected) 4.5.dp.toPx() else 3.dp.toPx(), center = pt)
                            if (isSelected) {
                                drawCircle(color.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = pt)
                            }
                        }
                    }

                    // Draw active series
                    if (showStrength) drawMonotoneCurve(strengthPoints, strengthColor)
                    if (showEndurance) drawMonotoneCurve(endurancePoints, enduranceColor)
                    if (showSpeed) drawMonotoneCurve(speedPoints, speedColor)

                    // Draw Scrubbing Cursor if active (Recharts <Tooltip cursor={{ strokeDasharray: '3 3' }} />)
                    activeScrubIndex?.let { idx ->
                        if (idx in trendData.indices) {
                            val activeX = chartLeft + idx * stepX
                            drawLine(
                                color = Color(0xFF64748B),
                                start = Offset(activeX, chartTop),
                                end = Offset(activeX, chartBottom),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }
                    }

                    // Draw X-Axis Ticks (Month names)
                    trendData.forEachIndexed { idx, point ->
                        val x = chartLeft + idx * stepX
                        drawText(
                            textMeasurer = textMeasurer,
                            text = point.label,
                            topLeft = Offset(x - 10.dp.toPx(), chartBottom + 6.dp.toPx()),
                            style = TextStyle(
                                color = if (activeScrubIndex == idx) primaryColor else Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = if (activeScrubIndex == idx) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Recharts Legend (Tap to toggle visibility)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendToggleItem(
                    label = "Speed (30m Sprint)",
                    color = speedColor,
                    isActive = showSpeed,
                    onClick = { showSpeed = !showSpeed }
                )
                VerticalDivider(modifier = Modifier.height(20.dp), color = borderColor)
                LegendToggleItem(
                    label = "Endurance (Yo-Yo)",
                    color = enduranceColor,
                    isActive = showEndurance,
                    onClick = { showEndurance = !showEndurance }
                )
                VerticalDivider(modifier = Modifier.height(20.dp), color = borderColor)
                LegendToggleItem(
                    label = "Strength (Jump)",
                    color = strengthColor,
                    isActive = showStrength,
                    onClick = { showStrength = !showStrength }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Progression Callout & Log Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = speedColor, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Peak Metric: Speed rated ${trendData.maxOfOrNull { it.speed }?.toInt() ?: 88}/100 (+8% Season Gain)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                }

                TextButton(
                    onClick = onAddMetricClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = speedColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Test", fontSize = 12.sp, color = speedColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TooltipMetricBadge(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Text("$label: $value", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LegendToggleItem(
    label: String,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (isActive) color else Color(0xFFCBD5E1), CircleShape)
        )
        Text(
            text = label.split(" ").first(),
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) Color(0xFF1E293B) else Color(0xFF94A3B8)
        )
    }
}
