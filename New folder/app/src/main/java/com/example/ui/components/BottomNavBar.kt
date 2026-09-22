package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GamingCardBorder
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

enum class NavScreen(val label: String) {
    HOME("Home"),
    GAMES("Games"),
    FAVORITES("Favorites"),
    PROFILE("Profile"),
    SETTINGS("Settings")
}

@Composable
fun BottomNavBar(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GamingSurface)
            .border(
                width = 1.dp,
                color = GamingCardBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Home",
                selected = currentScreen == NavScreen.HOME,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                testTag = "nav_home",
                onClick = { onScreenSelected(NavScreen.HOME) }
            )

            NavItem(
                label = "Games",
                selected = currentScreen == NavScreen.GAMES,
                activeIcon = Icons.Filled.PlayCircle,
                inactiveIcon = Icons.Outlined.PlayCircleOutline,
                testTag = "nav_games",
                onClick = { onScreenSelected(NavScreen.GAMES) }
            )

            NavItem(
                label = "Favorites",
                selected = currentScreen == NavScreen.FAVORITES,
                activeIcon = Icons.Filled.Favorite,
                inactiveIcon = Icons.Outlined.FavoriteBorder,
                testTag = "nav_favorites",
                onClick = { onScreenSelected(NavScreen.FAVORITES) }
            )

            NavItem(
                label = "Profile",
                selected = currentScreen == NavScreen.PROFILE,
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                testTag = "nav_profile",
                onClick = { onScreenSelected(NavScreen.PROFILE) }
            )

            NavItem(
                label = "Settings",
                selected = currentScreen == NavScreen.SETTINGS,
                activeIcon = Icons.Filled.Settings,
                inactiveIcon = Icons.Outlined.Settings,
                testTag = "nav_settings",
                onClick = { onScreenSelected(NavScreen.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = if (selected) {
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(NeonCyan.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.25f))
                        )
                    )
            } else {
                Modifier.size(32.dp)
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = if (selected) NeonCyan else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) NeonCyan else TextSecondary
        )
    }
}
