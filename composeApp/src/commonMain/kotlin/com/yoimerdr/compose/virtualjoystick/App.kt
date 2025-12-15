package com.yoimerdr.compose.virtualjoystick

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import compose_virtualjoystick_multiplatform.composeapp.generated.resources.Res
import compose_virtualjoystick_multiplatform.composeapp.generated.resources.dpad_modern_d
import compose_virtualjoystick_multiplatform.composeapp.generated.resources.dpad_modern_l
import compose_virtualjoystick_multiplatform.composeapp.generated.resources.dpad_modern_r
import compose_virtualjoystick_multiplatform.composeapp.generated.resources.dpad_modern_u
import io.github.yoimerdr.compose.virtualjoystick.core.control.BackgroundType
import io.github.yoimerdr.compose.virtualjoystick.core.control.Direction
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.image.drawDirectionalImage
import io.github.yoimerdr.compose.virtualjoystick.ui.view.VirtualJoystick
import io.github.yoimerdr.compose.virtualjoystick.ui.view.rememberJoystickState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.pow

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.image.ImageDrawDefaults
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.image.drawImage
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.path.WedgeDrawDefaults
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.path.WedgeMode
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.path.drawWedge
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.ArcDrawDefaults
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.CircleArcDrawDefaults
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.CircleDrawDefaults
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.drawArc
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.drawCircle
import io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.drawCircleArc
import io.github.yoimerdr.compose.virtualjoystick.ui.state.JoystickMoveEnd
import io.github.yoimerdr.compose.virtualjoystick.ui.state.JoystickMoveHeld
import io.github.yoimerdr.compose.virtualjoystick.ui.state.JoystickMoveStart
import io.github.yoimerdr.compose.virtualjoystick.ui.state.JoystickMoving
import io.github.yoimerdr.compose.virtualjoystick.ui.state.JoystickSnapshot
import io.github.yoimerdr.compose.virtualjoystick.ui.state.gotoWithReset
import io.github.yoimerdr.compose.virtualjoystick.ui.view.rememberJoystickEventHolder
import kotlinx.coroutines.launch

enum class DrawingTypes {
    Circle,
    Arc,
    CircleArc,
    Wedge,
    Drawable,
    StateDrawable,
}

enum class DrawingColors(
    val color: Color,
) {
    Red(Color.Red),
    Blue(Color.Blue),
    Cyan(Color.Cyan),
    White(Color.White),
    Blck(Color.Black),
    Yellow(Color.Yellow)
}

fun drawerBrush(
    primaryColor: DrawingColors,
    accentColor: DrawingColors,
    position: Offset,
    radius: Float,
): Brush {
    if (primaryColor == accentColor)
        return SolidColor(primaryColor.color)

    return Brush.radialGradient(
        colors = listOf(primaryColor.color, accentColor.color),
        center = position,
        radius = radius
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoystickSettingsScreen() {

    // --- State Holders (Mocking the data from your image) ---
    var primaryColor by remember { mutableStateOf(DrawingColors.Red) }
    var drawerType by remember { mutableStateOf(DrawingTypes.Circle) }
    var accentColor by remember { mutableStateOf(DrawingColors.Red) }
    var directionType by remember { mutableStateOf(io.github.yoimerdr.compose.virtualjoystick.core.control.DirectionType.Complete) }
    var drawMode by remember { mutableStateOf(io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.DrawMode.Clamped) }

    // Bottom controls
    var direction by remember { mutableStateOf(Direction.Right) }
    var magnitudeSlider by remember { mutableFloatStateOf(1.0f) }


    val scrollState = rememberScrollState()

    // Joystick states
    val state =
        rememberJoystickState(directionType = directionType)
    val eventHolder = rememberJoystickEventHolder(state)

    var snapshot by remember { mutableStateOf(JoystickSnapshot()) }

    val up = painterResource(Res.drawable.dpad_modern_u)
    val down = painterResource(Res.drawable.dpad_modern_d)
    val left = painterResource(Res.drawable.dpad_modern_l)
    val right = painterResource(Res.drawable.dpad_modern_r)

    val androidPainter = rememberVectorPainter(AndroidIcon)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(eventHolder) {
        eventHolder.events.collect {
            snapshot = it.snapshot
            when (it) {
                is JoystickMoveStart -> {
                    println("[VirtualJoystick]: The moving starts - ${it.snapshot}")
                }

                is JoystickMoving -> {
                    println("[VirtualJoystick]: The moving is running - ${it.snapshot}")
                }

                is JoystickMoveHeld -> {
                    println("[VirtualJoystick]: The moving is held - ${it.snapshot}")
                }

                is JoystickMoveEnd -> {
                    println("[VirtualJoystick]: The moving ends - ${it.snapshot}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Virtual Joystick") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        eventHolder.gotoWithReset(direction)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(PlayIcon, contentDescription = "Start")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 500.dp)
                    ) {
                        item {
                            SettingsDropdown(
                                label = "Primary color",
                                options = DrawingColors.entries,
                                selectedOption = primaryColor,
                                onOptionSelected = { primaryColor = it }
                            )
                        }
                        item {
                            SettingsDropdown(
                                label = "Drawer type",
                                options = DrawingTypes.entries,
                                selectedOption = drawerType,
                                onOptionSelected = { drawerType = it }
                            )
                        }
                        item {
                            SettingsDropdown(
                                label = "Accent color",
                                options = DrawingColors.entries,
                                selectedOption = accentColor,
                                onOptionSelected = { accentColor = it }
                            )
                        }
                        item {
                            SettingsDropdown(
                                label = "Direction type",
                                options = io.github.yoimerdr.compose.virtualjoystick.core.control.DirectionType.entries,
                                selectedOption = directionType,
                                onOptionSelected = { directionType = it }
                            )
                        }
                        item {
                            SettingsDropdown(
                                label = "Draw mode",
                                options = io.github.yoimerdr.compose.virtualjoystick.ui.scope.draw.shapes.DrawMode.entries,
                                selectedOption = drawMode,
                                onOptionSelected = { drawMode = it }
                            )
                        }
                    }
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    JoystickInfo(snapshot, state.center, state.radius)
                }
            }

            // Joystick Section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                VirtualJoystick(
                    backgroundType = BackgroundType.DpadModern,
                    modifier = Modifier.size(200.dp),
                    state = state,
                    holder = eventHolder
                ) {
                    when (drawerType) {
                        DrawingTypes.Circle -> {
                            drawCircle(
                                CircleDrawDefaults.properties(
                                    mode = drawMode,
                                    brush = { position, radius ->
                                        drawerBrush(
                                            primaryColor,
                                            accentColor,
                                            position,
                                            radius
                                        )
                                    }
                                ))
                        }

                        DrawingTypes.Arc -> {
                            drawArc(
                                ArcDrawDefaults.properties(
                                    mode = drawMode,
                                    brush = { position, radius ->
                                        drawerBrush(
                                            primaryColor,
                                            accentColor,
                                            position,
                                            radius
                                        )
                                    }
                                ))
                        }

                        DrawingTypes.CircleArc -> {
                            drawCircleArc(
                                CircleArcDrawDefaults.properties(
                                    circle = CircleDrawDefaults.properties(
                                        mode = drawMode,
                                        brush = { position, radius ->
                                            drawerBrush(primaryColor, accentColor, position, radius)
                                        }
                                    ),
                                    arc = ArcDrawDefaults.properties(
                                        mode = drawMode,
                                        brush = { position, radius ->
                                            drawerBrush(primaryColor, accentColor, position, radius)
                                        }
                                    )
                                )
                            )
                        }

                        DrawingTypes.Wedge -> {
                            drawWedge(
                                WedgeDrawDefaults.properties(
                                    brush = SolidColor(
                                        primaryColor.color.copy(alpha = 0.25f)
                                    ),
                                    mode = WedgeMode.Straight
                                )
                            )
                        }

                        DrawingTypes.Drawable -> {
                            drawImage(
                                ImageDrawDefaults.properties(
                                    painter = androidPainter,
                                    mode = drawMode,
                                    colorFilter = ColorFilter.tint(primaryColor.color)
                                ),
                            )
                        }

                        DrawingTypes.StateDrawable -> {
                            drawDirectionalImage {
                                when (it) {
                                    Direction.Up -> listOf(
                                        up
                                    )

                                    Direction.Down -> listOf(
                                        down
                                    )

                                    Direction.Left -> listOf(
                                        left
                                    )

                                    Direction.Right -> listOf(
                                        right
                                    )

                                    Direction.UpLeft -> listOf(
                                        up,
                                        left
                                    )

                                    Direction.UpRight -> listOf(
                                        up,
                                        right
                                    )

                                    Direction.DownLeft -> listOf(
                                        down,
                                        left
                                    )

                                    Direction.DownRight -> listOf(
                                        down,
                                        right
                                    )

                                    else -> null
                                }
                            }
                        }
                    }
                }
            }

            // Controls Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Controls",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Magnitude: ${magnitudeSlider.toFixedString(2)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = magnitudeSlider,
                        onValueChange = { magnitudeSlider = it },
                        valueRange = 0f..1f
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsDropdown(
                        label = "Direction",
                        options = Direction.entries,
                        selectedOption = direction,
                        onOptionSelected = { direction = it }
                    )
                }
            }
        }
    }
}

// --- Reusable Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Label above the dropdown (as seen in image)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedOption.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        JoystickSettingsScreen()
    }
}

fun Float.toFixedString(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(this * factor) / factor
    return rounded.toString()
}


@Composable
fun JoystickInfo(
    snapshot: JoystickSnapshot,
    center: Offset,
    radius: Float,
) {
    val position = snapshot.position
    val ndcPosition = if (position == Offset.Unspecified || radius == 0f)
        position
    else ((position - center) / radius)

    JoystickInfoText(
        "Direction",
        snapshot.direction
    )
    JoystickInfoText(
        "Magnitude",
        snapshot.strength.toFixedString(2)
    )
    JoystickInfoText(
        "Position",
        if (position == Offset.Unspecified) "Unspecified"
        else
            "(${position.x.toFixedString(2)};${position.y.toFixedString(2)})"
    )
    JoystickInfoText(
        "NDC Position",
        if (ndcPosition == Offset.Unspecified) "Unspecified"
        else "(${ndcPosition.x.toFixedString(2)};${ndcPosition.y.toFixedString(2)})"
    )
}

@Composable
fun JoystickInfoText(
    name: String,
    value: Any,
) {
    Text("$name: $value")
}