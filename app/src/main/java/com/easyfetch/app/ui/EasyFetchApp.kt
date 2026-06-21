package com.easyfetch.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.easyfetch.app.R
import com.easyfetch.app.data.repository.ExtractionRepository
import com.easyfetch.app.data.repository.MediaStorageRepository
import com.easyfetch.app.ui.home.HomeScreen
import com.easyfetch.app.ui.home.HomeViewModel
import com.easyfetch.app.ui.home.HomeViewModelFactory
import com.easyfetch.app.ui.library.LibraryScreen
import com.easyfetch.app.ui.library.LibraryViewModel
import com.easyfetch.app.ui.library.LibraryViewModelFactory
import com.easyfetch.app.ui.theme.ElectricCyan
import com.easyfetch.app.ui.theme.Midnight
import com.easyfetch.app.ui.theme.MidnightDeep
import com.easyfetch.app.ui.theme.MidnightSurface
import com.easyfetch.app.ui.theme.Mist
import com.easyfetch.app.ui.theme.Paper

private const val ROUTE_HOME = "home"
private const val ROUTE_LIBRARY = "library"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyFetchApp(
    extractionRepository: ExtractionRepository,
    mediaStorageRepository: MediaStorageRepository,
    sharedUrl: String?,
    onSharedUrlConsumed: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank() && currentRoute != ROUTE_HOME) {
            navController.navigate(ROUTE_HOME) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        containerColor = Midnight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(stringResource(R.string.app_name))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightDeep,
                    titleContentColor = Paper
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MidnightDeep,
                contentColor = Mist
            ) {
                NavigationBarItem(
                    selected = currentRoute == ROUTE_HOME,
                    onClick = {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDeep,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = Mist,
                        unselectedTextColor = Mist
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == ROUTE_LIBRARY,
                    onClick = {
                        navController.navigate(ROUTE_LIBRARY) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Library") },
                    label = { Text("Library") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDeep,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = Mist,
                        unselectedTextColor = Mist
                    )
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(ROUTE_HOME) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(
                        extractionRepository = extractionRepository,
                        mediaStorageRepository = mediaStorageRepository,
                        onVideoSaved = {}
                    )
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    sharedUrl = sharedUrl,
                    onSharedUrlConsumed = onSharedUrlConsumed
                )
            }
            composable(ROUTE_LIBRARY) {
                val libraryViewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModelFactory(mediaStorageRepository)
                )
                LibraryScreen(viewModel = libraryViewModel)
            }
        }
    }
}
