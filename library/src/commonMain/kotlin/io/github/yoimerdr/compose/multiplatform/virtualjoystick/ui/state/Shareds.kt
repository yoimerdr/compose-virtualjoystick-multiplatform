package io.github.yoimerdr.compose.multiplatform.virtualjoystick.ui.state

import androidx.annotation.FloatRange
import androidx.compose.ui.geometry.Offset
import io.github.yoimerdr.compose.multiplatform.virtualjoystick.core.control.Direction
import io.github.yoimerdr.compose.multiplatform.virtualjoystick.core.geometry.SQRT_2

/**
 * A listener for joystick move events, receiving a snapshot of the joystick state.
 */
typealias JoystickMoveListener = (JoystickSnapshot) -> Unit


/**
 * Moves the joystick to the position corresponding to the given direction.
 */
fun JoystickState.goto(direction: Direction) {
    goto(
        getPosition(direction)
    )
}

/**
 * Calculates the position offset for the given direction and strength.
 */
fun JoystickState.getPosition(
    direction: Direction,
    @FloatRange(
        from = 0.0, to = 1.0
    ) strength: Float = 1f,
): Offset {
    require(strength in 0f..1f) { "Magnitude must be between 0 and 1, got $strength" }

    val centerPos = center
    val radiusValue = radius

    val force = strength.coerceIn(0f, 1f)
    if (force == 0f || direction == Direction.None) return centerPos.copy()

    val scaledRadius = radiusValue * force

    return when (direction) {
        Direction.None -> centerPos.copy()
        Direction.Up -> Offset(centerPos.x, centerPos.y - scaledRadius)
        Direction.Down -> Offset(centerPos.x, centerPos.y + scaledRadius)
        Direction.Left -> Offset(centerPos.x - scaledRadius, centerPos.y)
        Direction.Right -> Offset(centerPos.x + scaledRadius, centerPos.y)
        else -> {
            // Diagonal directions: scale by 1/sqrt(2) to maintain distance
            val diagonalRadius = (scaledRadius / SQRT_2).toFloat()
            when (direction) {
                Direction.UpLeft -> Offset(
                    centerPos.x - diagonalRadius, centerPos.y - diagonalRadius
                )

                Direction.UpRight -> Offset(
                    centerPos.x + diagonalRadius, centerPos.y - diagonalRadius
                )

                Direction.DownLeft -> Offset(
                    centerPos.x - diagonalRadius, centerPos.y + diagonalRadius
                )

                Direction.DownRight -> Offset(
                    centerPos.x + diagonalRadius, centerPos.y + diagonalRadius
                )

                else -> centerPos.copy()
            }
        }
    }
}

/**
 * Converts the JoystickState to a JoystickSnapshot.
 */
fun JoystickState.toSnapshot(): JoystickSnapshot {
    return JoystickSnapshot(direction, position, strength, angle)
}
