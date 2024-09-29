package com.yeuchi.canvaslines.lines

import android.graphics.PointF
import com.yeuchi.canvaslines.ContourKnots

/**
 * Discrete Least Squares Approximation
 * Numerical Analysis 5th edition by Burden Chapter 8.1 Page 436-442
 */
class LinearRegression: ContourKnots() {

    fun findLeastSquare(points: List<PointF>,
                             startX: Float,
                             endX: Float):
            Pair<PointF, PointF> {


        // find slope
        var m = 0F
        var b = 0F
        var sumXY = 0F
        var sumX = 0F
        var sumY = 0F
        var sumX2 = 0F

        for (p in points) {
            sumXY += p.x * p.y
            sumX += p.x
            sumY += p.y
            sumX2 += p.x * p.x
        }

        m = (points.size * sumXY - sumX * sumY) / (points.size * sumX2 - sumX * sumX)
        b = (sumY - m * sumX) / points.size

        // return regression line end points
        val p0 = PointF()
        p0.x = startX
        p0.y = b

        val p1 = PointF()
        p1.x = endX
        p1.y = m * endX + b

        return Pair(p0, p1)
    }
}
