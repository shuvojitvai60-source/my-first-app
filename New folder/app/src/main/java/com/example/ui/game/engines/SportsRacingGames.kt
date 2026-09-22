package com.example.ui.game.engines

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import kotlin.random.Random

// --- 1. MULTI-LANE RACING & DODGE ENGINE ---
@Composable
fun LaneRacingGame(
    vehicleEmoji: String = "🏎️",
    obstacleEmoji: String = "🛢️",
    gameName: String = "Racing",
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var playerLane by remember { mutableIntStateOf(1) } // 0, 1, 2
    data class Obstacle(val lane: Int, var y: Float)
    val obstacles = remember { mutableStateListOf<Obstacle>() }
    var score by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        var tick = 0
        while (isRunning) {
            delay(35)
            tick++

            // Move obstacles down
            val remaining = mutableListOf<Obstacle>()
            for (obs in obstacles) {
                obs.y += 0.02f
                if (obs.y < 0.95f) {
                    // Collision check
                    if (obs.lane == playerLane && obs.y in 0.70f..0.85f) {
                        isRunning = false
                        onGameOver(score, false, "Collision on track! Final distance: ${score}m")
                        return@LaunchedEffect
                    }
                    remaining.add(obs)
                } else {
                    score += 10
                    onScoreUpdate(score)
                }
            }
            obstacles.clear()
            obstacles.addAll(remaining)

            // Spawn new obstacle
            if (tick % 24 == 0) {
                obstacles.add(Obstacle(Random.nextInt(3), 0f))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Track
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
            border = BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Lane dividers
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val laneW = size.width / 3
                    drawLine(Color(0xFF283048), Offset(laneW, 0f), Offset(laneW, size.height), strokeWidth = 3f)
                    drawLine(Color(0xFF283048), Offset(laneW * 2, 0f), Offset(laneW * 2, size.height), strokeWidth = 3f)
                }

                // Obstacles
                obstacles.forEach { obs ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.33f)
                            .align(Alignment.TopStart)
                            .padding(
                                start = (obs.lane * 110).dp,
                                top = (obs.y * 380).coerceAtLeast(0f).dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = obstacleEmoji, fontSize = 28.sp)
                    }
                }

                // Player Vehicle
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.33f)
                        .align(Alignment.BottomStart)
                        .padding(start = (playerLane * 110).dp, bottom = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = vehicleEmoji, fontSize = 36.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lane Steer Buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (playerLane > 0) {
                        onPlayTap()
                        playerLane--
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.size(80.dp, 50.dp)
            ) {
                Text("◀ LEFT", color = NeonCyan, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = {
                    if (playerLane < 2) {
                        onPlayTap()
                        playerLane++
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.size(80.dp, 50.dp)
            ) {
                Text("RIGHT ▶", color = NeonCyan, fontWeight = FontWeight.Black)
            }
        }
    }
}

// --- 2. BASKETBALL SHOT & DUNK ---
@Composable
fun BasketballGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var power by remember { mutableFloatStateOf(0f) }
    var increasing by remember { mutableStateOf(true) }
    var shotsLeft by remember { mutableIntStateOf(5) }
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("Time your shot when meter is in the GREEN zone!") }

    LaunchedEffect(shotsLeft) {
        while (shotsLeft > 0) {
            delay(20)
            if (increasing) {
                power += 0.03f
                if (power >= 1f) increasing = false
            } else {
                power -= 0.03f
                if (power <= 0f) increasing = true
            }
        }
    }

    fun shoot() {
        if (shotsLeft <= 0) return
        onPlayTap()
        shotsLeft--

        // Green sweet spot between 0.65 and 0.85
        if (power in 0.65f..0.85f) {
            score += 30
            onScoreUpdate(score)
            feedback = "🔥 SWISH! 3-POINTER SCORED! (+30)"
        } else if (power in 0.50f..0.95f) {
            score += 15
            onScoreUpdate(score)
            feedback = "🏀 Off the backboard and IN! (+15)"
        } else {
            feedback = "💨 Airball! Missed the rim!"
        }

        if (shotsLeft == 0) {
            onGameOver(score, score >= 60, "Final Score: $score points!")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏀 BASKETBALL SHOOTOUT", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Balls remaining: $shotsLeft", color = TextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Hoop Display
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1420))
                .border(2.dp, GamingGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🏀 🗑️", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = feedback, color = GamingGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Power Bar
        Card(
            modifier = Modifier.fillMaxWidth(0.8f).height(24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF10141E)),
            border = BorderStroke(1.dp, GamingCardBorder)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Target zone indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.20f)
                        .fillMaxSize()
                        .align(Alignment.CenterStart)
                        .padding(start = 140.dp)
                        .background(NeonGreen.copy(alpha = 0.3f))
                )
                // Current power
                Box(
                    modifier = Modifier
                        .fillMaxWidth(power)
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(NeonCyan, NeonGreen, NeonPink)))
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { shoot() },
            colors = ButtonDefaults.buttonColors(containerColor = GamingGold, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("SHOOT! 🏀", fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

// --- 3. CRICKET BATTING / TARGET / BOWLING ---
@Composable
fun CricketGame(
    isBowling: Boolean = false,
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var ballsLeft by remember { mutableIntStateOf(6) }
    var score by remember { mutableIntStateOf(0) }
    var ballY by remember { mutableFloatStateOf(0f) }
    var isPitching by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf("Press 'BOWL' to face the delivery!") }

    fun startBall() {
        if (ballsLeft <= 0 || isPitching) return
        onPlayTap()
        isPitching = true
        ballY = 0f
    }

    LaunchedEffect(isPitching) {
        if (isPitching) {
            while (ballY < 1f) {
                delay(20)
                ballY += 0.035f
            }
            // Ball completed without swing
            if (isPitching) {
                ballsLeft--
                isPitching = false
                comment = "Dot ball! Ball passed the bat!"
                if (ballsLeft == 0) {
                    onGameOver(score, score >= 16, "Innings Over! Total: $score runs")
                }
            }
        }
    }

    fun swingBat() {
        if (!isPitching) return
        onPlayTap()
        val timing = ballY
        isPitching = false
        ballsLeft--

        val runs = when {
            timing in 0.65f..0.85f -> 6
            timing in 0.50f..0.65f || timing in 0.85f..0.95f -> 4
            timing in 0.35f..0.50f -> 2
            timing in 0.20f..0.35f -> 1
            else -> 0
        }

        score += runs
        onScoreUpdate(score)

        comment = when (runs) {
            6 -> "💥 MASSIVE SIX OVER LONG ON! (+6)"
            4 -> "🏏 CRACKING SHOT FOR FOUR! (+4)"
            2 -> "🏃 Quick double taken! (+2)"
            1 -> "⚡ Single pushed into gap! (+1)"
            else -> "❌ Missed the swing! Dot ball."
        }

        if (ballsLeft == 0) {
            onGameOver(score, score >= 16, "Innings Over! Final: $score runs")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏏 CRICKET ARENA", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Balls Left: $ballsLeft | Total Runs: $score", color = GamingGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Cricket Pitch Canvas
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132A13)),
            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth(0.85f).height(240.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Pitch crease
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Bowling crease
                    drawLine(Color.White, Offset(w * 0.2f, h * 0.15f), Offset(w * 0.8f, h * 0.15f), strokeWidth = 2f)
                    // Batting crease
                    drawLine(Color.White, Offset(w * 0.2f, h * 0.85f), Offset(w * 0.8f, h * 0.85f), strokeWidth = 2f)

                    // Stumps at bottom
                    drawRect(Color(0xFFD4A373), Offset(w * 0.45f, h * 0.86f), Size(w * 0.1f, 10f))

                    // Incoming Ball
                    if (isPitching) {
                        drawCircle(color = Color(0xFFDC2626), radius = 12f, center = Offset(w * 0.5f, ballY * h))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = comment, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { startBall() },
                enabled = !isPitching && ballsLeft > 0,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("BOWL BALL ⚾", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { swingBat() },
                enabled = isPitching,
                colors = ButtonDefaults.buttonColors(containerColor = GamingGold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("SWING BAT 🏏", fontWeight = FontWeight.Black)
            }
        }
    }
}

// --- 4. FOOTBALL PENALTY SHOOTOUT ---
@Composable
fun FootballPenaltyGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var penaltiesLeft by remember { mutableIntStateOf(5) }
    var score by remember { mutableIntStateOf(0) }
    var goaliePos by remember { mutableIntStateOf(1) } // 0=left, 1=center, 2=right
    var statusText by remember { mutableStateOf("Pick where to kick the penalty!") }

    fun kick(targetPos: Int) {
        if (penaltiesLeft <= 0) return
        onPlayTap()
        penaltiesLeft--
        goaliePos = Random.nextInt(3)

        if (targetPos != goaliePos) {
            score += 20
            onScoreUpdate(score)
            statusText = "GOOOOAL! Net found! (+20)"
        } else {
            statusText = "🧤 SAVED! Goalkeeper blocked the shot!"
        }

        if (penaltiesLeft == 0) {
            onGameOver(score, score >= 60, "Shootout Finished! Final Score: $score")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚽ PENALTY SHOOTOUT", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Kicks Left: $penaltiesLeft", color = GamingGold, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Goalpost
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A12)),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier.fillMaxWidth(0.9f).height(160.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1B3A23))
                            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (goaliePos == i) {
                            Text("🧤🧍", fontSize = 32.sp)
                        } else {
                            Text("🕸️", fontSize = 24.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = statusText, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Text("AIM & SHOOT:", color = TextSecondary, fontSize = 12.sp)

        Row(
            modifier = Modifier.fillMaxWidth(0.9f).padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { kick(0) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("LEFT", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { kick(1) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("CENTER", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { kick(2) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("RIGHT", fontWeight = FontWeight.Bold)
            }
        }
    }
}
