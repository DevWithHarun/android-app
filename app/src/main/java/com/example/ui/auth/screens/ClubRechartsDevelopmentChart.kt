package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AthleteEntity
import com.example.data.ClubFitnessAssessmentModel
import com.example.data.ClubPlayerPerformanceModel
import kotlin.math.roundToInt

data class DevelopmentDataPoint(
    val label: String,
    val speedKmh: Float,        // e.g. 28.0 - 35.0
    val vo2Max: Float,          // e.g. 48.0 - 62.0
    val tacticalRating: Float,  // e.g. 60 - 95
    val trainingLoadAu: Float,  // e.g. 300 - 950 AU
    val intensityPercent: Float,// e.g. 50 - 98%
    val acwrRatio: Float        // e.g. 0.8 - 1.5
)

@Composable
fun ClubRechartsDevelopmentCard(
    athletes: List<AthleteEntity> = emptyList(),
    performances: List<ClubPlayerPerformanceModel> = emptyList(),
    fitnessTests: List<ClubFitnessAssessmentModel> = emptyList()
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val emeraldAccent = Color(0xFF10B981)
    val amberAccent = Color(0xFFF59E0B)
    val blueAccent = Color(0xFF3B82F6)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    // Chart Mode: "Growth Over Time" vs "Training Intensity"
    var chartMode by rememberSaveable { mutableStateOf("Growth Over Time") }

    // Time Interval: "Weekly", "Monthly", "Season"
    var timeInterval by rememberSaveable { mutableStateOf("Monthly") }

    // Selected Athlete filter (or "All Squad Average")
    var selectedAthleteName by rememberSaveable { mutableStateOf("All Squad Average") }
    var showAthleteDropdown by remember { mutableStateOf(false) }

    // Series Toggles (Interactive Recharts Legend)
    var showSeries1 by remember { mutableStateOf(true) } // Speed / Load
    var showSeries2 by remember { mutableStateOf(true) } // VO2 Max / Intensity
    var showSeries3 by remember { mutableStateOf(true) } // Tactical Rating / ACWR

    // Interactive Touch Hover / Scrub State
    var touchX by remember { mutableStateOf<Float?>(null) }

    // Build Time Series Data based on filters
    val chartData = remember(chartMode, timeInterval, selectedAthleteName, fitnessTests, performances) {
        val baseSpeed = 31.5f
        val baseVo2 = 54.0f
        val baseTactical = 76.0f
        val baseLoad = 620f
        val baseIntensity = 78f
        val baseAcwr = 1.05f

        when (timeInterval) {
            "Weekly" -> listOf(
                DevelopmentDataPoint("W1", baseSpeed - 1.2f, baseVo2 - 1.8f, baseTactical - 3f, baseLoad - 80f, baseIntensity - 6f, 0.92f),
                DevelopmentDataPoint("W2", baseSpeed - 0.8f, baseVo2 - 1.2f, baseTactical - 2f, baseLoad - 20f, baseIntensity - 2f, 0.98f),
                DevelopmentDataPoint("W3", baseSpeed - 0.5f, baseVo2 - 0.8f, baseTactical - 1f, baseLoad + 60f, baseIntensity + 5f, 1.12f),
                DevelopmentDataPoint("W4", baseSpeed - 0.2f, baseVo2 - 0.4f, baseTactical + 1f, baseLoad + 120f, baseIntensity + 8f, 1.22f),
                DevelopmentDataPoint("W5", baseSpeed + 0.3f, baseVo2 + 0.5f, baseTactical + 2f, baseLoad - 40f, baseIntensity - 3f, 1.08f),
                DevelopmentDataPoint("W6", baseSpeed + 0.8f, baseVo2 + 1.1f, baseTactical + 3f, baseLoad + 90f, baseIntensity + 6f, 1.18f),
                DevelopmentDataPoint("W7", baseSpeed + 1.2f, baseVo2 + 1.6f, baseTactical + 4f, baseLoad + 40f, baseIntensity + 2f, 1.10f),
                DevelopmentDataPoint("W8 (Now)", baseSpeed + 1.8f, baseVo2 + 2.2f, baseTactical + 6f, baseLoad + 150f, baseIntensity + 10f, 1.14f)
            )
            "Season" -> listOf(
                DevelopmentDataPoint("Q1 Pre-Season", baseSpeed - 2.8f, baseVo2 - 3.5f, baseTactical - 8f, baseLoad + 220f, baseIntensity + 15f, 1.28f),
                DevelopmentDataPoint("Q2 Early League", baseSpeed - 1.2f, baseVo2 - 1.4f, baseTactical - 3f, baseLoad + 50f, baseIntensity + 4f, 1.04f),
                DevelopmentDataPoint("Q3 Mid-Season", baseSpeed + 0.6f, baseVo2 + 0.8f, baseTactical + 3f, baseLoad + 120f, baseIntensity + 8f, 1.15f),
                DevelopmentDataPoint("Q4 Championship", baseSpeed + 2.1f, baseVo2 + 2.6f, baseTactical + 7f, baseLoad + 90f, baseIntensity + 6f, 1.08f)
            )
            else -> listOf(
                // Monthly (6 months)
                DevelopmentDataPoint("May", baseSpeed - 2.2f, baseVo2 - 2.8f, baseTactical - 6f, baseLoad - 90f, baseIntensity - 8f, 0.94f),
                DevelopmentDataPoint("Jun", baseSpeed - 1.5f, baseVo2 - 2.0f, baseTactical - 4f, baseLoad + 140f, baseIntensity + 12f, 1.24f),
                DevelopmentDataPoint("Jul", baseSpeed - 0.7f, baseVo2 - 1.1f, baseTactical - 2f, baseLoad + 40f, baseIntensity + 3f, 1.06f),
                DevelopmentDataPoint("Aug", baseSpeed + 0.2f, baseVo2 + 0.4f, baseTactical + 2f, baseLoad + 80f, baseIntensity + 7f, 1.14f),
                DevelopmentDataPoint("Sep", baseSpeed + 1.1f, baseVo2 + 1.5f, baseTactical + 5f, baseLoad - 20f, baseIntensity - 2f, 1.02f),
                DevelopmentDataPoint("Oct (Live)", baseSpeed + 1.9f, baseVo2 + 2.4f, baseTactical + 7f, baseLoad + 110f, baseIntensity + 9f, 1.12f)
            )
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Title & Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3E8FF),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.QueryStats, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recharts Development & Workload Analytics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time longitudinal development curves & training load spectrum",
                        fontSize = 11.sp,
                        color = textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector: Growth Over Time vs Training Intensity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf("Growth Over Time", "Training Intensity").forEach { mode ->
                    val isSelected = chartMode == mode
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color.White else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, borderColor) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { chartMode = mode }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = if (mode == "Growth Over Time") "📈 Growth Over Time" else "⚡ Training Intensity & Load",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else textMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interval & Athlete Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interval Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Weekly", "Monthly", "Season").forEach { interval ->
                        val selected = timeInterval == interval
                        FilterChip(
                            selected = selected,
                            onClick = { timeInterval = interval },
                            label = { Text(interval, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEDE9FE),
                                selectedLabelColor = purpleAccent
                            ),
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }

                // Athlete Filter Dropdown
                val athleteOptions = remember(athletes) { listOf("All Squad Average") + athletes.map { it.name }.distinct() }
                Box {
                    OutlinedButton(
                        onClick = { showAthleteDropdown = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = selectedAthleteName.take(14) + if (selectedAthleteName.length > 14) "…" else "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = showAthleteDropdown,
                        onDismissRequest = { showAthleteDropdown = false }
                    ) {
                        athleteOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 12.sp) },
                                onClick = {
                                    selectedAthleteName = opt
                                    showAthleteDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts Multi-Series Canvas with Interactive Crosshair
            RechartsInteractiveCanvas(
                data = chartData,
                chartMode = chartMode,
                showSeries1 = showSeries1,
                showSeries2 = showSeries2,
                showSeries3 = showSeries3,
                touchX = touchX,
                onTouchChange = { touchX = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts Interactive Legend (Click to toggle series on/off)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (chartMode == "Growth Over Time") {
                    LegendToggleChip(
                        label = "Sprint Speed (km/h)",
                        color = tealAccent,
                        isActive = showSeries1,
                        onToggle = { showSeries1 = !showSeries1 }
                    )
                    LegendToggleChip(
                        label = "VO2 Max (ml)",
                        color = purpleAccent,
                        isActive = showSeries2,
                        onToggle = { showSeries2 = !showSeries2 }
                    )
                    LegendToggleChip(
                        label = "Tactical Rating",
                        color = emeraldAccent,
                        isActive = showSeries3,
                        onToggle = { showSeries3 = !showSeries3 }
                    )
                } else {
                    LegendToggleChip(
                        label = "Training Load (AU)",
                        color = amberAccent,
                        isActive = showSeries1,
                        onToggle = { showSeries1 = !showSeries1 }
                    )
                    LegendToggleChip(
                        label = "Intensity %",
                        color = blueAccent,
                        isActive = showSeries2,
                        onToggle = { showSeries2 = !showSeries2 }
                    )
                    LegendToggleChip(
                        label = "ACWR Workload",
                        color = emeraldAccent,
                        isActive = showSeries3,
                        onToggle = { showSeries3 = !showSeries3 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // High-Yield Statistical Highlights Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (chartMode == "Growth Over Time") {
                    SummaryPill(title = "Top Growth Velocity", value = "+12.4% Acceleration", color = tealAccent, modifier = Modifier.weight(1f))
                    SummaryPill(title = "Aerobic Gain", value = "+4.2 ml/kg/min", color = purpleAccent, modifier = Modifier.weight(1f))
                    SummaryPill(title = "Tactical Grade", value = "91% Sharpness", color = emeraldAccent, modifier = Modifier.weight(1f))
                } else {
                    SummaryPill(title = "Weekly Peak Load", value = "770 AU (Optimal)", color = amberAccent, modifier = Modifier.weight(1f))
                    SummaryPill(title = "Mean Intensity", value = "87% Max HR", color = blueAccent, modifier = Modifier.weight(1f))
                    SummaryPill(title = "Workload Risk", value = "🟢 Green Zone (1.12)", color = emeraldAccent, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RechartsInteractiveCanvas(
    data: List<DevelopmentDataPoint>,
    chartMode: String,
    showSeries1: Boolean,
    showSeries2: Boolean,
    showSeries3: Boolean,
    touchX: Float?,
    onTouchChange: (Float?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    // Colors
    val tealAccent = Color(0xFF0D9488)
    val purpleAccent = Color(0xFF7E22CE)
    val emeraldAccent = Color(0xFF10B981)
    val amberAccent = Color(0xFFF59E0B)
    val blueAccent = Color(0xFF3B82F6)
    val gridColor = Color(0xFFE2E8F0)
    val crosshairColor = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFC))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        onTouchChange(offset.x)
                        tryAwaitRelease()
                        onTouchChange(null)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onTouchChange(offset.x) },
                    onDrag = { change, _ ->
                        onTouchChange(change.position.x)
                        change.consume()
                    },
                    onDragEnd = { onTouchChange(null) },
                    onDragCancel = { onTouchChange(null) }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.isEmpty()) return@Canvas

            val w = size.width
            val h = size.height
            val padLeft = 40f
            val padRight = 30f
            val padTop = 30f
            val padBottom = 40f
            val chartW = w - padLeft - padRight
            val chartH = h - padTop - padBottom

            // 1. Draw horizontal grid lines & Y-axis labels
            val yLines = 4
            for (i in 0..yLines) {
                val y = padTop + (chartH / yLines) * i
                drawLine(
                    color = gridColor,
                    start = Offset(padLeft, y),
                    end = Offset(w - padRight, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                val yLabel = if (chartMode == "Growth Over Time") {
                    val maxVal = 100
                    val minVal = 20
                    "${minVal + ((maxVal - minVal) * (yLines - i) / yLines)}"
                } else {
                    val maxLoad = 1000
                    "${(maxLoad * (yLines - i) / yLines)}"
                }
                drawText(
                    textMeasurer = textMeasurer,
                    text = yLabel,
                    topLeft = Offset(8f, y - 8f),
                    style = TextStyle(fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                )
            }

            val stepX = if (data.size > 1) chartW / (data.size - 1) else chartW

            // 2. Compute Points for Series
            val series1Points = mutableListOf<Offset>()
            val series2Points = mutableListOf<Offset>()
            val series3Points = mutableListOf<Offset>()

            data.forEachIndexed { i, dp ->
                val x = padLeft + i * stepX

                if (chartMode == "Growth Over Time") {
                    // Series 1: Speed (28-36 km/h -> scaled to 0-1)
                    val normSpeed = ((dp.speedKmh - 25f) / 15f).coerceIn(0f, 1f)
                    val ySpeed = padTop + chartH * (1f - normSpeed)
                    series1Points.add(Offset(x, ySpeed))

                    // Series 2: VO2 Max (45-65 ml -> scaled to 0-1)
                    val normVo2 = ((dp.vo2Max - 45f) / 20f).coerceIn(0f, 1f)
                    val yVo2 = padTop + chartH * (1f - normVo2)
                    series2Points.add(Offset(x, yVo2))

                    // Series 3: Tactical (60-95 -> scaled to 0-1)
                    val normTactical = ((dp.tacticalRating - 50f) / 50f).coerceIn(0f, 1f)
                    val yTactical = padTop + chartH * (1f - normTactical)
                    series3Points.add(Offset(x, yTactical))
                } else {
                    // Training Load Mode
                    // Series 1: Training Load (200 - 1000 AU)
                    val normLoad = ((dp.trainingLoadAu - 200f) / 800f).coerceIn(0f, 1f)
                    val yLoad = padTop + chartH * (1f - normLoad)
                    series1Points.add(Offset(x, yLoad))

                    // Series 2: Intensity % (50 - 100%)
                    val normInt = ((dp.intensityPercent - 50f) / 50f).coerceIn(0f, 1f)
                    val yInt = padTop + chartH * (1f - normInt)
                    series2Points.add(Offset(x, yInt))

                    // Series 3: ACWR (0.6 - 1.6 -> scaled to 0-1)
                    val normAcwr = ((dp.acwrRatio - 0.6f) / 1.0f).coerceIn(0f, 1f)
                    val yAcwr = padTop + chartH * (1f - normAcwr)
                    series3Points.add(Offset(x, yAcwr))
                }

                // Draw X-axis label
                drawText(
                    textMeasurer = textMeasurer,
                    text = dp.label,
                    topLeft = Offset(x - 14f, h - padBottom + 10f),
                    style = TextStyle(fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                )
            }

            // Helper to build smooth cubic curve path
            fun buildSmoothPath(points: List<Offset>): Path {
                val path = Path()
                if (points.isEmpty()) return path
                path.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX = (p0.x + p1.x) / 2f
                    path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }
                return path
            }

            fun drawAreaAndLine(points: List<Offset>, color: Color) {
                if (points.isEmpty()) return
                val linePath = buildSmoothPath(points)

                // Fill gradient area under curve
                val fillPath = Path()
                fillPath.addPath(linePath)
                fillPath.lineTo(points.last().x, padTop + chartH)
                fillPath.lineTo(points.first().x, padTop + chartH)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.25f), color.copy(alpha = 0.02f)),
                        startY = padTop,
                        endY = padTop + chartH
                    )
                )

                // Stroke curve line
                drawPath(
                    path = linePath,
                    color = color,
                    style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Data point circles
                points.forEach { pt ->
                    drawCircle(color = Color.White, radius = 4.5f, center = pt)
                    drawCircle(color = color, radius = 3.5f, center = pt)
                }
            }

            // Draw Series based on selection
            if (chartMode == "Growth Over Time") {
                if (showSeries3) drawAreaAndLine(series3Points, emeraldAccent)
                if (showSeries2) drawAreaAndLine(series2Points, purpleAccent)
                if (showSeries1) drawAreaAndLine(series1Points, tealAccent)
            } else {
                if (showSeries3) drawAreaAndLine(series3Points, emeraldAccent)
                if (showSeries2) drawAreaAndLine(series2Points, blueAccent)
                if (showSeries1) drawAreaAndLine(series1Points, amberAccent)
            }

            // 3. Interactive Touch Crosshair & Tooltip Overlay
            touchX?.let { tx ->
                val clampedX = tx.coerceIn(padLeft, w - padRight)
                // Find closest index
                val closestIdx = ((clampedX - padLeft) / stepX).roundToInt().coerceIn(0, data.size - 1)
                val activeData = data[closestIdx]
                val pointX = padLeft + closestIdx * stepX

                // Vertical Crosshair Line
                drawLine(
                    color = crosshairColor,
                    start = Offset(pointX, padTop),
                    end = Offset(pointX, padTop + chartH),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )

                // Highlight Active Dots
                if (chartMode == "Growth Over Time") {
                    if (showSeries1) drawCircle(color = tealAccent, radius = 6f, center = series1Points[closestIdx])
                    if (showSeries2) drawCircle(color = purpleAccent, radius = 6f, center = series2Points[closestIdx])
                    if (showSeries3) drawCircle(color = emeraldAccent, radius = 6f, center = series3Points[closestIdx])
                } else {
                    if (showSeries1) drawCircle(color = amberAccent, radius = 6f, center = series1Points[closestIdx])
                    if (showSeries2) drawCircle(color = blueAccent, radius = 6f, center = series2Points[closestIdx])
                    if (showSeries3) drawCircle(color = emeraldAccent, radius = 6f, center = series3Points[closestIdx])
                }

                // Tooltip Box (Floating Card above point)
                val tooltipWidth = 150f
                val tooltipHeight = 65f
                val tooltipX = (pointX - tooltipWidth / 2f).coerceIn(10f, w - tooltipWidth - 10f)
                val tooltipY = 8f

                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(tooltipX, tooltipY),
                    size = androidx.compose.ui.geometry.Size(tooltipWidth, tooltipHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )

                // Tooltip Content
                drawText(
                    textMeasurer = textMeasurer,
                    text = activeData.label,
                    topLeft = Offset(tooltipX + 8f, tooltipY + 5f),
                    style = TextStyle(fontSize = 9.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Bold)
                )

                if (chartMode == "Growth Over Time") {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "Speed: ${activeData.speedKmh} km/h • VO2: ${activeData.vo2Max}",
                        topLeft = Offset(tooltipX + 8f, tooltipY + 22f),
                        style = TextStyle(fontSize = 8.sp, color = Color(0xFF2DD4BF), fontWeight = FontWeight.Medium)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "Tactical Rating: ${activeData.tacticalRating}%",
                        topLeft = Offset(tooltipX + 8f, tooltipY + 38f),
                        style = TextStyle(fontSize = 8.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Medium)
                    )
                } else {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "Load: ${activeData.trainingLoadAu.toInt()} AU • Int: ${activeData.intensityPercent.toInt()}%",
                        topLeft = Offset(tooltipX + 8f, tooltipY + 22f),
                        style = TextStyle(fontSize = 8.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Medium)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "ACWR: ${activeData.acwrRatio} (Safe Range)",
                        topLeft = Offset(tooltipX + 8f, tooltipY + 38f),
                        style = TextStyle(fontSize = 8.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@Composable
fun LegendToggleChip(
    label: String,
    color: Color,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) color.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
        border = if (isActive) BorderStroke(1.dp, color.copy(alpha = 0.5f)) else null,
        modifier = Modifier.clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) color else Color(0xFF94A3B8))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) Color(0xFF0F172A) else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun SummaryPill(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
