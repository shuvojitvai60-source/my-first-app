package com.example.ui.game.engines

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

// --- 1. REACTION TEST ---
enum class ReactionState { WAITING, READY, FINISHED }

@Composable
fun ReactionTestGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var state by remember { mutableStateOf(ReactionState.WAITING) }
    var startTime by remember { mutableLongStateOf(0L) }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var roundsLeft by remember { mutableIntStateOf(3) }
    var bestTimeMs by remember { mutableLongStateOf(9999L) }

    LaunchedEffect(roundsLeft, state) {
        if (state == ReactionState.WAITING && roundsLeft > 0) {
            val waitTime = 1500L + Random.nextLong(2500L)
            delay(waitTime)
            startTime = System.currentTimeMillis()
            state = ReactionState.READY
        }
    }

    fun handleTap() {
        onPlayTap()
        when (state) {
            ReactionState.WAITING -> {
                // False start
                state = ReactionState.WAITING
                onGameOver(0, false, "Too early! Wait until the box turns GREEN!")
            }
            ReactionState.READY -> {
                val now = System.currentTimeMillis()
                val diff = now - startTime
                reactionTimeMs = diff
                if (diff < bestTimeMs) bestTimeMs = diff
                state = ReactionState.FINISHED
                roundsLeft--
                val score = ((1000 - diff).coerceAtLeast(50L) / 2).toInt()
                onScoreUpdate(score)

                if (roundsLeft == 0) {
                    val finalScore = ((1000 - bestTimeMs).coerceAtLeast(100L) / 2).toInt()
                    onGameOver(finalScore, bestTimeMs < 350, "Best Reaction: ${bestTimeMs}ms! Superhuman reflexes!")
                }
            }
            ReactionState.FINISHED -> {
                if (roundsLeft > 0) {
                    state = ReactionState.WAITING
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Rounds Left: $roundsLeft | Best: ${if (bestTimeMs < 9999) "${bestTimeMs}ms" else "--"}",
            color = TextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    when (state) {
                        ReactionState.WAITING -> NeonPink
                        ReactionState.READY -> NeonGreen
                        ReactionState.FINISHED -> NeonCyan
                    }
                )
                .clickable { handleTap() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (state) {
                        ReactionState.WAITING -> "WAIT FOR GREEN..."
                        ReactionState.READY -> "TAP NOW! ⚡"
                        ReactionState.FINISHED -> "${reactionTimeMs}ms!"
                    },
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                if (state == ReactionState.FINISHED) {
                    Text(
                        text = "Tap to continue next round",
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// --- 2. SPEED TAP CHALLENGE ---
@Composable
fun SpeedTapGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var taps by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var isStarted by remember { mutableStateOf(false) }

    LaunchedEffect(isStarted) {
        if (isStarted) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            onGameOver(taps, taps >= 50, "You completed $taps taps in 10s! (${taps / 10.0} taps/sec)")
        }
    }

    fun tap() {
        if (!isStarted) {
            isStarted = true
        }
        if (timeLeft > 0) {
            onPlayTap()
            taps++
            onScoreUpdate(taps)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚡ SPEED TAP ARENA", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Time Left: ${timeLeft}s", color = if (timeLeft <= 3) NeonPink else GamingGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(NeonCyan, NeonPurple))
                )
                .clickable { tap() }
                .testTag("speed_tap_circle"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$taps",
                    color = Color.Black,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (isStarted) "TAP FAST!" else "START TAP",
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tap the circle as many times as possible before the clock expires!",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

// --- 3. TAP TARGET / AVOID BOMB ---
@Composable
fun TapTargetGame(
    isAvoidBomb: Boolean = false,
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var targetPos by remember { mutableStateOf(Pair(0.5f, 0.5f)) }
    var bombPos by remember { mutableStateOf(Pair(0.2f, 0.2f)) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(20) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
            targetPos = Pair(0.1f + Random.nextFloat() * 0.75f, 0.1f + Random.nextFloat() * 0.75f)
            bombPos = Pair(0.1f + Random.nextFloat() * 0.75f, 0.1f + Random.nextFloat() * 0.75f)
        }
        if (timeLeft == 0) {
            onGameOver(score, score >= 60, "Time's up! Target reflex score: $score")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Time: ${timeLeft}s | Score: $score",
            color = GamingGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
        )

        // Target
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = (targetPos.first * 280).dp,
                    top = (targetPos.second * 400).dp
                )
                .size(60.dp)
                .clip(CircleShape)
                .background(NeonGreen)
                .clickable {
                    onPlayTap()
                    score += 15
                    onScoreUpdate(score)
                    targetPos = Pair(0.1f + Random.nextFloat() * 0.75f, 0.1f + Random.nextFloat() * 0.75f)
                },
            contentAlignment = Alignment.Center
        ) {
            Text("🎯", fontSize = 28.sp)
        }

        // Bomb
        if (isAvoidBomb) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = (bombPos.first * 280).dp,
                        top = (bombPos.second * 400).dp
                    )
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(NeonPink)
                    .clickable {
                        isRunning = false
                        onGameOver(score, false, "BOOM! You tapped the bomb!")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("💣", fontSize = 28.sp)
            }
        }
    }
}

// --- 4. LUCKY NUMBER / ROULETTE ---
@Composable
fun LuckyNumberGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var currentNum by remember { mutableIntStateOf(7) }
    var isSpinning by remember { mutableStateOf(false) }
    var spinsLeft by remember { mutableIntStateOf(5) }
    var score by remember { mutableIntStateOf(0) }

    fun spin() {
        if (spinsLeft <= 0 || isSpinning) return
        onPlayTap()
        isSpinning = true
        spinsLeft--
    }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            for (i in 0..15) {
                delay(60)
                currentNum = Random.nextInt(1, 100)
            }
            isSpinning = false
            score += currentNum
            onScoreUpdate(score)
            if (spinsLeft == 0) {
                onGameOver(score, score >= 200, "Lucky Roulette Final: $score points!")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎰 LUCKY GAMING ROULETTE", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Spins Left: $spinsLeft", color = TextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(GamingGold, Color(0xFF6B4A00)))
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$currentNum", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { spin() },
            enabled = !isSpinning && spinsLeft > 0,
            colors = ButtonDefaults.buttonColors(containerColor = GamingGold, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("SPIN WHEEL 🎲", fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}
