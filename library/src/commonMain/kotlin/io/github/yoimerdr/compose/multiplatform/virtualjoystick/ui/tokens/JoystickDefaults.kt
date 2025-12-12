package io.github.yoimerdr.compose.multiplatform.virtualjoystick.ui.tokens

import io.github.yoimerdr.compose.multiplatform.virtualjoystick.core.control.BackgroundType
import io.github.yoimerdr.compose.multiplatform.virtualjoystick.core.control.DirectionType
import io.github.yoimerdr.compose.multiplatform.virtualjoystick.core.geometry.Radius

/**
 * Provides default values for the virtual joystick configuration.
 */
object JoystickDefaults {
    /**
     * The default interval in milliseconds for joystick hold updates.
     */
    val interval: Long
        get() = 175L

    /**
     * The default radius of the joystick `dead zone`.
     */
    val radius: Radius
        get() = Radius.Ratio(0.2f)

    /**
     * The default direction type for the joystick.
     */
    val directionType
        get() = DirectionType.Complete

    /**
     * The default background type for the joystick.
     */
    val backgroundType: BackgroundType
        get() = BackgroundType.Default
}