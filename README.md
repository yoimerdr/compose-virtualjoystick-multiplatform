# Compose Virtual Joystick Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/io.github.yoimerdr.compose/virtualjoystick)](https://central.sonatype.com/artifact/io.github.yoimerdr.compose/virtualjoystick)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.2.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.9.3-blue)](https://github.com/JetBrains/compose-multiplatform)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-DB413D.svg?style=flat)
![badge-desktop](https://img.shields.io/badge/platform-web-59B6EC.svg?style=flat)

<div style="text-align: center;">
    <img src="./images/sample_android_app.mp4" height="500" alt="Sample Android Application">
</div>

A customizable virtual joystick library for **Compose Multiplatform** that provides touch-based directional controls with configurable dead zones, multiple direction types, and customizable visual styles.

## Features

- **Multiplatform Support**: Works on Android, iOS, Desktop (JVM), Web (JS), and WASM
- **Fully Customizable**: Control appearance with custom drawing scopes
- **Multiple Direction Types**: Support for 4-direction and 8-direction control schemes
- **Dead Zone Configuration**: Adjustable invalid radius for precise control
- **Event System**: Reactive event flow with move start, moving, held, and end states
- **Built-in Backgrounds**: Multiple pre-built background styles including D-pad variants
- **Compose-First**: Built with Jetpack Compose best practices

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("io.github.yoimerdr.compose:virtualjoystick:x.x.x")
}
```

## Quick Start

### Simple Usage

```kotlin
@Composable
fun SampleJoystick() {
    val joystickState = rememberJoystickState()
    
    VirtualJoystick(
        state = joystickState,
        modifier = Modifier.size(200.dp),
        properties = CircleDrawDefaults.properties()
    ) { snapshot ->
        // Handle joystick movement
        println("Direction: ${snapshot.direction}, Strength: ${snapshot.strength}")
    }
}
```

### Usage with Events

```kotlin
@Composable
fun SampleJoystick() {
    val joystickState = rememberJoystickState(
        invalidRadius = Radius.Ratio(0.15f),
        directionType = DirectionType.Complete
    )
    val eventHolder = rememberJoystickEventHolder(joystickState)
    
    LaunchedEffect(eventHolder) {
        eventHolder.events.collect { event ->
            when (event) {
                is JoystickMoveStart -> println("Started: ${event.snapshot}")
                is JoystickMoving -> println("Moving: ${event.snapshot}")
                is JoystickMoveHeld -> println("Held: ${event.snapshot}")
                is JoystickMoveEnd -> println("Ended: ${event.snapshot}")
            }
        }
    }
    
    VirtualJoystick(
        state = joystickState,
        modifier = Modifier.size(200.dp),
        properties = CircleDrawDefaults.properties(),
        holder = eventHolder
    )
}
```

### Custom Appearance

```kotlin
@Composable
fun CustomStyledJoystick() {
    val state = rememberJoystickState()

    BaseVirtualJoystick(
        state = state,
        modifier = Modifier.size(200.dp),
        onMove = {

        }
    ) {
        // Custom background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                brush = SolidColor(Color.Gray.copy(alpha = 0.3f)),
                radius = state.radius
            )
        }

        // Custom indicator
        JoystickCanvas {
            drawCircle(
                properties = CircleDrawDefaults.properties(
                    brush = SolidColor(Color.Yellow),
                    radius = Radius.Fixed(state.radius * 0.3f),
                )
            )
        }
    }
}
```

## Documentation

### Direction Types

- **`DirectionType.Complete`**: Returns all 8 directions (Right, DownRight, Down, DownLeft, Left, UpLeft, Up, UpRight, None)
- **`DirectionType.Simple`**: Returns only 4 cardinal directions (Right, Down, Left, Up, None)

### Background Types

The library includes several pre-built background styles:

- `BackgroundType.Default`: The default background style
- `BackgroundType.Modern`: Modern styled background
- `BackgroundType.Classic`: Standard circular background
- `BackgroundType.DpadModern`: Modern D-pad style
- `BackgroundType.DpadClassic`: Classic D-pad style
- `BackgroundType.DpadStandard`: Standard D-pad style

### Joystick State

The `JoystickState` provides:
- `offset`: Current joystick offset position
- `direction`: Current direction enum value
- `strength`: Movement strength (0.0 to 1.0)
- `angle`: Current angle in degrees
- `isInvalid`: Whether the joystick is in the dead zone
- `goto`: Method for move the joystick to the specified position (or direction when use extension)

### Event Types

- **`JoystickMoveStart`**: Triggered when touch begins
- **`JoystickMoving`**: Triggered during movement
- **`JoystickMoveHeld`**: Triggered when held in position
- **`JoystickMoveEnd`**: Triggered when touch ends

## Sample App

The `composeApp` module contains a comprehensive demo application showcasing:
- Different drawing types (Circle, Arc, Wedge, etc.)
- Customizable colors and styles
- Event handling examples
- Direction type switching
- Real-time state visualization

Run the sample:
```bash
./gradlew :composeApp:run
```

## Platform Support

| Platform | Status    |
|----------|-----------|
| Android  | API 21+   |
| iOS      | iOS 13+ * |
| Desktop  | JVM 11+   |
| Web (JS) | Browser   |
| WASM     | Browser   |

> **iOS Note:** The iOS 13+ minimum version is inherited from the default Kotlin Multiplatform configuration. The library has not been fully tested on all iOS versions, so compatibility with iOS 13+ is not 100% guaranteed. If you encounter any issues on specific iOS versions, please report them in the [issue tracker](https://github.com/yoimerdr/compose-virtualjoystick-multiplatform/issues).

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yoimerdr/compose-virtualjoystick-multiplatform.git

# Build the library
./gradlew :library:build

# Run tests
./gradlew :library:test

# Publish to Maven Local
./gradlew :library:publishToMavenLocal
```

### Requirements

- Kotlin 2.2.21+
- Compose Multiplatform 1.9.3+
- Android minSdk 21+
- iOS 13+
- JVM 11+

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

## Links

- [GitHub Repository](https://github.com/yoimerdr/compose-virtualjoystick-multiplatform)
- [Maven Central](https://central.sonatype.com/artifact/io.github.yoimerdr.compose/virtualjoystick)
- [Issue Tracker](https://github.com/yoimerdr/compose-virtualjoystick-multiplatform/issues)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

## Acknowledgments

- Built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Published using [Vanniktech Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)

---

**If you find this library useful, please consider giving it a ⭐ on GitHub!**
