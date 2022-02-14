package com.example.exercise2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class ApplicationState(val navController: NavHostController)
//Constructor of class that takes a navController obj as param
{
    fun navigateBack() //this method pops the last element from the stack that contains the windows
    // of our app. When we go back, we pop the stack so we can get the previous window
    {
        navController.popBackStack()
    }
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    ApplicationState(navController)
}