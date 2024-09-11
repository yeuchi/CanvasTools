package com.yeuchi.canvasTools

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun is_radio_button_line_Visible_test() {
        composeTestRule.onNodeWithText("line").assertIsDisplayed()
    }

    @Test
    fun is_radio_button_cubic_spline_Visible_test() {
        composeTestRule.onNodeWithText("cubic_spline").assertIsDisplayed()
    }

    @Test
    fun is_fab_delete_Visible_test() {
        composeTestRule.onNodeWithTag("fab_delete").assertIsDisplayed()
    }
}