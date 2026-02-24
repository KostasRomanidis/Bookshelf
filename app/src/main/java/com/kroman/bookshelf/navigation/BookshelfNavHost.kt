package com.kroman.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kroman.bookshelf.presentation.ui.screens.BookDetailsScreen
import com.kroman.bookshelf.presentation.ui.screens.BooksScreen
import com.kroman.bookshelf.presentation.ui.screens.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable
object BooksScreen

@Serializable
data class BookDetails(val bookId: Int)

@Serializable
object FavoritesScreen


@Composable
fun BookshelfNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BooksScreen,
        modifier = modifier
    ) {
        composable<BooksScreen> {
            BooksScreen(onNavigateToDetails = {
                navController.navigate(BookDetails(it.id))
            })
        }
        composable<BookDetails> { backStackEntry ->
            val book: BookDetails = backStackEntry.toRoute()
            BookDetailsScreen(book.bookId)
        }
        composable<FavoritesScreen> {
            FavoritesScreen(onNavigateToDetails = {
                navController.navigate(BookDetails(it.id))
            })
        }
    }
}
