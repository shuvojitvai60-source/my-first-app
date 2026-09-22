package com.example.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.PlayerStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shuvojit_gaming_prefs", Context.MODE_PRIVATE)

    private val _favoritesFlow = MutableStateFlow(loadFavorites())
    val favoritesFlow: StateFlow<Set<Int>> = _favoritesFlow.asStateFlow()

    private val _statsFlow = MutableStateFlow(loadStats())
    val statsFlow: StateFlow<PlayerStats> = _statsFlow.asStateFlow()

    // Settings
    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _musicEnabled = MutableStateFlow(prefs.getBoolean(KEY_MUSIC, true))
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION, true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, true))
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private fun loadFavorites(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isFavorite(gameId: Int): Boolean {
        return _favoritesFlow.value.contains(gameId)
    }

    fun toggleFavorite(gameId: Int) {
        val current = _favoritesFlow.value.toMutableSet()
        if (current.contains(gameId)) {
            current.remove(gameId)
        } else {
            current.add(gameId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current.map { it.toString() }.toSet()).apply()
        _favoritesFlow.value = current
        _statsFlow.value = loadStats()
    }

    fun getBestScore(gameId: Int): Int {
        return prefs.getInt(KEY_BEST_SCORE_PREFIX + gameId, 0)
    }

    fun recordGameResult(gameId: Int, score: Int, won: Boolean) {
        val currentBest = getBestScore(gameId)
        val newBest = maxOf(currentBest, score)
        val totalPlayed = prefs.getInt(KEY_TOTAL_PLAYED, 0) + 1
        val totalWon = prefs.getInt(KEY_TOTAL_WON, 0) + if (won) 1 else 0
        val overallBest = maxOf(prefs.getInt(KEY_OVERALL_BEST, 0), score)

        prefs.edit()
            .putInt(KEY_BEST_SCORE_PREFIX + gameId, newBest)
            .putInt(KEY_TOTAL_PLAYED, totalPlayed)
            .putInt(KEY_TOTAL_WON, totalWon)
            .putInt(KEY_OVERALL_BEST, overallBest)
            .apply()

        _statsFlow.value = loadStats()
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
        _favoritesFlow.value = emptySet()
        _statsFlow.value = loadStats()
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC, enabled).apply()
        _musicEnabled.value = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        _vibrationEnabled.value = enabled
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _darkModeEnabled.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun loadStats(): PlayerStats {
        val played = prefs.getInt(KEY_TOTAL_PLAYED, 0)
        val won = prefs.getInt(KEY_TOTAL_WON, 0)
        val best = prefs.getInt(KEY_OVERALL_BEST, 0)
        val favCount = (prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()).size

        val achievements = listOf(
            Achievement("first_game", "First Game", "Played your first mini-game", "🎮", played >= 1),
            Achievement("novice_10", "10 Games Played", "Played 10 rounds of mini-games", "🥉", played >= 10),
            Achievement("master_50", "50 Games Played", "Played 50 rounds of mini-games", "🥈", played >= 50),
            Achievement("legend_100", "100 Games Played", "True gaming legend with 100 plays", "🥇", played >= 100),
            Achievement("high_score", "High Score", "Achieved a score of 100+ in any game", "🔥", best >= 100),
            Achievement("first_win", "Victory Royale", "Won your first game", "🏆", won >= 1),
            Achievement("quiz_master", "Quiz Master", "Won 5 or more challenge rounds", "🧠", won >= 5),
            Achievement("speed_player", "Speed Player", "Played 25+ matches in the gaming arena", "⚡", played >= 25)
        )

        return PlayerStats(
            gamesPlayed = played,
            gamesWon = won,
            bestScore = best,
            favoriteCount = favCount,
            achievements = achievements
        )
    }

    companion object {
        private const val KEY_FAVORITES = "user_favorites"
        private const val KEY_TOTAL_PLAYED = "total_played"
        private const val KEY_TOTAL_WON = "total_won"
        private const val KEY_OVERALL_BEST = "overall_best"
        private const val KEY_BEST_SCORE_PREFIX = "best_score_game_"
        private const val KEY_SOUND = "setting_sound"
        private const val KEY_MUSIC = "setting_music"
        private const val KEY_VIBRATION = "setting_vibration"
        private const val KEY_DARK_MODE = "setting_dark_mode"
        private const val KEY_NOTIFICATIONS = "setting_notifications"
    }
}
