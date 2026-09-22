package com.example.ui.game.engines

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GamingCardBorder
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// --- 1. SNAKE GAME ---
@Composable
fun SnakeGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val gridSize = 16
    var snake = remember { mutableStateListOf(Pair(8, 8), Pair(8, 9), Pair(8, 10)) }
    var direction by remember { mutableStateOf(Pair(0, -1)) }
    var food by remember { mutableStateOf(Pair(4, 4)) }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(160)
            val head = snake.first()
            val newHead = Pair(head.first + direction.first, head.second + direction.second)

            // Wall collision
            if (newHead.first !in 0 until gridSize || newHead.second !in 0 until gridSize) {
                isRunning = false
                onGameOver(score, false, "Snake hit the electric boundary!")
                break
            }
            // Self collision
            if (snake.contains(newHead)) {
                isRunning = false
                onGameOver(score, false, "Snake collided with its own tail!")
                break
            }

            val newSnake = mutableListOf(newHead)
            newSnake.addAll(snake)

            if (newHead == food) {
                onPlayTap()
                score += 10
                onScoreUpdate(score)
                // Spawn food
                var newFood = Pair(Random.nextInt(gridSize), Random.nextInt(gridSize))
                while (newSnake.contains(newFood)) {
                    newFood = Pair(Random.nextInt(gridSize), Random.nextInt(gridSize))
                }
                food = newFood
            } else {
                newSnake.removeAt(newSnake.size - 1)
            }

            snake.clear()
            snake.addAll(newSnake)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0C101A))
                .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / gridSize

                // Draw food
                drawCircle(
                    color = NeonPink,
                    radius = cellSize * 0.4f,
                    center = Offset(food.first * cellSize + cellSize / 2, food.second * cellSize + cellSize / 2)
                )

                // Draw snake
                snake.forEachIndexed { index, part ->
                    val color = if (index == 0) NeonCyan else NeonGreen
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(part.first * cellSize + 1, part.second * cellSize + 1),
                        size = Size(cellSize - 2, cellSize - 2),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // D-Pad Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { if (direction != Pair(0, 1)) direction = Pair(0, -1) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                modifier = Modifier.size(52.dp, 40.dp)
            ) {
                Text("▲", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                Button(
                    onClick = { if (direction != Pair(1, 0)) direction = Pair(-1, 0) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(52.dp, 40.dp)
                ) {
                    Text("◀", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { if (direction != Pair(0, -1)) direction = Pair(0, 1) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(52.dp, 40.dp)
                ) {
                    Text("▼", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { if (direction != Pair(-1, 0)) direction = Pair(1, 0) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(52.dp, 40.dp)
                ) {
                    Text("▶", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 2. FLAPPY OBSTACLE GAME ---
@Composable
fun FlappyBirdGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var birdY by remember { mutableFloatStateOf(0.5f) }
    var velocity by remember { mutableFloatStateOf(0f) }
    var obstacleX by remember { mutableFloatStateOf(1.2f) }
    var gapY by remember { mutableFloatStateOf(0.4f) }
    var score by remember { mutableIntStateOf(0) }
    var isAlive by remember { mutableStateOf(true) }

    LaunchedEffect(isAlive) {
        while (isAlive) {
            delay(25)
            velocity += 0.0015f
            birdY += velocity
            obstacleX -= 0.015f

            if (obstacleX < -0.2f) {
                obstacleX = 1.0f
                gapY = 0.25f + Random.nextFloat() * 0.4f
                score += 10
                onScoreUpdate(score)
            }

            // Collision check
            if (birdY < 0f || birdY > 1f) {
                isAlive = false
                onGameOver(score, false, "Crashed into boundaries!")
                break
            }

            // Pipe collision: bird is at x = 0.25, width ~ 0.06
            if (obstacleX in 0.18f..0.32f) {
                val gapSize = 0.28f
                if (birdY < gapY || birdY > gapY + gapSize) {
                    isAlive = false
                    onGameOver(score, false, "Crashed into neon pillar!")
                    break
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    if (isAlive) {
                        onPlayTap()
                        velocity = -0.025f
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gapSize = 0.28f * h

            // Draw Pipes
            val pipeW = w * 0.15f
            val pipeX = obstacleX * w
            val gapTop = gapY * h

            // Top Pipe
            drawRect(
                brush = Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                topLeft = Offset(pipeX, 0f),
                size = Size(pipeW, gapTop)
            )

            // Bottom Pipe
            drawRect(
                brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                topLeft = Offset(pipeX, gapTop + gapSize),
                size = Size(pipeW, h - (gapTop + gapSize))
            )

            // Bird
            drawCircle(
                color = GamingGold,
                radius = w * 0.04f,
                center = Offset(w * 0.25f, birdY * h)
            )
        }

        Text(
            text = "TAP TO FLAP",
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp)
        )
    }
}

// --- 3. BRICK BREAKER ---
@Composable
fun BrickBreakerGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var paddleX by remember { mutableFloatStateOf(0.5f) }
    var ballX by remember { mutableFloatStateOf(0.5f) }
    var ballY by remember { mutableFloatStateOf(0.7f) }
    var ballVx by remember { mutableFloatStateOf(0.012f) }
    var ballVy by remember { mutableFloatStateOf(-0.016f) }
    val bricks = remember { mutableStateListOf(*Array(24) { true }) }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(25)
            ballX += ballVx
            ballY += ballVy

            // Wall bounce
            if (ballX <= 0.05f) { ballX = 0.05f; ballVx = -ballVx }
            if (ballX >= 0.95f) { ballX = 0.95f; ballVx = -ballVx }
            if (ballY <= 0.05f) { ballY = 0.05f; ballVy = -ballVy }

            // Paddle bounce
            if (ballY >= 0.85f && ballY <= 0.88f && ballX in (paddleX - 0.16f)..(paddleX + 0.16f)) {
                ballVy = -ballVy
                ballVx += (ballX - paddleX) * 0.04f
                onPlayTap()
            }

            // Bottom out
            if (ballY > 0.95f) {
                isRunning = false
                onGameOver(score, false, "Ball dropped into the abyss!")
                break
            }

            // Brick collision
            val cols = 6
            val rows = 4
            val brickW = 1.0f / cols
            val brickH = 0.05f
            val startY = 0.08f

            for (i in bricks.indices) {
                if (bricks[i]) {
                    val r = i / cols
                    val c = i % cols
                    val bx = c * brickW
                    val by = startY + r * brickH
                    if (ballX in bx..(bx + brickW) && ballY in by..(by + brickH)) {
                        bricks[i] = false
                        ballVy = -ballVy
                        score += 20
                        onScoreUpdate(score)
                        onPlayTap()
                        if (bricks.none { it }) {
                            isRunning = false
                            onGameOver(score, true, "All Bricks Shattered!")
                        }
                        break
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    paddleX = (paddleX + dragAmount.x / 800f).coerceIn(0.15f, 0.85f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Draw Bricks
            val cols = 6
            val brickW = w / cols
            val brickH = h * 0.045f
            val startY = h * 0.08f

            bricks.forEachIndexed { i, active ->
                if (active) {
                    val r = i / cols
                    val c = i % cols
                    drawRoundRect(
                        color = when (r) { 0 -> NeonPink 1 -> NeonPurple 2 -> NeonCyan else -> NeonGreen },
                        topLeft = Offset(c * brickW + 3f, startY + r * (brickH + 4f)),
                        size = Size(brickW - 6f, brickH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                }
            }

            // Draw Ball
            drawCircle(
                color = GamingGold,
                radius = w * 0.025f,
                center = Offset(ballX * w, ballY * h)
            )

            // Draw Paddle
            val padW = w * 0.28f
            val padH = h * 0.022f
            drawRoundRect(
                color = NeonCyan,
                topLeft = Offset(paddleX * w - padW / 2, h * 0.86f),
                size = Size(padW, padH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )
        }

        Text(
            text = "SLIDE TO MOVE PADDLE",
            color = TextSecondary.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        )
    }
}

// --- 4. SPACE SHOOTER / ALIEN DEFENDER ---
@Composable
fun SpaceShooterGame(
    isAlienTheme: Boolean = false,
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var playerX by remember { mutableFloatStateOf(0.5f) }
    val bullets = remember { mutableStateListOf<Pair<Float, Float>>() }
    val enemies = remember { mutableStateListOf<Pair<Float, Float>>() }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        var tick = 0
        while (isRunning) {
            delay(30)
            tick++

            // Move bullets up
            val nextBullets = bullets.map { Pair(it.first, it.second - 0.035f) }.filter { it.second > 0f }
            bullets.clear()
            bullets.addAll(nextBullets)

            // Spawn enemies
            if (tick % 25 == 0 && enemies.size < 6) {
                enemies.add(Pair(0.1f + Random.nextFloat() * 0.8f, 0f))
            }

            // Move enemies down
            val nextEnemies = mutableListOf<Pair<Float, Float>>()
            for (enemy in enemies) {
                val ny = enemy.second + 0.012f
                if (ny > 0.85f) {
                    isRunning = false
                    onGameOver(score, false, if (isAlienTheme) "Aliens invaded Earth!" else "Spaceship overwhelmed!")
                    return@LaunchedEffect
                }
                nextEnemies.add(Pair(enemy.first, ny))
            }
            enemies.clear()
            enemies.addAll(nextEnemies)

            // Bullet - Enemy collision
            val toRemoveEnemies = mutableListOf<Pair<Float, Float>>()
            val toRemoveBullets = mutableListOf<Pair<Float, Float>>()

            for (b in bullets) {
                for (e in enemies) {
                    if (kotlin.math.abs(b.first - e.first) < 0.08f && kotlin.math.abs(b.second - e.second) < 0.06f) {
                        toRemoveEnemies.add(e)
                        toRemoveBullets.add(b)
                        score += 25
                        onScoreUpdate(score)
                        onPlayTap()
                    }
                }
            }
            enemies.removeAll(toRemoveEnemies)
            bullets.removeAll(toRemoveBullets)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (isRunning && bullets.size < 5) {
                            onPlayTap()
                            bullets.add(Pair(playerX, 0.80f))
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bullets
            bullets.forEach { b ->
                drawCircle(color = NeonCyan, radius = 6f, center = Offset(b.first * w, b.second * h))
            }

            // Enemies
            enemies.forEach { e ->
                drawCircle(color = NeonPink, radius = w * 0.045f, center = Offset(e.first * w, e.second * h))
            }

            // Player Ship
            val px = playerX * w
            val py = h * 0.82f
            drawCircle(color = NeonGreen, radius = w * 0.05f, center = Offset(px, py))
        }

        // Steer Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { playerX = (playerX - 0.1f).coerceIn(0.1f, 0.9f) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.size(70.dp, 48.dp)
            ) {
                Text("◀", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = {
                    if (isRunning && bullets.size < 6) {
                        onPlayTap()
                        bullets.add(Pair(playerX, 0.80f))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                modifier = Modifier.height(48.dp)
            ) {
                Text("FIRE 🚀", color = Color.White, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = { playerX = (playerX + 0.1f).coerceIn(0.1f, 0.9f) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.size(70.dp, 48.dp)
            ) {
                Text("▶", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

// --- 5. BALLOON POP / BUBBLE POP / FRUIT SLICE ---
@Composable
fun PopAndSliceGame(
    typeEmoji: String = "🎈",
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    data class PopItem(val id: Int, val x: Float, var y: Float, val size: Float, val speed: Float)
    val items = remember { mutableStateListOf<PopItem>() }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var timer by remember { mutableIntStateOf(30) }

    LaunchedEffect(isRunning) {
        var idCounter = 0
        while (isRunning && timer > 0) {
            delay(50)
            // Spawn items
            if (Random.nextFloat() < 0.2f && items.size < 7) {
                items.add(PopItem(idCounter++, Random.nextFloat() * 0.8f + 0.1f, 1.1f, 44f, 0.015f + Random.nextFloat() * 0.02f))
            }
            // Move up
            val remaining = mutableListOf<PopItem>()
            items.forEach { it.y -= it.speed; if (it.y > -0.1f) remaining.add(it) }
            items.clear()
            items.addAll(remaining)
        }
        if (timer <= 0) {
            onGameOver(score, score >= 100, "Time's up! Pop Master!")
        }
    }

    LaunchedEffect(Unit) {
        while (timer > 0 && isRunning) {
            delay(1000)
            timer--
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Time Left: ${timer}s",
            color = GamingGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
        )

        items.forEach { item ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = (item.x * 320).dp,
                        top = (item.y * 500).coerceAtLeast(0f).dp
                    )
                    .clip(CircleShape)
                    .background(Color(0xFF1E2538))
                    .clickable {
                        onPlayTap()
                        score += 10
                        onScoreUpdate(score)
                        items.remove(item)
                    }
                    .padding(8.dp)
            ) {
                Text(text = typeEmoji, fontSize = 32.sp)
            }
        }
    }
}

// --- 6. KNIFE TARGET / KNIFE THROW ---
@Composable
fun KnifeThrowGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var angle by remember { mutableFloatStateOf(0f) }
    val stuckAngles = remember { mutableStateListOf<Float>() }
    var knivesLeft by remember { mutableIntStateOf(7) }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(20)
            angle = (angle + 3f) % 360f
        }
    }

    fun throwKnife() {
        if (!isRunning || knivesLeft <= 0) return
        onPlayTap()
        // The knife hits target at 90 degrees relative to world (bottom to center)
        // Stuck angle relative to spinning wheel:
        val hitAngle = (90f - angle + 360f) % 360f

        val collision = stuckAngles.any { kotlin.math.abs(it - hitAngle) < 16f || kotlin.math.abs(it - hitAngle) > 344f }
        if (collision) {
            isRunning = false
            onGameOver(score, false, "Knife collided with another blade!")
        } else {
            stuckAngles.add(hitAngle)
            knivesLeft--
            score += 20
            onScoreUpdate(score)
            if (knivesLeft == 0) {
                isRunning = false
                onGameOver(score, true, "Target Mastered! All knives placed!")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Blades Remaining: $knivesLeft", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Spinning Target Box
        Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val targetRadius = size.width * 0.35f

                // Target log
                drawCircle(color = Color(0xFF5D4037), radius = targetRadius, center = center)
                drawCircle(color = Color(0xFF795548), radius = targetRadius * 0.85f, center = center)
                drawCircle(color = NeonCyan, radius = targetRadius * 0.15f, center = center)

                // Stuck knives
                stuckAngles.forEach { a ->
                    val rad = Math.toRadians((a + angle).toDouble())
                    val kx = center.x + (targetRadius + 20f) * cos(rad).toFloat()
                    val ky = center.y + (targetRadius + 20f) * sin(rad).toFloat()
                    drawCircle(color = GamingGold, radius = 8f, center = Offset(kx, ky))
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x + targetRadius * cos(rad).toFloat(), center.y + targetRadius * sin(rad).toFloat()),
                        end = Offset(kx, ky),
                        strokeWidth = 5f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { throwKnife() },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("THROW KNIFE 🗡️", fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}
