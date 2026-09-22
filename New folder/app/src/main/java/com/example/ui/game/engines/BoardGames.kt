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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

// --- 1. TIC TAC TOE ---
@Composable
fun TicTacToeGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val board = remember { mutableStateListOf("", "", "", "", "", "", "", "", "") }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var movesCount by remember { mutableIntStateOf(0) }

    fun checkWin(symbol: String): Boolean {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return lines.any { it.all { index -> board[index] == symbol } }
    }

    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn) {
            delay(400)
            val emptyIndices = board.indices.filter { board[it].isEmpty() }
            if (emptyIndices.isNotEmpty()) {
                val botMove = emptyIndices.random()
                board[botMove] = "O"
                movesCount++
                if (checkWin("O")) {
                    onGameOver(score, false, "Bot won this round!")
                } else if (movesCount >= 9) {
                    onGameOver(score, false, "It's a draw!")
                } else {
                    isPlayerTurn = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isPlayerTurn) "Your Turn (X)" else "Bot Thinking (O)...",
            color = if (isPlayerTurn) NeonCyan else NeonPurple,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val cellValue = board[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F1420))
                                    .border(1.dp, GamingCardBorder, RoundedCornerShape(12.dp))
                                    .clickable(enabled = cellValue.isEmpty() && isPlayerTurn) {
                                        onPlayTap()
                                        board[index] = "X"
                                        movesCount++
                                        if (checkWin("X")) {
                                            score += 100
                                            onScoreUpdate(score)
                                            onGameOver(score, true, "Spectacular Victory!")
                                        } else if (movesCount >= 9) {
                                            onGameOver(score, false, "Match Tied!")
                                        } else {
                                            isPlayerTurn = false
                                        }
                                    }
                                    .testTag("ttt_cell_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cellValue,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (cellValue == "X") NeonCyan else NeonPink
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. ROCK PAPER SCISSORS ---
@Composable
fun RockPaperScissorsGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val options = listOf("✊ Rock", "✋ Paper", "✌️ Scissors")
    var playerChoice by remember { mutableStateOf<String?>(null) }
    var botChoice by remember { mutableStateOf<String?>(null) }
    var resultText by remember { mutableStateOf("Choose your weapon!") }
    var winStreak by remember { mutableIntStateOf(0) }
    var roundCount by remember { mutableIntStateOf(0) }

    fun playRound(choice: String) {
        onPlayTap()
        playerChoice = choice
        val bot = options.random()
        botChoice = bot
        roundCount++

        val p = choice.substring(3)
        val b = bot.substring(3)

        if (p == b) {
            resultText = "Tie! Go again!"
        } else if ((p == "Rock" && b == "Scissors") ||
            (p == "Paper" && b == "Rock") ||
            (p == "Scissors" && b == "Paper")
        ) {
            winStreak++
            val score = winStreak * 25
            onScoreUpdate(score)
            resultText = "You win this clash! Streak: $winStreak"
            if (winStreak >= 5) {
                onGameOver(score, true, "Master of Rock Paper Scissors!")
            }
        } else {
            val finalScore = winStreak * 25
            resultText = "Bot counter-struck!"
            if (roundCount >= 3) {
                onGameOver(finalScore, winStreak >= 2, "Final Streak: $winStreak")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Win Streak: $winStreak",
            color = GamingGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("YOU", color = NeonCyan, fontWeight = FontWeight.Bold)
                Text(
                    playerChoice?.substring(0, 2) ?: "❓",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Text("VS", color = TextSecondary, fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterVertically))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("BOT", color = NeonPink, fontWeight = FontWeight.Bold)
                Text(
                    botChoice?.substring(0, 2) ?: "❓",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resultText,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { opt ->
            Button(
                onClick = { playRound(opt) },
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(52.dp)
                    .padding(vertical = 4.dp)
                    .testTag("rps_${opt.substring(3)}")
            ) {
                Text(opt, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

// --- 3. CONNECT FOUR ---
@Composable
fun ConnectFourGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val rows = 6
    val cols = 7
    val grid = remember { mutableStateListOf(*Array(rows * cols) { 0 }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }

    fun checkWin(p: Int): Boolean {
        // Horizontal
        for (r in 0 until rows) {
            for (c in 0 until cols - 3) {
                if ((0..3).all { grid[r * cols + (c + it)] == p }) return true
            }
        }
        // Vertical
        for (r in 0 until rows - 3) {
            for (c in 0 until cols) {
                if ((0..3).all { grid[(r + it) * cols + c] == p }) return true
            }
        }
        // Diagonal
        for (r in 0 until rows - 3) {
            for (c in 0 until cols - 3) {
                if ((0..3).all { grid[(r + it) * cols + (c + it)] == p }) return true
            }
        }
        for (r in 3 until rows) {
            for (c in 0 until cols - 3) {
                if ((0..3).all { grid[(r - it) * cols + (c + it)] == p }) return true
            }
        }
        return false
    }

    fun dropInCol(col: Int, player: Int): Boolean {
        for (r in rows - 1 downTo 0) {
            val idx = r * cols + col
            if (grid[idx] == 0) {
                grid[idx] = player
                return true
            }
        }
        return false
    }

    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn) {
            delay(400)
            val validCols = (0 until cols).filter { c -> grid[c] == 0 }
            if (validCols.isNotEmpty()) {
                val col = validCols.random()
                dropInCol(col, 2)
                if (checkWin(2)) {
                    onGameOver(score, false, "Yellow AI connected four!")
                } else if ((0 until cols).none { grid[it] == 0 }) {
                    onGameOver(score, false, "Board is full!")
                } else {
                    isPlayerTurn = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isPlayerTurn) "Your Turn (Cyan)" else "AI Thinking (Yellow)...",
            color = if (isPlayerTurn) NeonCyan else GamingGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.5.dp, NeonPurple.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0 until cols) {
                            val cell = grid[r * cols + c]
                            val color = when (cell) {
                                1 -> NeonCyan
                                2 -> GamingGold
                                else -> Color(0xFF0D111A)
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, GamingCardBorder, CircleShape)
                                    .clickable(enabled = isPlayerTurn && grid[c] == 0) {
                                        onPlayTap()
                                        if (dropInCol(c, 1)) {
                                            if (checkWin(1)) {
                                                score += 150
                                                onScoreUpdate(score)
                                                onGameOver(score, true, "You connected 4 tokens!")
                                            } else if ((0 until cols).none { grid[it] == 0 }) {
                                                onGameOver(score, false, "Board is full!")
                                            } else {
                                                isPlayerTurn = false
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 4. 2048 GAME ---
@Composable
fun Game2048(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val board = remember { mutableStateListOf(*Array(16) { 0 }) }
    var score by remember { mutableIntStateOf(0) }

    fun addRandomTile() {
        val empties = board.indices.filter { board[it] == 0 }
        if (empties.isNotEmpty()) {
            val idx = empties.random()
            board[idx] = if (Random.nextFloat() < 0.9f) 2 else 4
        }
    }

    LaunchedEffect(Unit) {
        board.fill(0)
        addRandomTile()
        addRandomTile()
    }

    fun slideRow(row: List<Int>): Pair<List<Int>, Int> {
        val nonZero = row.filter { it != 0 }.toMutableList()
        var earned = 0
        val result = mutableListOf<Int>()
        var i = 0
        while (i < nonZero.size) {
            if (i + 1 < nonZero.size && nonZero[i] == nonZero[i + 1]) {
                val merged = nonZero[i] * 2
                earned += merged
                result.add(merged)
                i += 2
            } else {
                result.add(nonZero[i])
                i += 1
            }
        }
        while (result.size < 4) {
            result.add(0)
        }
        return Pair(result, earned)
    }

    fun moveLeft() {
        onPlayTap()
        var moved = false
        var roundScore = 0
        for (r in 0..3) {
            val row = (0..3).map { board[r * 4 + it] }
            val (newRow, earned) = slideRow(row)
            roundScore += earned
            for (c in 0..3) {
                if (board[r * 4 + c] != newRow[c]) moved = true
                board[r * 4 + c] = newRow[c]
            }
        }
        if (moved) {
            score += roundScore
            onScoreUpdate(score)
            addRandomTile()
            if (board.contains(2048)) {
                onGameOver(score, true, "You reached 2048!")
            }
        }
    }

    fun moveRight() {
        onPlayTap()
        var moved = false
        var roundScore = 0
        for (r in 0..3) {
            val row = (0..3).map { board[r * 4 + it] }.reversed()
            val (newRow, earned) = slideRow(row)
            roundScore += earned
            val revBack = newRow.reversed()
            for (c in 0..3) {
                if (board[r * 4 + c] != revBack[c]) moved = true
                board[r * 4 + c] = revBack[c]
            }
        }
        if (moved) {
            score += roundScore
            onScoreUpdate(score)
            addRandomTile()
        }
    }

    fun moveUp() {
        onPlayTap()
        var moved = false
        var roundScore = 0
        for (c in 0..3) {
            val col = (0..3).map { board[it * 4 + c] }
            val (newCol, earned) = slideRow(col)
            roundScore += earned
            for (r in 0..3) {
                if (board[r * 4 + c] != newCol[r]) moved = true
                board[r * 4 + c] = newCol[r]
            }
        }
        if (moved) {
            score += roundScore
            onScoreUpdate(score)
            addRandomTile()
        }
    }

    fun moveDown() {
        onPlayTap()
        var moved = false
        var roundScore = 0
        for (c in 0..3) {
            val col = (0..3).map { board[it * 4 + c] }.reversed()
            val (newCol, earned) = slideRow(col)
            roundScore += earned
            val revBack = newCol.reversed()
            for (r in 0..3) {
                if (board[r * 4 + c] != revBack[r]) moved = true
                board[r * 4 + c] = revBack[r]
            }
        }
        if (moved) {
            score += roundScore
            onScoreUpdate(score)
            addRandomTile()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0..3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0..3) {
                            val v = board[r * 4 + c]
                            val tileColor = when (v) {
                                2 -> Color(0xFF1E293B)
                                4 -> Color(0xFF334155)
                                8 -> Color(0xFF0284C7)
                                16 -> Color(0xFF0369A1)
                                32 -> Color(0xFF0D9488)
                                64 -> Color(0xFF059669)
                                128 -> Color(0xFFD97706)
                                256 -> Color(0xFFEA580C)
                                512 -> Color(0xFFDC2626)
                                1024 -> NeonPurple
                                2048 -> GamingGold
                                else -> Color(0xFF0F1420)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tileColor),
                                contentAlignment = Alignment.Center
                            ) {
                                if (v > 0) {
                                    Text(
                                        text = "$v",
                                        color = TextPrimary,
                                        fontSize = if (v >= 1000) 14.sp else 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Directional controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { moveUp() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                modifier = Modifier.size(54.dp, 40.dp)
            ) {
                Text("▲", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                Button(
                    onClick = { moveLeft() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(54.dp, 40.dp)
                ) {
                    Text("◀", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { moveDown() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(54.dp, 40.dp)
                ) {
                    Text("▼", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { moveRight() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.size(54.dp, 40.dp)
                ) {
                    Text("▶", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 5. MINESWEEPER ---
@Composable
fun MinesweeperGame(
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    val size = 6
    val totalMines = 5
    val mines = remember {
        val list = mutableListOf<Int>()
        while (list.size < totalMines) {
            val r = Random.nextInt(size * size)
            if (!list.contains(r)) list.add(r)
        }
        list
    }
    val revealed = remember { mutableStateListOf(*Array(size * size) { false }) }
    var score by remember { mutableIntStateOf(0) }

    fun countAdjacent(idx: Int): Int {
        val r = idx / size
        val c = idx % size
        var cnt = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until size && nc in 0 until size) {
                    if (mines.contains(nr * size + nc)) cnt++
                }
            }
        }
        return cnt
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💣 Mines: $totalMines | Safe squares left: ${size * size - totalMines - revealed.count { it }}",
            color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingSurface),
            border = BorderStroke(1.dp, GamingCardBorder),
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                for (r in 0 until size) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (c in 0 until size) {
                            val idx = r * size + c
                            val isRev = revealed[idx]
                            val isMine = mines.contains(idx)
                            val adj = if (isRev && !isMine) countAdjacent(idx) else 0

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isRev) Color(0xFF1E293B) else Color(0xFF0F1420))
                                    .border(1.dp, if (isRev) Color.Transparent else GamingCardBorder, RoundedCornerShape(6.dp))
                                    .clickable(enabled = !isRev) {
                                        onPlayTap()
                                        revealed[idx] = true
                                        if (isMine) {
                                            onGameOver(score, false, "Boom! You hit a mine!")
                                        } else {
                                            score += 15
                                            onScoreUpdate(score)
                                            val safeCount = size * size - totalMines
                                            if (revealed.count { it } >= safeCount) {
                                                score += 100
                                                onScoreUpdate(score)
                                                onGameOver(score, true, "Minefield Cleared Safely!")
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRev) {
                                    if (isMine) {
                                        Text("💣", fontSize = 16.sp)
                                    } else if (adj > 0) {
                                        Text("$adj", color = when (adj) { 1 -> NeonCyan 2 -> NeonGreen else -> NeonPink }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
