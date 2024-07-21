package com.yeuchi.canvasTools

import android.graphics.PointF
import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeuchi.canvaslines.curves.CubicSpline
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

    val cubicSpline = CubicSpline()

    val points:List<PointF>
        get() {
            return cubicSpline.getPoints()
        }

    val knots:List<PointF>
        get() {
            return cubicSpline.knots
        }

    fun select(p: PointF) {
        /* TODO */
    }

    fun drag(p: PointF) {
        /* TODO */
    }

    fun save(p: PointF) {
        viewModelScope.launch(Dispatchers.IO) {
            cubicSpline.apply {
                insert(p)
                formulate()
            }

            _event.emit(MainViewEvent.invalidated)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            cubicSpline.clear()
            _event.emit(MainViewEvent.invalidated)
        }
    }
}

sealed class MainViewEvent() {
    object invalidated : MainViewEvent()
}