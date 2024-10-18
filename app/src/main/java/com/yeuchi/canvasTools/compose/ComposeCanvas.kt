package com.yeuchi.canvasTools.compose

import android.graphics.PointF
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.yeuchi.canvasTools.ContourType
import com.yeuchi.canvasTools.MainViewModel
import com.yeuchi.canvaslines.ContourKnots
import com.yeuchi.canvaslines.lines.Line
import com.yeuchi.canvaslines.lines.LinearRegression


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposeCanvas(viewModel: MainViewModel) {

    viewModel.apply {
        val radioOptions = listOf(
            ContourType.Line.name,
            ContourType.BezierCubic.name,
            ContourType.BezierQuad.name,
            ContourType.CubicSpline.name,
            ContourType.LinearRegression.name
        )
        val selectedOption = remember { mutableStateOf(radioOptions[0]) }

        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> select(PointF(it.rawX, it.rawY))
                    MotionEvent.ACTION_MOVE -> drag(PointF(it.rawX, it.rawY))
                    MotionEvent.ACTION_UP -> save(PointF(it.rawX, it.rawY))
                    else -> false
                }
                true
            })
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            knots.forEach {
                drawCircle(Color.Blue, radius = 20F, center = Offset(it.x, it.y))
            }

            if (points.size > 1) {
                Path().let { path ->
                    when (viewModel.contourType) {
                        ContourType.BezierCubic -> drawBezierCubic(points, path, this)

                        ContourType.LinearRegression -> drawLinearRegression(
                            points,
                            path,
                            this,
                            contourObject as LinearRegression
                        )

//                        ContourType.Line,
//                        ContourType.CubicSpline,
                        else -> drawLine(points, path, this)
                    }
                }
            }
        }

        Column(Modifier.padding(30.dp, 110.dp, 0.dp, 0.dp)) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption.value),
                            onClick = {
                                selectedOption.value = text
                                chooseContourType(text, viewModel)
                            }
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption.value),
                        onClick = {
                            selectedOption.value = text
                            chooseContourType(text, viewModel)
                        },
                        Modifier.testTag("btn_$text")
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

private fun chooseContourType(text: String, viewModel: MainViewModel) {
    val type = ContourType.valueOf(text)
    viewModel.setContourType(type)
}

fun drawLine(points: List<PointF>, path: Path, drawScope: DrawScope) {
    path.moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        path.lineTo(points[i].x, points[i].y)
        drawScope.drawPath(
            path = path,
            Brush.verticalGradient(
                colors = listOf(
                    Color.DarkGray,
                    Color.DarkGray
                )
            ),
            style = Stroke(width = 1f, cap = StrokeCap.Round)
        )
    }
}

fun drawBezierCubic(points: List<PointF>, path: Path, drawScope: DrawScope) {
    val size = points.size
    if (size > 2) {
        val conPoint1 = ArrayList<PointF>()
        val conPoint2 = ArrayList<PointF>()
        for (i in 1 until size) {
            val prev = points[i - 1]
            val p = points[i]
            conPoint1.add(PointF((p.x + prev.x) / 2, prev.y))
            conPoint2.add(PointF((p.x + prev.x) / 2, p.y))
        }
        val first = points[0]
        first.apply {
            path.moveTo(first.x, first.y)

            for (i in 1..<size) {
                val p = points[i]
                path.cubicTo(
                    conPoint1[i - 1].x, conPoint1[i - 1].y,
                    conPoint2[i - 1].x, conPoint2[i - 1].y,
                    p.x, p.y
                )
            }
            drawScope.drawPath(
                path,
                Brush.verticalGradient(
                    colors = listOf(
                        Color.DarkGray,
                        Color.DarkGray
                    )
                ),
                style = Stroke(width = 1f, cap = StrokeCap.Round)
            )
        }
    } else {
        drawLine(points, path, drawScope)
    }
}

fun drawLinearRegression(
    points: List<PointF>,
    path: Path,
    drawScope: DrawScope,
    regression: LinearRegression
) {
    var regressionLine: Line? = null
    val trianglePath = when (points.size) {

        0, 1 -> {
            Path().let {
                it.moveTo(drawScope.size.width * .20f, drawScope.size.height * .77f)
                it.lineTo(drawScope.size.width * .20f, drawScope.size.height * 0.95f)
                it.lineTo(drawScope.size.width * .37f, drawScope.size.height * 0.86f)
                it.close()
                it
            }
        }

        //1 -> {}

        2 -> {
            Path().let { path ->
                path.moveTo(points[0].x, points[0].y)
                path.lineTo(points[1].x, points[1].y)
                path
            }
        }

        else -> {
            val (a, b) = regression.findLeastSquare(
                points,
                points[0].x,
                points[points.size - 1].x
            )
            regressionLine = Line(a, b)
            Path().let { path ->
                path.moveTo(a.x, a.y)
                path.lineTo(b.x, b.y)
                path
            }
        }
    }

    val colors = listOf(Color.Green, Color(0xFF0277fe))
    drawScope.drawPath(
        path = trianglePath,
        Brush.verticalGradient(colors = colors),
        style = Stroke(width = 5f, cap = StrokeCap.Round)
    )

    /**
     * Draw tangent lines
     */

    regressionLine?.let { line ->
        points.forEach { p ->
            val tangent = line.findNormalLineFrom(p)
            val pp = line.findIntersectionFrom(tangent)
            pp?.let {
                Path().let { path ->
                    path.moveTo(p.x, p.y)
                    path.lineTo(pp.x, pp.y)

                    drawScope.drawPath(
                        path = path,
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.DarkGray,
                                Color.DarkGray
                            )
                        ),
                        style = Stroke(width = 5f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}