package com.yoimerdr.compose.multiplatform.virtualjoystick

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VirtualJoystick",
    ) {
        App()
    }
}