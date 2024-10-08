package com.yeuchi.canvaslines.curves

import android.graphics.PointF
import com.yeuchi.canvaslines.ContourKnots

/**
 *
 */
class BezierQuad : ContourKnots() {

    private fun requant(i: Int): Int {
        return when {
            i < 1 -> 1
            i > knots.size - 2 -> knots.size - 2
            else -> i
        }
    }

    fun getPoints(): ArrayList<PointF> {
        val numKnots = getNumKnots()
        return when (numKnots) {
            0,
            1,
            2 -> knots

            else -> interpolateAll()
        }
    }

    /**
     * Assumption: 3 or more points
     */
    private fun interpolateAll(): ArrayList<PointF> {
        val listPoints = arrayListOf<PointF>()
//        val start = knots[0].x.toInt() + 1
//        val end = knots.last().x.toInt() - 1
//
//        /* calculate all points */
//        for (i in start..end) {
//
//            /* find nearest knot */
//            val index = requant(bisection(i.toFloat()))
//            val p0 = knots[index - 1]
//
//            /* calculate control point or skip this if it is control point, not knot */
//            val p1 = calculateControlPoint(index)
//            val p2 = knots[index + 1]
//
//            val y = doBezier(i.toFloat(), p0, p1, p2)
//            listPoints.add(PointF(i.toFloat(), y))
//        }
        for(i in 1 .. knots.size-2 step 2) {
            val p0 = knots[i-1]
            val p1 = knots[i]
            val p2 = knots[i+1]
            for(j in p0.x.toInt() until p2.x.toInt()) {
                val y = doBezier(j.toFloat(), p0, p1, p2)
                listPoints.add(PointF(j.toFloat(), y))
            }
        }
        listPoints.add(knots[knots.size-1])
        return listPoints
    }

    /**
     * Calculate point on bezier curve
     */
    private fun doBezier(x: Float, p0: PointF, p1: PointF, p2: PointF): Float {
        /* x: 0 to 1 range */
        val t = (x - p0.x) / (p2.x - p0.x)
        // val x = (1 - t) * (1 - t) * knots[0].x + 2 * (1 - t) * t * knots[1].x + t * t * knots[2].x
        val y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
        return y
    }

    /**
     * Calculate Control Point from knots i-1, i, i+1
     * So line will go through all knots
     */
    private fun calculateControlPoint(index: Int): PointF {
        /* index-1, index, index+1 must exist */
        val t = (knots[index].x - knots[index - 1].x) / (knots[index + 1].x - knots[index - 1].x)
        // val x = (1 - t) * (1 - t) * knots[0].x + 2 * (1 - t) * t * knots[1].x + t * t * knots[2].x
        //val y = (1 - t) * (1 - t) * knots[index-1].y + 2 * (1 - t) * t * knots[index].y + t * t * knots[index+1].y
        val controlY = (knots[index].y - ((1 - t) * (1 - t) * knots[index-1].y) - (t * t * knots[index+1].y)) / (2 * (1 - t) * t)
        return PointF(knots[index].x, controlY)
    }
}