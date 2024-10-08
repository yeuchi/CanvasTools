package com.yeuchi.canvaslines.curves

import android.graphics.PointF
import com.yeuchi.canvaslines.ContourKnots

// ============================================================================
// Module:		CubicSpline.js
//
// Description:	2D Cubic Spline interpolation in javascript as defined by reference.
//
// Reference:	Numerical Recipes in C 2nd Edition, Press, Teukolsky, Vetterling, Flannery, pg.113
//
// Authors:		William H. Press
//				William T. Vetterling
//				Saul A. Teukolsky
//				Brian P. Flannery
//
//				C.T. Yeung (porting from C into javascript)
//
// Input:		pSrcX - array of anchors' x (assume positive real numbers)
//				pSrcY - array of anchors' y (assume positive real numbers)
//
// Output:		getY(x) => returns interpolated Y value.
//
// WARNING:     must invoke formulate() after all knots are added before getY(x)
//
// History:
// 20Nov11		ported it to javascript, working in HTML5 canvas.			cty
// 12Nov17      upgrade to ECMAScript6                                      cty
// ============================================================================
class CubicSpline : ContourKnots() {
    private var arrayB: FloatArray? = null
    private var arrayC: FloatArray? = null
    private var arrayD: FloatArray? = null
    private var arrayH: FloatArray? = null
    private var arraySIG: FloatArray? = null
    private var arrayL: FloatArray? = null
    private var arrayU: FloatArray? = null
    private var arrayZ: FloatArray? = null

    override fun clear() {
        super.clear()

        arrayB = null
        arrayC = null
        arrayD = null
        arrayH = null
        arraySIG = null
        arrayL = null
        arrayU = null
        arrayZ = null
    }

    private fun initIntermediate(size: Int) {
        arrayB = FloatArray(size)
        arrayC = FloatArray(size)
        arrayD = FloatArray(size)
        arrayH = FloatArray(size)
        arraySIG = FloatArray(size)
        arrayL = FloatArray(size)
        arrayU = FloatArray(size)
        arrayZ = FloatArray(size)
    }

    fun formulate() {
        val numKnots = knots.size
        if (numKnots < 3)
            return

        initIntermediate(numKnots)

        // Theorem 3.11		[A].[x] = [b]					[A] -> n x n Matrix
        //													[b] -> n x n Matrix
        //													[x] -> c[] 0..n
        //	STEP 1		eq. 4 (pg. 134)
        for (aa in 0 until numKnots - 1) {
            arrayH?.set(
                aa,
                (knots[aa + 1].x - knots[aa].x)
            ) // [A], Hj = Xj+1 - Xj
        }

        // STEP 2
        for (aa in 1 until numKnots - 1) {
            // 0 -> n-1
            arraySIG?.set(
                aa, (3F / arrayH!!.get(aa) * (knots.get(aa + 1).y - knots.get(aa).y) -
                        3F / arrayH!!.get(aa - 1) * (knots.get(aa).y - knots.get(aa - 1).y))
            )
        }

        // STEP 3
        arrayL?.set(0, 0F)
        arrayU?.set(0, 0F)
        arrayZ?.set(0, 0F)
        arraySIG?.set(0, 0F)

        // STEP 4
        for (aa in 1 until numKnots - 1) {
            arrayL?.set(
                aa,
                (2F * (knots.get(aa + 1).x - knots.get(aa - 1).x))- (arrayH!!.get(aa - 1) * arrayU!!.get(
                    aa - 1
                ))
            )

            arrayU?.set(aa, arrayH!!.get(aa) / arrayL!!.get(aa))

            arrayZ?.set(
                aa,
                (arraySIG!!.get(aa) - (arrayH!!.get(aa - 1) * arrayZ!!.get(aa - 1))) / arrayL!!.get(aa))
        }

        // STEP 5		TAIL BOUNDARY @ 0
        arrayL?.set(numKnots - 1, 1F)
        arrayZ?.set(numKnots - 1, 0F)
        arrayC?.set(numKnots - 1, 0F)

        // STEP 6
        for (aa in numKnots - 2 downTo 0) {
            arrayC?.set(
                aa,
                arrayZ!!.get(aa) - arrayU!!.get(aa) * arrayC!!.get(aa + 1)
            ) // Theorem 3.11

            arrayB?.set(
                aa, ((knots[aa + 1].y - knots[aa].y) / arrayH!!.get(aa)
                        - (arrayH!!.get(aa) * (arrayC!!.get(aa + 1) + 2 * arrayC!!.get(aa)) / 3F))
            ) // eq. 10

            arrayD?.set(
                aa,
                (arrayC!!.get(aa + 1) - arrayC!!.get(aa)) / (3F * arrayH!!.get(aa))
            ) // eq. 11
        }
    }

    /*
    * calculate the y value
    * - intended to be a private method
    * WARNING: must have invoked formulate() before
    * RETURN: -1 if invalid, positive value if ok.
    */
    fun doCubicSpline(
        x: Float,                // [in] x value
        i: Int
    ): Float {            // [in] index of anchor to use
        val Y = knots[i].y +
                arrayB?.get(i)!! * (x - knots[i].x) +
                arrayC?.get(i)!! * Math.pow((x - knots[i].x).toDouble(), 2.0) +
                arrayD?.get(i)!! * Math.pow((x - knots[i].x).toDouble(), 3.0)
        return Y.toFloat()
    }

    fun interpolateY(x: Float): Float {
        val index = bisection(x)
        return if (knots[index].x == x) {
            knots[index].y
        } else {
            doCubicSpline(x, index)
        }
    }

    /*
     * calculated points resolution by finding x of min distance.
     * then add 1 point in between to satisfy Nyquist frequency 2X ?
     */
    fun getPoints(): ArrayList<PointF> {
        val numKnots = getNumKnots()
        return when (numKnots) {
            0 -> arrayListOf()
            1 -> arrayListOf<PointF>(knots[0])
            2 -> arrayListOf<PointF>(
                knots[0],
                knots[1]
            )
            else -> interpolateAll()
        }
    }

    private fun interpolateAll(): ArrayList<PointF> {
        val listPoints = ArrayList<PointF>()
        val start = knots[0].x.toInt() + 1
        val end = knots.get(getNumKnots() - 1).x.toInt() - 1
        for (i in start..end) {
            val y = interpolateY(i.toFloat())
            listPoints.add(PointF(i.toFloat(), y))
        }
        return listPoints
    }
}