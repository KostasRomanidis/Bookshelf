package com.kroman.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.kroman.bookshelf.presentation.bookdetails.BookDetailsScreen
import com.kroman.bookshelf.presentation.books.BooksScreen
import kotlinx.serialization.Serializable

@Serializable
object BooksScreen

@Serializable
data class BookDetails(val bookId: Int)


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
    }
}
