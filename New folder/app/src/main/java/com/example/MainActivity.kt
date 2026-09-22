package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.GameSoundManager
import com.example.storage.GamePreferences
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavScreen
import com.example.ui.game.GamePlayScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.ShuvojitGamingTheme

class MainActivity : ComponentActivity() {

    private lateinit var gamePrefs: GamePreferences
    private lateinit var soundManager: GameSoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        gamePrefs = GamePreferences(applicationContext)
        soundManager = GameSoundManager(applicationContext)

        setContent {
            ShuvojitGamingTheme {
                MainApp(gamePrefs = gamePrefs, soundManager = soundManager)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun MainApp(
    gamePrefs: GamePreferences,
    soundManager: GameSoundManager
) {
    var currentTab by remember { mutableStateOf(NavScreen.HOME) }
    var activeGameId by remember { mutableStateOf<Int?>(null) }

    // System Back Press handling
    BackHandler(enabled = activeGameId != null || currentTab != NavScreen.HOME) {
        if (activeGameId != null) {
            activeGameId = null
        } else if (currentTab != NavScreen.HOME) {
            currentTab = NavScreen.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
    ) {
        if (activeGameId != null) {
            // Full Screen Game Play Mode
            GamePlayScreen(
                gameId = activeGameId!!,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onBackToGames = { activeGameId = null }
            )
        } else {
            // Main App Navigation Shell
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = GamingDarkBackground,
                bottomBar = {
                    BottomNavBar(
                        currentScreen = currentTab,
                        onScreenSelected = { selected ->
                            soundManager.playTap(true)
                            currentTab = selected
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { tab ->
                        when (tab) {
                            NavScreen.HOME, NavScreen.GAMES -> HomeScreen(
                                gamePrefs = gamePrefs,
                                soundManager = soundManager,
                                onGameSelected = { id -> activeGameId = id }
                            )
                            NavScreen.FAVORITES -> FavoritesScreen(
                                gamePrefs = gamePrefs,
                                soundManager = soundManager,
                                onGameSelected = { id -> activeGameId = id }
                            )
                            NavScreen.PROFILE -> ProfileScreen(
                                gamePrefs = gamePrefs
                            )
                            NavScreen.SETTINGS -> SettingsScreen(
                                gamePrefs = gamePrefs,
                                soundManager = soundManager
                            )
                        }
                    }
                }
            }
        }
    }
}
