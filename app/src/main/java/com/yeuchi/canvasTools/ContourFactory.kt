package com.yeuchi.canvasTools

import com.yeuchi.canvaslines.ContourKnots
import com.yeuchi.canvaslines.curves.BezierCubic
import com.yeuchi.canvaslines.curves.BezierQuad
import com.yeuchi.canvaslines.curves.CubicSpline
import com.yeuchi.canvaslines.lines.LinearRegression

object ContourFactory {

    fun create(pointsType: ContourType): ContourKnots {

        return when (pointsType) {
            ContourType.BEZIER_CUBIC -> BezierCubic()
            ContourType.BEZIER_QUAD -> BezierQuad()
            ContourType.CUBIC_SPLINE -> CubicSpline()
            ContourType.LINEAR_REGRESSION -> LinearRegression()
            else -> ContourKnots()
        }
    }
}

enum class ContourType {
    BEZIER_CUBIC,
    BEZIER_QUAD,
    CUBIC_SPLINE,
    LINEAR_REGRESSION,
    DEFAULT_LINE
}