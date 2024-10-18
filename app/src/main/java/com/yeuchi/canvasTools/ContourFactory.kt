package com.yeuchi.canvasTools

import com.yeuchi.canvaslines.ContourKnots
import com.yeuchi.canvaslines.curves.BezierCubic
import com.yeuchi.canvaslines.curves.BezierQuad
import com.yeuchi.canvaslines.curves.CubicSpline
import com.yeuchi.canvaslines.lines.LinearRegression

object ContourFactory {

    fun create(pointsType: ContourType): ContourKnots {

        return when (pointsType) {
            ContourType.BezierCubic -> BezierCubic()
            ContourType.BezierQuad -> BezierQuad()
            ContourType.CubicSpline -> CubicSpline()
            ContourType.LinearRegression -> LinearRegression()
            else -> ContourKnots()
        }
    }
}

enum class ContourType {
    Line,
    BezierCubic,
    BezierQuad,
    CubicSpline,
    LinearRegression
}