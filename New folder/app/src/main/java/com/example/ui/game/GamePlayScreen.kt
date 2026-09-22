package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundManager
import com.example.data.GameRegistry
import com.example.model.Game
import com.example.model.GameType
import com.example.storage.GamePreferences
import com.example.ui.components.GameOverDialog
import com.example.ui.game.engines.*
import com.example.ui.theme.GamingCardBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun GamePlayScreen(
    gameId: Int,
    gamePrefs: GamePreferences,
    soundManager: GameSoundManager,
    onBackToGames: () -> Unit
) {
    val game = remember(gameId) { GameRegistry.getGame(gameId) }

    if (game == null) {
        // Friendly error state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GamingDarkBackground)
                .statusBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚠️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sorry, this game could not be loaded.",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBackToGames,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("BACK TO GAMES", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    var score by remember { mutableIntStateOf(0) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isWin by remember { mutableStateOf(false) }
    var gameOverMsg by remember { mutableStateOf<String?>(null) }
    var restartKey by remember { mutableIntStateOf(0) }

    val soundEnabled by gamePrefs.soundEnabled.collectAsState()
    val vibrationEnabled by gamePrefs.vibrationEnabled.collectAsState()
    val bestScore = remember(gameId, isGameOver) { gamePrefs.getBestScore(gameId) }

    // Game Timer
    LaunchedEffect(isPaused, isGameOver, restartKey) {
        while (!isPaused && !isGameOver) {
            delay(1000)
            secondsElapsed++
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    fun handleGameOver(finalScore: Int, won: Boolean, message: String) {
        score = finalScore
        isWin = won
        gameOverMsg = message
        isGameOver = true
        gamePrefs.recordGameResult(game.id, finalScore, won)
        if (won) {
            soundManager.playWin(soundEnabled)
        } else {
            soundManager.playGameOver(soundEnabled)
        }
        soundManager.vibrate(vibrationEnabled, 80)
    }

    fun restartGame() {
        score = 0
        secondsElapsed = 0
        isPaused = false
        isGameOver = false
        gameOverMsg = null
        restartKey++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GamingSurface)
                .border(1.dp, GamingCardBorder.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToGames,
                modifier = Modifier.testTag("game_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonCyan
                )
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text(
                    text = "${game.number}: ${game.name}",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Best: $bestScore",
                    color = GamingGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Live Score & Timer
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = "Score: $score",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Timer: $timerString",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // GAME PLAY AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PAUSED", color = NeonCyan, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isPaused = false },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("RESUME", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                key(restartKey) {
                    GameEngineDispatcher(
                        game = game,
                        onScoreUpdate = { newScore ->
                            score = newScore
                            soundManager.playScore(soundEnabled)
                            soundManager.vibrate(vibrationEnabled, 30)
                        },
                        onGameOver = { fScore, won, msg ->
                            handleGameOver(fScore, won, msg)
                        },
                        onPlayTap = {
                            soundManager.playTap(soundEnabled)
                            soundManager.vibrate(vibrationEnabled, 20)
                        }
                    )
                }
            }
        }

        // BOTTOM AREA CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GamingSurface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { restartGame() },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingCardBorder),
                modifier = Modifier.weight(1f).height(42.dp).testTag("bottom_restart_btn")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeonCyan)
                Spacer(modifier = Modifier.size(4.dp))
                Text("RESTART", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedButton(
                onClick = { isPaused = !isPaused },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingCardBorder),
                modifier = Modifier.weight(1f).height(42.dp).testTag("bottom_pause_btn")
            ) {
                Icon(
                    if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = GamingGold
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(if (isPaused) "RESUME" else "PAUSE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedButton(
                onClick = onBackToGames,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingCardBorder),
                modifier = Modifier.weight(1f).height(42.dp).testTag("bottom_home_btn")
            ) {
                Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeonPurple)
                Spacer(modifier = Modifier.size(4.dp))
                Text("HOME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }

    // GAME OVER DIALOG
    if (isGameOver) {
        GameOverDialog(
            score = score,
            bestScore = maxOf(bestScore, score),
            isWin = isWin,
            customMessage = gameOverMsg,
            onPlayAgain = { restartGame() },
            onHome = onBackToGames
        )
    }
}

@Composable
fun GameEngineDispatcher(
    game: Game,
    onScoreUpdate: (Int) -> Unit,
    onGameOver: (score: Int, isWin: Boolean, message: String) -> Unit,
    onPlayTap: () -> Unit
) {
    when (game.gameType) {
        GameType.TIC_TAC_TOE -> TicTacToeGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.ROCK_PAPER_SCISSORS -> RockPaperScissorsGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.CONNECT_FOUR -> ConnectFourGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.GAME_2048, GameType.BLOCK_PUZZLE, GameType.SLIDING_PUZZLE, GameType.CHECKERS, GameType.CHESS_PUZZLE ->
            Game2048(onScoreUpdate, onGameOver, onPlayTap)
        GameType.MINESWEEPER -> MinesweeperGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.SNAKE -> SnakeGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.FLAPPY_BIRD, GameType.ENDLESS_RUNNER, GameType.JUMP_GAME, GameType.PLATFORM_JUMPER, GameType.SPACE_RUNNER ->
            FlappyBirdGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.BRICK_BREAKER, GameType.TABLE_TENNIS, GameType.AIR_HOCKEY, GameType.TENNIS_HIT ->
            BrickBreakerGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.SPACE_SHOOTER, GameType.ASTEROID_DODGE, GameType.TOWER_DEFENSE ->
            SpaceShooterGame(isAlienTheme = false, onScoreUpdate, onGameOver, onPlayTap)
        GameType.ALIEN_SHOOTER, GameType.ZOMBIE_ESCAPE ->
            SpaceShooterGame(isAlienTheme = true, onScoreUpdate, onGameOver, onPlayTap)
        GameType.MINI_RACING, GameType.CAR_DODGE ->
            LaneRacingGame("🏎️", "🛢️", game.name, onScoreUpdate, onGameOver, onPlayTap)
        GameType.TRAFFIC_DODGE, GameType.CAR_PARKING ->
            LaneRacingGame("🚗", "🚚", game.name, onScoreUpdate, onGameOver, onPlayTap)
        GameType.BIKE_RACING ->
            LaneRacingGame("🏍️", "🚧", game.name, onScoreUpdate, onGameOver, onPlayTap)
        GameType.BOAT_RACING ->
            LaneRacingGame("🚤", "🪨", game.name, onScoreUpdate, onGameOver, onPlayTap)
        GameType.PLANE_DODGE ->
            LaneRacingGame("✈️", "🌩️", game.name, onScoreUpdate, onGameOver, onPlayTap)
        GameType.BASKETBALL_SHOT, GameType.BASKETBALL_DUNK, GameType.BOWLING, GameType.ARCHERY, GameType.GOLF_MINI ->
            BasketballGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.FOOTBALL_PENALTY, GameType.FOOTBALL_GOAL ->
            FootballPenaltyGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.CRICKET_BATTING, GameType.CRICKET_SIX ->
            CricketGame(isBowling = false, onScoreUpdate, onGameOver, onPlayTap)
        GameType.CRICKET_TARGET, GameType.CRICKET_BOWLING ->
            CricketGame(isBowling = true, onScoreUpdate, onGameOver, onPlayTap)
        GameType.KNIFE_TARGET, GameType.KNIFE_THROW ->
            KnifeThrowGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.BALLOON_POP ->
            PopAndSliceGame("🎈", onScoreUpdate, onGameOver, onPlayTap)
        GameType.BUBBLE_POP, GameType.BUBBLE_SHOOTER ->
            PopAndSliceGame("🫧", onScoreUpdate, onGameOver, onPlayTap)
        GameType.FRUIT_SLICE ->
            PopAndSliceGame("🍉", onScoreUpdate, onGameOver, onPlayTap)
        GameType.FISHING ->
            PopAndSliceGame("🐟", onScoreUpdate, onGameOver, onPlayTap)
        GameType.REACTION_TEST, GameType.QUICK_REACTION ->
            ReactionTestGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.SPEED_TAP, GameType.TAP_CHALLENGE, GameType.BOXING_TAP, GameType.WRESTLING_TAP, GameType.ENDLESS_TAP ->
            SpeedTapGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.TAP_THE_TARGET, GameType.CATCH_THE_BALL, GameType.COIN_COLLECTOR, GameType.TREASURE_HUNT ->
            TapTargetGame(isAvoidBomb = false, onScoreUpdate, onGameOver, onPlayTap)
        GameType.AVOID_THE_BOMB ->
            TapTargetGame(isAvoidBomb = true, onScoreUpdate, onGameOver, onPlayTap)
        GameType.LUCKY_NUMBER ->
            LuckyNumberGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.MEMORY_MATCH, GameType.MEMORY_CARDS ->
            MemoryMatchGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.SIMON_SAYS ->
            SimonSaysGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.TOWER_STACK, GameType.STACK_BLOCKS, GameType.COLOR_SWITCH, GameType.MATCH_3 ->
            TowerStackGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.FIND_THE_ODD_ONE, GameType.FIND_THE_DIFFERENCE, GameType.COLOR_MATCH, GameType.COLOR_MEMORY,
        GameType.PATTERN_MATCH, GameType.SHAPE_MATCH, GameType.MAZE_ESCAPE ->
            FindOddOneGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.QUICK_MATH, GameType.MATH_CHALLENGE ->
            QuickMathGame("ALL", onScoreUpdate, onGameOver, onPlayTap)
        GameType.ADDITION_CHALLENGE ->
            QuickMathGame("ADD", onScoreUpdate, onGameOver, onPlayTap)
        GameType.MULTIPLICATION_CHALLENGE ->
            QuickMathGame("MULT", onScoreUpdate, onGameOver, onPlayTap)
        GameType.NUMBER_GUESS, GameType.NUMBER_PUZZLE ->
            NumberGuessGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.SEQUENCE_PUZZLE, GameType.SUDOKU, GameType.LOGIC_PUZZLE, GameType.IQ_CHALLENGE, GameType.BRAIN_TEST, GameType.MEMORY_NUMBERS ->
            SequencePuzzleGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.WORD_GUESS, GameType.HANGMAN, GameType.WORD_SCRAMBLE, GameType.WORD_SEARCH, GameType.CROSSWORD ->
            WordGuessGame(onScoreUpdate, onGameOver, onPlayTap)
        GameType.EMOJI_GUESS ->
            TriviaQuizGame(
                "Emoji Guess",
                listOf(
                    QuizQuestion("Guess the movie: 🍿 🎬", listOf("Cinema Night", "Fast & Furious", "Space Jam", "Toy Story"), 0),
                    QuizQuestion("Guess the hero: 🕷️ 🕸️", listOf("Batman", "Iron Man", "Spider-Man", "Superman"), 2),
                    QuizQuestion("Guess the food: 🍕 🧀", listOf("Pizza", "Taco", "Sushi", "Burger"), 0),
                    QuizQuestion("Guess the vehicle: 🚀 🌌", listOf("Airplane", "Rocket", "Helicopter", "Submarine"), 1)
                ),
                onScoreUpdate, onGameOver, onPlayTap
            )
        GameType.FLAG_GUESS ->
            TriviaQuizGame(
                "Flag Guess",
                listOf(
                    QuizQuestion("Which country has this flag: 🇯🇵 ?", listOf("China", "Japan", "South Korea", "Thailand"), 1),
                    QuizQuestion("Which country has this flag: 🇧🇷 ?", listOf("Argentina", "Spain", "Brazil", "Portugal"), 2),
                    QuizQuestion("Which country has this flag: 🇨🇦 ?", listOf("Canada", "USA", "UK", "Australia"), 0),
                    QuizQuestion("Which country has this flag: 🇫🇷 ?", listOf("Italy", "Netherlands", "Germany", "France"), 3)
                ),
                onScoreUpdate, onGameOver, onPlayTap
            )
        GameType.ANIMAL_GUESS ->
            TriviaQuizGame(
                "Animal Guess",
                listOf(
                    QuizQuestion("King of the Jungle:", listOf("Tiger", "Lion", "Bear", "Elephant"), 1),
                    QuizQuestion("Tallest mammal on Earth:", listOf("Giraffe", "Zebra", "Camel", "Kangaroo"), 0),
                    QuizQuestion("Fastest land mammal:", listOf("Cheetah", "Horse", "Leopard", "Greyhound"), 0)
                ),
                onScoreUpdate, onGameOver, onPlayTap
            )
        GameType.FOOD_GUESS, GameType.MOVIE_GUESS, GameType.GENERAL_KNOWLEDGE_QUIZ, GameType.SPORTS_QUIZ, GameType.GEOGRAPHY_QUIZ, GameType.RAPID_QUIZ ->
            TriviaQuizGame(
                game.name,
                listOf(
                    QuizQuestion("How many players are in a cricket team on field?", listOf("9", "10", "11", "12"), 2),
                    QuizQuestion("What is the capital city of France?", listOf("Berlin", "Madrid", "Rome", "Paris"), 3),
                    QuizQuestion("Which planet is known as the Red Planet?", listOf("Venus", "Mars", "Jupiter", "Saturn"), 1),
                    QuizQuestion("In gaming, what does 'FPS' usually stand for?", listOf("First-Person Shooter", "Fast Player Score", "Final Power Shield", "Full Play Speed"), 0)
                ),
                onScoreUpdate, onGameOver, onPlayTap
            )
    }
}
