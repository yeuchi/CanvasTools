package com.yeuchi.canvasTools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.yeuchi.canvasTools.ui.theme.CanvasToolsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.event.collect {
                when (it) {
                    is MainViewEvent.invalidated -> onInvalidated()
                }
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
                    modifier = Modifier.padding(5.dp, 5.dp).testTag("fab_delete"),
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