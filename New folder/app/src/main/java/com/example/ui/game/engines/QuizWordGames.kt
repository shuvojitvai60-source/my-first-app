package com.example.ui.game.engines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateListOf
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

// --- 1. WORD GUESS / HANGMAN ---
@Composable
fun WordGuessGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val words = listOf("GAMER", "ROBOT", "CYBER", "LASER", "PIXEL", "TURBO")
    val secretWord = remember { words.random() }
    val guessedLetters = remember { mutableStateListOf<Char>() }
    var livesLeft by remember { mutableIntStateOf(6) }
    var score by remember { mutableIntStateOf(0) }

    fun guessLetter(letter: Char) {
        if (guessedLetters.contains(letter) || livesLeft <= 0) return
        onPlayTap()
        guessedLetters.add(letter)

        if (!secretWord.contains(letter)) {
            livesLeft--
            if (livesLeft == 0) {
                onGameOver(0, false, "Word was $secretWord! Out of lives!")
            }
        } else {
            score += 15
            onScoreUpdate(score)
            if (secretWord.all { guessedLetters.contains(it) }) {
                score += 50
                onScoreUpdate(score)
                onGameOver(score, true, "Spectacular! Word '$secretWord' Solved!")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Lives: ${"❤️".repeat(livesLeft)}", color = NeonPink, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(28.dp))

        // Revealed Word letters
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            secretWord.forEach { ch ->
                val isGuessed = guessedLetters.contains(ch)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF161E30)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGuessed) "$ch" else "_",
                        color = if (isGuessed) NeonCyan else TextSecondary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Virtual Letter Keyboard (A-Z)
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            alphabet.chunked(7).forEach { rowLetters ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowLetters.forEach { char ->
                        val used = guessedLetters.contains(char)
                        Button(
                            onClick = { guessLetter(char) },
                            enabled = !used && livesLeft > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (used) Color.Gray.copy(alpha = 0.2f) else GamingSurface
                            ),
                            border = BorderStroke(1.dp, if (used) Color.Transparent else GamingCardBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(38.dp, 42.dp)
                        ) {
                            Text("$char", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (used) Color.Gray else TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

// --- 2. MULTI-QUESTION TRIVIA / QUIZ / EMOJI GUESS ---
data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
fun TriviaQuizGame(
    topicTitle: String = "Trivia",
    questions: List<QuizQuestion>,
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var timer by remember { mutableIntStateOf(15) }

    val currentQ = questions.getOrElse(qIndex) { questions[0] }

    LaunchedEffect(qIndex) {
        timer = 15
        while (timer > 0) {
            delay(1000)
            timer--
        }
        if (timer == 0) {
            if (qIndex < questions.size - 1) {
                qIndex++
            } else {
                onGameOver(score, score >= 40, "Time out! Quiz finished. Final: $score")
            }
        }
    }

    fun answer(selected: Int) {
        onPlayTap()
        if (selected == currentQ.correctIndex) {
            score += 25
            onScoreUpdate(score)
        }

        if (qIndex < questions.size - 1) {
            qIndex++
        } else {
            val isWin = score >= (questions.size * 25 / 2)
            onGameOver(score, isWin, "Quiz Complete! You answered with $score points!")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Question ${qIndex + 1}/${questions.size} | Time: ${timer}s",
            color = if (timer <= 3) NeonPink else GamingGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.5.dp, NeonPurple),
            modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 12.dp)
        ) {
            Text(
                text = currentQ.prompt,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth(0.9f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentQ.options.forEachIndexed { idx, opt ->
                Button(
                    onClick = { answer(idx) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C2E)),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = opt, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }
    }
}
