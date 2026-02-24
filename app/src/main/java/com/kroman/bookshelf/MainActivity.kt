package com.kroman.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kroman.bookshelf.navigation.BookDetails
import com.kroman.bookshelf.navigation.BookshelfNavHost
import com.kroman.bookshelf.navigation.FavoritesScreen
import com.kroman.bookshelf.presentation.ui.components.BookshelfTopAppBar
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookshelfTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val destination = backStackEntry?.destination

                val showBackButton =
                    destination?.hasRoute<BookDetails>() == true || destination?.hasRoute<FavoritesScreen>() == true
                val title = when {
                    destination?.hasRoute<BookDetails>() == true -> stringResource(R.string.book_details_title)
                    destination?.hasRoute<FavoritesScreen>() == true -> stringResource(R.string.favorites_title)
                    else -> stringResource(R.string.app_name)
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        BookshelfTopAppBar(
                            title = title,
                            showBackButton = showBackButton,
                            navController = navController
                        )
                    }
                ) { paddingValues ->
                    BookshelfNavHost(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
