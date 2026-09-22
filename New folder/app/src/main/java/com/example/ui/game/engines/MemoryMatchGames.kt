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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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

// --- 1. MEMORY MATCH CARDS ---
@Composable
fun MemoryMatchGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val icons = listOf("🚀", "🎮", "🏎️", "⚽", "💎", "⭐")
    val deck = remember { (icons + icons).shuffled() }
    val flipped = remember { mutableStateListOf(*Array(12) { false }) }
    val matched = remember { mutableStateListOf(*Array(12) { false }) }
    var firstSelection by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(0) }

    fun selectCard(index: Int) {
        if (flipped[index] || matched[index]) return
        onPlayTap()
        flipped[index] = true

        val first = firstSelection
        if (first == null) {
            firstSelection = index
        } else {
            moves++
            if (deck[first] == deck[index]) {
                matched[first] = true
                matched[index] = true
                firstSelection = null
                score += 30
                onScoreUpdate(score)

                if (matched.all { it }) {
                    onGameOver(score, true, "All pairs matched in $moves moves!")
                }
            } else {
                firstSelection = null
                // Flip back after short pause handled in Coroutine
            }
        }
    }

    LaunchedEffect(firstSelection) {
        if (firstSelection == null && flipped.count { it } > matched.count { it }) {
            delay(600)
            for (i in flipped.indices) {
                if (!matched[i]) flipped[i] = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Moves: $moves | Pairs Matched: ${matched.count { it } / 2}/6",
            color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                for (r in 0..3) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (c in 0..2) {
                            val idx = r * 3 + c
                            val isFlipped = flipped[idx] || matched[idx]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isFlipped) Color(0xFF1E293B) else NeonCyan.copy(alpha = 0.2f))
                                    .border(1.dp, if (matched[idx]) NeonGreen else GamingCardBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectCard(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFlipped) {
                                    Text(deck[idx], fontSize = 28.sp)
                                } else {
                                    Text("❓", fontSize = 18.sp, color = NeonCyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. SIMON SAYS ---
@Composable
fun SimonSaysGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val sequence = remember { mutableStateListOf(Random.nextInt(4)) }
    var playerStep by remember { mutableIntStateOf(0) }
    var activeFlash by remember { mutableStateOf<Int?>(null) }
    var isBotPlaying by remember { mutableStateOf(true) }
    var round by remember { mutableIntStateOf(1) }

    LaunchedEffect(round) {
        isBotPlaying = true
        delay(600)
        for (colorIdx in sequence) {
            activeFlash = colorIdx
            delay(450)
            activeFlash = null
            delay(200)
        }
        isBotPlaying = false
        playerStep = 0
    }

    fun playerTap(colorIdx: Int) {
        if (isBotPlaying) return
        onPlayTap()
        if (sequence[playerStep] == colorIdx) {
            playerStep++
            if (playerStep == sequence.size) {
                val score = round * 25
                onScoreUpdate(score)
                if (round >= 8) {
                    onGameOver(score, true, "Master of Simon Says! 8 sequences completed!")
                } else {
                    round++
                    sequence.add(Random.nextInt(4))
                }
            }
        } else {
            val finalScore = (round - 1) * 25
            onGameOver(finalScore, false, "Wrong sequence tapped! Reached round $round")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isBotPlaying) "WATCH THE SEQUENCE..." else "YOUR TURN!",
            color = if (isBotPlaying) NeonPink else NeonGreen,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        Text("Round: $round", color = GamingGold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

        Box(modifier = Modifier.size(240.dp)) {
            // 4 Quadrants: 0=Green, 1=Red, 2=Yellow, 3=Blue
            val colors = listOf(NeonGreen, NeonPink, GamingGold, NeonCyan)
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(topStart = 40.dp))
                            .background(if (activeFlash == 0) Color.White else colors[0].copy(alpha = 0.6f))
                            .clickable { playerTap(0) }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(topEnd = 40.dp))
                            .background(if (activeFlash == 1) Color.White else colors[1].copy(alpha = 0.6f))
                            .clickable { playerTap(1) }
                    )
                }
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(bottomStart = 40.dp))
                            .background(if (activeFlash == 2) Color.White else colors[2].copy(alpha = 0.6f))
                            .clickable { playerTap(2) }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(bottomEnd = 40.dp))
                            .background(if (activeFlash == 3) Color.White else colors[3].copy(alpha = 0.6f))
                            .clickable { playerTap(3) }
                    )
                }
            }
        }
    }
}

// --- 3. TOWER STACK ---
@Composable
fun TowerStackGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var blockX by remember { mutableFloatStateOf(0.5f) }
    var movingRight by remember { mutableStateOf(true) }
    var towerHeight by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(20)
            if (movingRight) {
                blockX += 0.025f
                if (blockX >= 0.85f) movingRight = false
            } else {
                blockX -= 0.025f
                if (blockX <= 0.15f) movingRight = true
            }
        }
    }

    fun dropBlock() {
        if (!isRunning) return
        onPlayTap()
        // Center alignment threshold
        val diff = kotlin.math.abs(blockX - 0.5f)
        if (diff > 0.28f) {
            isRunning = false
            onGameOver(towerHeight * 15, towerHeight >= 10, "Block missed the stack! Tower height: $towerHeight")
        } else {
            towerHeight++
            val score = towerHeight * 15
            onScoreUpdate(score)
            if (towerHeight >= 15) {
                isRunning = false
                onGameOver(score, true, "Skyscraper Master! 15 levels built!")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏢 TOWER STACK", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("Height: $towerHeight Floors", color = GamingGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier.fillMaxWidth(0.85f).height(240.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Moving Block at top
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = (blockX * 220).dp, top = 20.dp)
                        .size(60.dp, 24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan)
                )

                // Stack base at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(minOf(towerHeight, 6)) {
                        Box(
                            modifier = Modifier
                                .size(70.dp, 20.dp)
                                .padding(vertical = 1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonPurple)
                        )
                    }
                    // Foundation
                    Box(
                        modifier = Modifier
                            .size(100.dp, 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF334155))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { dropBlock() },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("DROP BLOCK 🧱", fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

// --- 4. FIND THE ODD ONE ---
@Composable
fun FindOddOneGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val sets = listOf(
        Pair("🐱", "🐶"),
        Pair("🍎", "🍅"),
        Pair("🚗", "🏎️"),
        Pair("⚽", "🏀"),
        Pair("⭐", "🌟")
    )
    var currentRound by remember { mutableIntStateOf(0) }
    var oddIndex by remember { mutableIntStateOf(Random.nextInt(16)) }
    var score by remember { mutableIntStateOf(0) }

    fun checkTap(idx: Int) {
        onPlayTap()
        if (idx == oddIndex) {
            score += 25
            onScoreUpdate(score)
            if (currentRound >= 4) {
                onGameOver(score, true, "Eagle Eye! Found all odd ones!")
            } else {
                currentRound++
                oddIndex = Random.nextInt(16)
            }
        } else {
            onGameOver(score, false, "Wrong one picked!")
        }
    }

    val (common, odd) = sets[currentRound % sets.size]

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Round ${currentRound + 1}/5: Spot the Odd One!", color = NeonCyan, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                for (r in 0..3) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (c in 0..3) {
                            val idx = r * 4 + c
                            val emoji = if (idx == oddIndex) odd else common
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F1420))
                                    .clickable { checkTap(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
