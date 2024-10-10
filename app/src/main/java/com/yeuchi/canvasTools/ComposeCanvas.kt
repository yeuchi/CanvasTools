package com.yeuchi.canvasTools

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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposeCanvas(viewModel: MainViewModel) {
    viewModel.apply {
        val radioOptions = listOf("line", "bezier_quad", "cubic_spline")
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
                    path.moveTo(points[0].x, points[0].y)
                    drawPath(points, path, this)
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

private fun chooseContourType(text:String, viewModel: MainViewModel) {
    val type = when (text) {
        "bezier_quad" -> ContourType.BEZIER_QUAD
        "cubic_spline" -> ContourType.CUBIC_SPLINE
//                                   "line"
        else -> ContourType.DEFAULT_LINE
    }
    viewModel.setContourType(type)
}

fun drawPath(points: List<PointF>, path: Path, drawScope: DrawScope) {
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
