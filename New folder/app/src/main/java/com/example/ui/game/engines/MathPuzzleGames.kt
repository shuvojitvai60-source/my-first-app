package com.example.ui.game.engines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

// --- 1. QUICK MATH & ARITHMETIC DRILL ---
@Composable
fun QuickMathGame(
    operationType: String = "ALL", // "ADD", "MULT", "ALL"
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var num1 by remember { mutableIntStateOf(Random.nextInt(5, 25)) }
    var num2 by remember { mutableIntStateOf(Random.nextInt(2, 12)) }
    var op by remember { mutableStateOf(if (operationType == "MULT") "×" else if (operationType == "ADD") "+" else listOf("+", "-", "×").random()) }
    var options by remember { mutableStateOf(emptyList<Int>()) }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var timer by remember { mutableIntStateOf(10) }

    fun correctAnswer(): Int = when (op) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "×" -> num1 * num2
        else -> num1 + num2
    }

    fun generateOptions() {
        val ans = correctAnswer()
        val opts = mutableListOf(ans)
        while (opts.size < 4) {
            val delta = Random.nextInt(-6, 7)
            val fake = ans + delta
            if (fake != ans && !opts.contains(fake)) opts.add(fake)
        }
        options = opts.shuffled()
    }

    LaunchedEffect(round) {
        num1 = Random.nextInt(5, 30)
        num2 = Random.nextInt(2, 15)
        op = if (operationType == "MULT") "×" else if (operationType == "ADD") "+" else listOf("+", "-", "×").random()
        generateOptions()
        timer = 10
    }

    LaunchedEffect(round) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
        if (timer == 0) {
            onGameOver(score, score >= 60, "Time out! Final score: $score")
        }
    }

    fun checkAnswer(chosen: Int) {
        onPlayTap()
        if (chosen == correctAnswer()) {
            score += 20
            onScoreUpdate(score)
            if (round >= 6) {
                onGameOver(score, true, "Math Genius! All 6 equations cleared!")
            } else {
                round++
            }
        } else {
            onGameOver(score, false, "Incorrect! The right answer was ${correctAnswer()}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Round $round/6 | Time: ${timer}s", color = if (timer <= 3) NeonPink else GamingGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Equation Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.5.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 12.dp)
        ) {
            Text(
                text = "$num1 $op $num2 = ?",
                color = TextPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4 Options Grid
        Column(modifier = Modifier.fillMaxWidth(0.85f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                options.take(2).forEach { opt ->
                    Button(
                        onClick = { checkAnswer(opt) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161E30)),
                        border = BorderStroke(1.dp, GamingCardBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("$opt", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                options.drop(2).forEach { opt ->
                    Button(
                        onClick = { checkAnswer(opt) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161E30)),
                        border = BorderStroke(1.dp, GamingCardBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("$opt", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
        }
    }
}

// --- 2. NUMBER GUESS (HOT / COLD) ---
@Composable
fun NumberGuessGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val secret = remember { Random.nextInt(1, 100) }
    var currentGuess by remember { mutableIntStateOf(50) }
    var attemptsLeft by remember { mutableIntStateOf(6) }
    var hint by remember { mutableStateOf("Guess a number between 1 and 99!") }

    fun submitGuess() {
        if (attemptsLeft <= 0) return
        onPlayTap()
        attemptsLeft--

        if (currentGuess == secret) {
            val score = attemptsLeft * 25 + 50
            onScoreUpdate(score)
            onGameOver(score, true, "Spot on! Secret number was $secret!")
        } else if (attemptsLeft == 0) {
            onGameOver(0, false, "Out of attempts! Secret number was $secret")
        } else if (currentGuess < secret) {
            hint = "📈 HIGHER! (Greater than $currentGuess)"
        } else {
            hint = "📉 LOWER! (Less than $currentGuess)"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Attempts Left: $attemptsLeft", color = GamingGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = hint, color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "$currentGuess",
            color = TextPrimary,
            fontSize = 60.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Slider / adjustment buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { currentGuess = (currentGuess - 10).coerceAtLeast(1) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface)
            ) { Text("-10") }
            Button(
                onClick = { currentGuess = (currentGuess - 1).coerceAtLeast(1) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface)
            ) { Text("-1") }
            Button(
                onClick = { currentGuess = (currentGuess + 1).coerceAtMost(99) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface)
            ) { Text("+1") }
            Button(
                onClick = { currentGuess = (currentGuess + 10).coerceAtMost(99) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface)
            ) { Text("+10") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { submitGuess() },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(48.dp)
        ) {
            Text("SUBMIT GUESS 🎯", fontWeight = FontWeight.Bold)
        }
    }
}

// --- 3. SEQUENCE PUZZLE ---
@Composable
fun SequencePuzzleGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    data class Puzzle(val sequence: String, val answer: Int, val choices: List<Int>)
    val puzzles = listOf(
        Puzzle("2, 4, 8, 16, ?", 32, listOf(24, 30, 32, 36)),
        Puzzle("5, 10, 15, 20, ?", 25, listOf(22, 25, 28, 30)),
        Puzzle("1, 4, 9, 16, ?", 25, listOf(20, 24, 25, 36)),
        Puzzle("100, 90, 80, 70, ?", 60, listOf(50, 55, 60, 65)),
        Puzzle("3, 6, 12, 24, ?", 48, listOf(36, 42, 48, 52))
    )
    var puzzleIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }

    val current = puzzles[puzzleIdx]

    fun pickChoice(c: Int) {
        onPlayTap()
        if (c == current.answer) {
            score += 25
            onScoreUpdate(score)
            if (puzzleIdx >= puzzles.size - 1) {
                onGameOver(score, true, "Sequence Master! All patterns solved!")
            } else {
                puzzleIdx++
            }
        } else {
            onGameOver(score, false, "Wrong number! Pattern was interrupted.")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Puzzle ${puzzleIdx + 1}/${puzzles.size}: Find the Missing Number", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Black)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.5.dp, NeonPurple),
            modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 12.dp)
        ) {
            Text(
                text = current.sequence,
                color = GamingGold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            current.choices.forEach { choice ->
                Button(
                    onClick = { pickChoice(choice) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121828)),
                    border = BorderStroke(1.dp, GamingCardBorder),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("$choice", color = NeonCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
