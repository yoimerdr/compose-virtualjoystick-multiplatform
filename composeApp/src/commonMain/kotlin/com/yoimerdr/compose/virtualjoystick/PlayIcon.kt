package com.yoimerdr.compose.virtualjoystick

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlayIcon: ImageVector
    get() {
        if (_PlayIcon != null) {
            return _PlayIcon!!
        }
        _PlayIcon = ImageVector.Builder(
            name = "PlayIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 18f)
                verticalLineTo(6f)
                lineToRelative(8f, 6f)
                lineToRelative(-8f, 6f)
                close()
            }
        }.build()

        return _PlayIcon!!
    }

@Suppress("ObjectPropertyName")
private var _PlayIcon: ImageVector? = null
