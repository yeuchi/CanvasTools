package com.yeuchi.canvaslines.curves

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import com.yeuchi.canvaslines.ContourKnots

/*
 * https://proandroiddev.com/drawing-bezier-curve-like-in-google-material-rally-e2b38053038c
 */
class BezierCubic : ContourKnots() {

    fun draw(canvas: Canvas) {
        Paint().let { paint ->
            paint.isAntiAlias = true
            paint.strokeWidth = 3f
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.CYAN
            paint.textSize = 65f

            val size = knots.size
            if (size > 2) {
                val path = Path()
                val conPoint1 = ArrayList<PointF>()
                val conPoint2 = ArrayList<PointF>()
                for (i in 1 until size) {
                    val prev = knots[i - 1]
                    val p = knots[i]
                    conPoint1.add(PointF((p.x + prev.x) / 2, prev.y))
                    conPoint2.add(PointF((p.x + prev.x) / 2, p.y))
                }
                val first = knots[0]
                first.apply {
                    path.reset()
                    path.moveTo(first.x, first.y)

                    for (i in 1..<size) {
                        val p = knots[i]
                        path.cubicTo(
                            conPoint1[i - 1].x, conPoint1[i - 1].y,
                            conPoint2[i - 1].x, conPoint2[i - 1].y,
                            p.x, p.y
                        )
                    }
                    canvas.drawPath(path, paint)
                    // canvas.drawTextOnPath("Over the hill; The best of both worlds; You can’t judge a book by its cover; ", path, 1F, -10F, paint)
                }
            } else {
                drawLine(canvas)
            }
        }
    }

    /*
     * Draw tangent lines
     */
    private fun drawLine(canvas: Canvas) {
        val drawPaint = Paint()
        drawPaint.apply {
            isAntiAlias = true
            strokeWidth = 3f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = Color.GREEN
        }

        val size = knots.size
        if (size > 1) {
            val path = Path()
            for (i in 0..size - 2) {
                val p = knots[i]
                val pp = knots[i + 1]
                if (p != null && pp != null) {
                    path.moveTo(p.x.toFloat(), p.y.toFloat())
                    path.lineTo(pp.x.toFloat(), pp.y.toFloat())
                }
            }
            canvas.drawPath(path, drawPaint)
        }
    }
}