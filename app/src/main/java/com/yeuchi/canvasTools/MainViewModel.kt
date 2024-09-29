package com.yeuchi.canvasTools

import android.graphics.PointF
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeuchi.canvaslines.ContourKnots
import com.yeuchi.canvaslines.curves.BezierCubic
import com.yeuchi.canvaslines.curves.BezierQuad
import com.yeuchi.canvaslines.curves.CubicSpline
import com.yeuchi.canvaslines.lines.Line
import com.yeuchi.canvaslines.lines.LinearRegression
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class MainViewModel @Inject constructor() : ViewModel() {

    private val _event = MutableSharedFlow<MainViewEvent>()
    val event: SharedFlow<MainViewEvent> = _event

// Migrate to Kotlin 2.0 https://kotlinlang.org/docs/k2-compiler-migration-guide.html#support-in-ides
// got everything to compile and run but the app does not work correctly. IDE is not ready
//   val event: SharedFlow<MainViewEvent>
//       field = MutableSharedFlow<MainViewEvent>()

    private var contour: ContourKnots = ContourFactory.create(ContourType.DEFAULT_LINE)

    fun setContourType(type: ContourType) {
        val knots = contour.getKnots()
        contour = ContourFactory.create(type)
        contour.setKnots(ArrayList<PointF>(knots))
    }

    val points: List<PointF>
        get() {
            when (contour) {
                /*
                 * TODO need to implement correct get() here
                 */
                is BezierQuad -> return (contour as BezierQuad).getPoints()

                is BezierCubic -> return contour.getKnots()

                is CubicSpline ->
                    (contour as CubicSpline).apply {
                        formulate()
                        return getPoints()
                    }

                is LinearRegression -> return contour.getKnots()

//                is Line,
                else -> return contour.getKnots()
            }
        }

    val knots: List<PointF>
        get() {
            return contour.getKnots()
        }

    fun select(p: PointF) {
        /* TODO */
    }

    fun drag(p: PointF) {
        /* TODO */
    }

    fun save(p: PointF) {
        viewModelScope.launch(Dispatchers.IO) {
            contour.apply {
                insert(p)
            }

            _event.emit(MainViewEvent.invalidated)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            contour.clear()
            _event.emit(MainViewEvent.invalidated)
        }
    }
}

sealed class MainViewEvent() {
    object invalidated : MainViewEvent()
}