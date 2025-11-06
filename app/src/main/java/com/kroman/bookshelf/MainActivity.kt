package com.kroman.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kroman.bookshelf.navigation.BookshelfNavHost
import com.kroman.bookshelf.ui.theme.BookshelfTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookshelfTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()

                val showBackButton = currentBackStackEntry?.destination?.route?.contains("BookDetails") == true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = stringResource(R.string.app_name)) },
                            navigationIcon = {
                                if (showBackButton) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_back_icon),
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }
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