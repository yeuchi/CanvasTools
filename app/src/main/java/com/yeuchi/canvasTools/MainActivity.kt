package com.yeuchi.canvasTools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.yeuchi.canvasTools.ui.theme.CanvasToolsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
            viewModel.event.asLiveData().observeForever {
                when (it) {
                    is MainViewEvent.Invalidated -> onInvalidated()
                }
            }
        onInvalidated()
    }

    private fun onInvalidated() {
        setContent {
            CanvasToolsTheme {
                // A surface container using the 'background' color from the theme
                ComposeCanvas(viewModel)

                FloatingActionButton(
                    modifier = Modifier.padding(45.dp, 40.dp).testTag("fab_delete"),
                    onClick = { onClickClear() },
                ) {
                    Icon(Icons.Filled.Delete, "Delete")
                }
            }
        }
    }

    private fun onClickClear() {
        viewModel.deleteAll()
    }
}