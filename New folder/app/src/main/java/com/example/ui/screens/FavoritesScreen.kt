package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundManager
import com.example.data.GameRegistry
import com.example.storage.GamePreferences
import com.example.ui.components.GameCard
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    gamePrefs: GamePreferences,
    soundManager: GameSoundManager,
    onGameSelected: (Int) -> Unit
) {
    val favorites by gamePrefs.favoritesFlow.collectAsState()
    val soundEnabled by gamePrefs.soundEnabled.collectAsState()
    val vibrationEnabled by gamePrefs.vibrationEnabled.collectAsState()

    val favGames = remember(favorites) {
        GameRegistry.games.filter { favorites.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "FAVORITE GAMES",
                color = NeonCyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "${favGames.size} saved titles",
                color = GamingGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (favGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorites yet!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any game card in the home screen to quickly access your favorite titles here.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favGames, key = { it.id }) { game ->
                    val best = gamePrefs.getBestScore(game.id)
                    GameCard(
                        game = game,
                        isFavorite = true,
                        bestScore = best,
                        onPlayClick = {
                            soundManager.playTap(soundEnabled)
                            soundManager.vibrate(vibrationEnabled, 25)
                            onGameSelected(game.id)
                        },
                        onFavoriteToggle = {
                            soundManager.playTap(soundEnabled)
                            soundManager.vibrate(vibrationEnabled, 20)
                            gamePrefs.toggleFavorite(game.id)
                        }
                    )
                }
            }
        }
    }
}
