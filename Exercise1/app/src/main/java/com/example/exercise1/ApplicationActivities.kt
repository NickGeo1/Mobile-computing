package com.example.exercise1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise1.ui.Login.Login
@Composable
fun ApplicationActivities(appState: ApplicationState = rememberAppState())
{
    NavHost(
        navController = appState.navController, //navigation object
        startDestination = "login"  //route of first window destination
    ){
        composable(route = "login") //all the windows-destinations are here as composables
        {
            Login(navController = appState.navController)
        }
//        composable(route = "main")
//        {
//            Home(navController = appState.navController)
//        }
//        composable(route="profile")
//        {
//            Payment(onBackPress = appState::navigateBack) //take the method appState.navigateBack() as param
//        }

    }
}
